package one.tranic.goldpiglin.common;

import org.bukkit.Bukkit;

public enum VersionEnum {
    UNKNOWN_VERSION,
    V1_20(1200), V1_20_1(1201), V1_20_2(1202), V1_20_3(1203), V1_20_4(1204), V1_20_5(1205), V1_20_6(1206),
    V1_21(1210), V1_21_1(1211), V1_21_2(1212), V1_21_3(1213), V1_21_4(1214);

    private static VersionEnum version = null;
    public final int versionNum;

    VersionEnum() {
        this(0);
    }

    VersionEnum(int versionNum) {
        this.versionNum = versionNum;
    }

    public static VersionEnum getVersion() {
        if (version != null) return version;
        String bukkit = Bukkit.getBukkitVersion();
        if (bukkit.contains("1.21.4")) {
            version = V1_21_4;
        } else if (bukkit.contains("1.21.3")) {
            version = V1_21_3;
        } else if (bukkit.contains("1.21.2")) {
            version = V1_21_2;
        } else if (bukkit.contains("1.21.1")) {
            version = V1_21_1;
        } else if (bukkit.contains("1.21")) {
            version = V1_21;
        } else if (bukkit.contains("1.20_6")) {
            version = V1_20_6;
        } else if (bukkit.contains("1.20_5")) {
            version = V1_20_5;
        } else if (bukkit.contains("1.20.4")) {
            version = V1_20_4;
        } else if (bukkit.contains("1.20.3")) {
            version = V1_20_3;
        } else if (bukkit.contains("1.20.2")) {
            version = V1_20_2;
        } else if (bukkit.contains("1.20.1")) {
            version = V1_20_1;
        } else if (bukkit.contains("1.20")) {
            version = V1_20;
        } else version = UNKNOWN_VERSION;
        return version;
    }
}
