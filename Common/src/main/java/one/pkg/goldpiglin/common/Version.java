package one.pkg.goldpiglin.common;

import org.bukkit.Bukkit;

@SuppressWarnings("unused")
public class Version {
    private static final String VERSION_SEPARATOR = "-";
    private static final String VERSION_DELIMITER = "\\.";
    private static final Version INSTANCE = parseVersion(Bukkit.getServer().getBukkitVersion());
    private final int major;
    private final int minor;
    private final int patch;

    private Version(int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    private static Version parseVersion(String versionString) {
        String[] versionParts = versionString.split(VERSION_SEPARATOR)[0].split(VERSION_DELIMITER);
        if (versionParts.length < 2) {
            throw new IllegalArgumentException(versionString + " is not a valid version string");
        }

        int major = Integer.parseInt(versionParts[0]);
        int minor = Integer.parseInt(versionParts[1]);
        int patch = versionParts.length > 2 ? Integer.parseInt(versionParts[2]) : 0;

        return new Version(major, minor, patch);
    }

    public static boolean isMajorVersion(int major) {
        return INSTANCE.major == major;
    }

    public static boolean isMinimumMajorVersion(int major) {
        return INSTANCE.major >= major;
    }

    public static boolean isMaximumMajorVersion(int major) {
        return INSTANCE.major <= major;
    }

    public static boolean isMinorVersion(int minor) {
        return INSTANCE.minor == minor;
    }

    public static boolean isVersion(int minor, int patch) {
        return INSTANCE.minor == minor && INSTANCE.patch == patch;
    }

    public static boolean isVersion(int major, int minor, int patch) {
        return INSTANCE.major == major && INSTANCE.minor == minor && INSTANCE.patch == patch;
    }

    public static boolean isVersionNoPatch(int major, int minor) {
        return INSTANCE.major == major && INSTANCE.minor == minor;
    }

    public static boolean isMinimumMinorVersion(int minor) {
        return INSTANCE.minor >= minor;
    }

    public static boolean isMinimumVersion(int minor, int patch) {
        return INSTANCE.minor > minor || (INSTANCE.minor == minor && INSTANCE.patch >= patch);
    }

    public static boolean isMinimumVersion(int major, int minor, int patch) {
        return INSTANCE.major >= major && INSTANCE.minor > minor || (INSTANCE.minor == minor && INSTANCE.patch >= patch);
    }

    public static boolean isMinimumVersionNoPatch(int major, int minor) {
        return INSTANCE.major >= major && INSTANCE.minor > minor;
    }

    public static boolean isMaximumMinorVersion(int minor) {
        return INSTANCE.minor <= minor;
    }

    public static boolean isMaximumVersion(int minor, int patch) {
        return INSTANCE.minor < minor || (INSTANCE.minor == minor && INSTANCE.patch <= patch);
    }

    public static boolean isMaximumVersion(int major, int minor, int patch) {
        return INSTANCE.major <= major && INSTANCE.minor < minor || (INSTANCE.minor == minor && INSTANCE.patch <= patch);
    }

    public static boolean isMaximumVersionNoPatch(int major, int minor) {
        return INSTANCE.major <= major && INSTANCE.minor < minor;
    }

    public static int getMajor() {
        return INSTANCE.major;
    }

    public static int getMinor() {
        return INSTANCE.minor;
    }

    public static int getPatch() {
        return INSTANCE.patch;
    }
}