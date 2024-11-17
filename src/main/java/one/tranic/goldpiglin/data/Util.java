package one.tranic.goldpiglin.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        return fastutil ? new it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap<>() : new HashMap<>();
    }

    public static <T> List<T> newArrayList() {
        return fastutil ? new it.unimi.dsi.fastutil.objects.ObjectArrayList<>() : new ArrayList<>();
    }
}