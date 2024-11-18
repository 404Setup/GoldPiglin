package one.tranic.goldpiglin.common.data;

import one.tranic.goldpiglin.common.config.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Util {
    private static boolean fastutil = false;

    static {
        try {
            Class.forName("it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap");
            fastutil = true;
        } catch (ClassNotFoundException ignored) {
        }
    }

    public static <K, V> Map<K, V> newHashMap() {
        return Config.isUseConcurrentMap() ? new ConcurrentHashMap<>() : (fastutil ? new it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap<>() : new HashMap<>());
    }

    public static <T> List<T> newArrayList() {
        return fastutil ? new it.unimi.dsi.fastutil.objects.ObjectArrayList<>() : new ArrayList<>();
    }

    public static <K, V> void entryForEach(Map<K, V> map, final Consumer<? super Map.Entry<K, V>> consumer) {
        if (fastutil && map instanceof it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap<K, V> fastMap) {
            fastMap.object2ObjectEntrySet().fastForEach(consumer);
        } else map.entrySet().forEach(consumer);
    }

    public static <K, V> boolean removeIf(Map<K, V> map, Predicate<? super Map.Entry<K, V>> filter) {
        return (fastutil && map instanceof it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap<K, V> fastMap) ?
                fastMap.object2ObjectEntrySet().removeIf(filter) :
                map.entrySet().removeIf(filter);
    }
}
