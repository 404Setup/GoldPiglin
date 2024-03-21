package stream.fset.plugin.goldpiglin

import kotlinx.coroutines.*

class ExpiringHashMap<K, V>(private val expirationTime: Long, private val expirationScannerTime: Long) {
    private val map = HashMap<K, V>()
    private val expirationMap = HashMap<K, Long>()

    init {
        CoroutineScope(Dispatchers.Default).launch {
            while (true) {
                delay(expirationScannerTime)
                removeExpiredEntries()
            }
        }
    }

    private fun removeExpiredEntries() {
        val currentTime = System.currentTimeMillis()
        val expiredKeys = expirationMap.filter { it.value < currentTime }.keys
        expiredKeys.forEach { remove(it) }
    }

    private fun put(key: K, value: V) {
        map[key] = value
        expirationMap[key] = System.currentTimeMillis() + expirationTime
    }

    operator fun get(key: K): V? {
        val expiration = expirationMap[key]
        if (expiration != null && System.currentTimeMillis() > expiration) {
            remove(key)
            return null
        }
        return map[key]
    }

    operator fun iterator(): Iterator<Map.Entry<K, V>> {
        val validEntries = mutableListOf<Map.Entry<K, V>>()
        val currentTime = System.currentTimeMillis()

        for ((key, value) in map) {
            val expiration = expirationMap[key]
            if (expiration != null && currentTime <= expiration) {
                validEntries.add(Entry(key, value))
            }
        }

        return validEntries.iterator()
    }

    fun filter(predicate: (Map.Entry<K, V>) -> Boolean): List<Map.Entry<K, V>> {
        val filteredEntries = mutableListOf<Map.Entry<K, V>>()
        val currentTime = System.currentTimeMillis()

        for ((key, value) in map) {
            val expiration = expirationMap[key]
            if (expiration != null && currentTime <= expiration) {
                val entry = Entry(key, value)
                if (predicate(entry)) {
                    filteredEntries.add(entry)
                }
            }
        }

        return filteredEntries
    }

    fun remove(key: K) {
        map.remove(key)
        expirationMap.remove(key)
    }

    fun containsKey(key: K): Boolean {
        return get(key) != null
    }

    fun clear() {
        map.clear()
        expirationMap.clear()
    }

    operator fun set(key: K, value: V) {
        put(key, value)
    }

    private data class Entry<K, V>(override val key: K, override val value: V) : Map.Entry<K, V>
}