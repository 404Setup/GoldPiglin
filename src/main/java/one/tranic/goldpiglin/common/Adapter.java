package one.tranic.goldpiglin.common;

import one.tranic.goldpiglin.bukkit.napi.v1_20_1.N1201_Bukkit;
import one.tranic.goldpiglin.bukkit.napi.v1_20_1.N1201_Paper;
import one.tranic.goldpiglin.bukkit.napi.v1_20_5.N1205_Bukkit;
import one.tranic.goldpiglin.bukkit.napi.v1_20_5.N1205_Paper;
import one.tranic.goldpiglin.bukkit.rtag.v1_20_1.R1201_Bukkit;
import one.tranic.goldpiglin.bukkit.rtag.v1_20_1.R1201_Paper;
import one.tranic.goldpiglin.bukkit.rtag.v1_20_5.R1205_Bukkit;
import one.tranic.goldpiglin.bukkit.rtag.v1_20_5.R1205_Paper;
import one.tranic.goldpiglin.common.config.Config;
import one.tranic.goldpiglin.paper.v1_20_1.P1201_Target;
import one.tranic.goldpiglin.paper.v1_20_6.P1206_Target;
import one.tranic.goldpiglin.paper.v1_21_3.P1213_Target;
import one.tranic.t.utils.Reflect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@SuppressWarnings("unused")
public enum Adapter {
    PAPER("Paper", null) {
        @Override
        public BaseTarget createTarget() {
            boolean is1205 = Version.isMinimumVersion(20, 5);
            boolean is1213 = Version.isMinimumVersion(21, 3);

            if (is1213) return new P1213_Target();
            return is1205 ? new P1206_Target() : new P1201_Target();
        }
    },
    NBTAPI("NBTAPI", "de.tr7zw.nbtapi.NBT") {
        @Override
        public BaseTarget createTarget() {
            boolean is1205 = Version.isMinimumVersion(20, 5);
            boolean paper = VersionUtils.isPaper();

            if (is1205) return paper ? new N1205_Paper() : new N1205_Bukkit();
            return paper ? new N1201_Paper() : new N1201_Bukkit();
        }
    },
    RTAG("Rtag", "com.saicone.rtag.RtagItem") {
        @Override
        public BaseTarget createTarget() {
            boolean is1205 = Version.isMinimumVersion(20, 5);
            boolean paper = VersionUtils.isPaper();

            if (is1205) return paper ? new R1205_Paper() : new R1205_Bukkit();
            return paper ? new R1201_Paper() : new R1201_Bukkit();
        }
    };

    private static final Adapter now = getAdapter(Config.getAdapter());
    private final String adapterName;
    private final String adapterClass;
    private final boolean isPresent;

    Adapter(String name, String adapterClass) {
        this.adapterName = name;
        this.adapterClass = adapterClass;
        this.isPresent = Objects.equals(this.adapterName, "Paper") ?
                VersionUtils.isPaper() : Reflect.hasClass(adapterClass);
    }

    public static @NotNull Adapter getAdapter() {
        return now;
    }

    public static @NotNull Adapter getAdapter(String name) {
        for (Adapter adapter : Adapter.values())
            if (adapter.adapterName.equalsIgnoreCase(name))
                return adapter;
        return NBTAPI;
    }

    public abstract BaseTarget createTarget();

    public @NotNull String getAdapterName() {
        return adapterName;
    }

    public boolean isPresent() {
        return isPresent;
    }

    public @Nullable Class<?> getAdapterClass() {
        return Reflect.getClass(adapterClass);
    }

    public @Nullable String getAdapterClassName() {
        return adapterClass;
    }
}
