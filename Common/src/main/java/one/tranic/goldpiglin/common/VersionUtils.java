package one.tranic.goldpiglin.common;

public class VersionUtils {
    private static boolean isPaper = false;

    static {
        try {
            Class.forName("io.papermc.paper.command.MSPTCommand");
            isPaper = true;
        } catch (ClassNotFoundException ignored) {
        }
    }

    public static boolean isPaper() {
        return isPaper;
    }
}
