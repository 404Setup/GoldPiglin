package one.pkg.goldpiglin.common;

import one.pkg.goldpiglin.bukkit.*;
import one.pkg.tinyutils.Reflect;
import one.pkg.tinyutils.minecraft.Platform;
import one.pkg.goldpiglin.bukkit.*;
import one.pkg.goldpiglin.common.config.Config;
import one.pkg.goldpiglin.paper.V1_20_R1_Paper;
import one.pkg.goldpiglin.paper.V1_20_R4_Paper;
import one.pkg.goldpiglin.paper.V1_21_R2_Paper;
import one.pkg.goldpiglin.paper.V26_1_Paper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

@SuppressWarnings("unused")
public enum Adapter {
    Spigot("Spigot", null) {
        @Override
        public BaseTarget createTarget() {
            if (Version.isMinimumVersionNoPatch(26, 1)) return new V26_1_Spigot();
            if (Version.isMinimumVersion(1, 21, 11)) return new V1_21_R7_Spigot();
            if (Version.isMinimumVersion(1, 21, 10)) return new V1_21_R6_Spigot();
            if (Version.isMinimumVersion(1, 21, 8)) return new V1_21_R5_Spigot();
            if (Version.isMinimumVersion(1, 21, 5)) return new V1_21_R4_Spigot();
            if (Version.isMinimumVersion(1, 21, 4)) return new V1_21_R3_Spigot();
            if (Version.isMinimumVersion(1, 21, 3)) return new V1_21_R2_Spigot();
            if (Version.isMinimumVersion(1, 21, 1)) return new V1_21_R1_Spigot();
            if (Version.isMinimumVersion(1, 20, 6)) return new V1_20_R4_Spigot();
            if (Version.isMinimumVersion(1, 20, 4)) return new V1_20_R3_Spigot();
            if (Version.isMinimumVersion(1, 20, 2)) return new V1_20_R2_Spigot();
            if (Version.isMinimumVersion(1, 20, 1)) return new V1_20_R1_Spigot();
            return null;
        }
    },
    PAPER("Paper", null) {
        @Override
        public BaseTarget createTarget() {
            if (Version.isMinimumVersionNoPatch(26, 1)) return new V26_1_Paper();
            if (Version.isMinimumVersion(21, 3)) return new V1_21_R2_Paper();
            return Version.isMinimumVersion(20, 5) ? new V1_20_R4_Paper() : new V1_20_R1_Paper();
        }
    },
    NBTAPI("NBTAPI", "de.tr7zw.nbtapi.NBT") {
        @Override
        public BaseTarget createTarget() {
            boolean paper = Platform.get() != Platform.Spigot;
            if (Version.isMinimumVersion(20, 5)) return paper ? new V1_20_R4_NAPI_Paper() : new V1_20_R4_NAPI_Bukkit();
            return paper ? new V1_20_R1_NAPI_Paper() : new V1_20_R1_NAPI_Bukkit();
        }
    },
    RTAG("Rtag", "com.saicone.rtag.RtagItem") {
        @Override
        public BaseTarget createTarget() {
            boolean paper = Platform.get() != Platform.Spigot;

            if (Version.isMinimumVersion(20, 5)) return paper ? new V1_20_R4_Rtag_Paper() : new V1_20_R4_Rtag_Bukkit();
            return paper ? new V1_20_R1_Rtag_Paper() : new V1_20_R1_Rtag_Bukkit();
        }
    };

    private static final Adapter now = getAdapter(Config.getAdapter());
    private static final Logger logger = LoggerFactory.getLogger("Adapter");

    static {
        logger.info("Minecraft version detected: {}.{}.{}", Version.getMajor(), Version.getMinor(), Version.getPatch());
    }

    private final String adapterName;
    private final String adapterClass;
    private final boolean isPresent;

    Adapter(String name, String adapterClass) {
        this.adapterName = name;
        this.adapterClass = adapterClass;
        this.isPresent = Objects.equals(this.adapterName, "Paper") ?
                Platform.get() != Platform.Spigot :
                Objects.equals(this.adapterName, "Spigot") ?
                        Platform.get() == Platform.Spigot :
                        Reflect.hasClass(adapterClass);
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
