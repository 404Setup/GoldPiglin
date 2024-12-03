package one.tranic.goldpiglin.common.data;

import one.tranic.goldpiglin.common.config.Config;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
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

    public static <K, V> Map<K, V> newHashMap(@NotNull Map<K, V> map) {
        return Config.isUseConcurrentMap() ? new ConcurrentHashMap<>(map) : (fastutil ? new it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap<>(map) : new HashMap<>(map));
    }

    public static <T> List<T> newArrayList() {
        return fastutil ? new it.unimi.dsi.fastutil.objects.ObjectArrayList<>() : new ArrayList<>();
    }

    @SafeVarargs
    public static <T> List<T> newArrayList(@NotNull T... elements) {
        if (fastutil) {
            return new it.unimi.dsi.fastutil.objects.ObjectArrayList<>(elements);
        }
        List<T> list = new ArrayList<>(elements.length);
        Collections.addAll(list, elements);
        return list;
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
