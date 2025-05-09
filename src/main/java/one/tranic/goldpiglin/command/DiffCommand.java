package one.tranic.goldpiglin.command;

import one.tranic.goldpiglin.GoldPiglin;
import one.tranic.goldpiglin.common.GoldPiglinLogger;
import one.tranic.t.utils.compress.BaseCompress;
import one.tranic.t.utils.diff.SimplePatcher;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class DiffCommand extends Command {
    private final static List<String> INTERNAL_ERROR = List.of("Internal error");
    private final static Path DIFF_DIR = GoldPiglin.getPlugin().getDataFolder().toPath().getParent().resolve("diff");
    private final static Path Plugin_DIR = GoldPiglin.getPlugin().getDataFolder().toPath().getParent();

    public DiffCommand() {
        super("diff");
        this.setUsage("/diff <create> jar1 jar2 | /diff <merge> patch jar");

        if (!DIFF_DIR.toFile().exists()) {
            try {
                Files.createDirectories(DIFF_DIR);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static List<String> getFiles(String prefix) {
        try (final Stream<Path> files = Files.list(Plugin_DIR)) {
            return files.filter(path ->
                            path.toString().endsWith("." + prefix) && path.toFile().isFile()
                    )
                    .map(Path::getFileName)
                    .map(Path::toString).toList();
        } catch (IOException e) {
            e.printStackTrace();
            return INTERNAL_ERROR;
        }
    }

    public static List<String> getJarFiles() {
        return getFiles("jar");
    }

    public static List<String> getDiffFiles() {
        return getFiles("diff");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, String[] args) {
        if (args.length == 0) {
            return true;
        }
        if (args[0].equalsIgnoreCase("create")) {
            if (args.length < 3) {
                sender.sendMessage("Usage: /diff create <jar1> <jar2>");
                return true;
            }
            return runCreateDiff(sender, commandLabel, args);
        }
        if (args[0].equalsIgnoreCase("merge")) {
            if (args.length < 3) {
                sender.sendMessage("Usage: /diff merge <patch> <jar>");
                return true;
            }
            return runMergeDiff(sender, commandLabel, args);
        }
        return true;
    }

    private boolean runCreateDiff(@NotNull CommandSender sender, @NotNull String commandLabel, String[] args) {
        var jar1 = args[1];
        var jar2 = args[2];

        var j1 = Plugin_DIR.resolve(jar1).toFile();
        var j2 = Plugin_DIR.resolve(jar2).toFile();

        if (!j1.exists() || !j2.exists()) {
            sender.sendMessage("One of the jars does not exist");
            return true;
        }

        try {
            var diff = DIFF_DIR.resolve(jar1 + "-" + jar2 + ".diff");
            var diffFile = diff.toFile();
            /*if (!diffFile.exists()) {
                diffFile.createNewFile();
            } else {
                diffFile.delete();
                diffFile.createNewFile();
            }*/

            SimplePatcher.createPatch(j1, j2, diffFile, BaseCompress.GZIP);
            /*try (var fis1 = new FileInputStream(j1); var fis2 = new FileInputStream(j2)) {
                try (ByteArrayOutputStream output = (ByteArrayOutputStream) SimplePatcher.createPatch(fis1, fis2); OutputStream fos = Files.newOutputStream(diff)) {
                    output.writeTo(fos);
                    fos.flush();
                }
            }*/

            sender.sendMessage("Diff created at " + diff.toAbsolutePath());

        } catch (IOException e) {
            sender.sendMessage("Error: " + e.getMessage());
            e.printStackTrace();
        }

        return true;
    }

    private boolean runMergeDiff(@NotNull CommandSender sender, @NotNull String commandLabel, String[] args) {
        GoldPiglinLogger.logger.info("Args: {}", String.join(" ", args));
        var patch = args[1];
        var jar = args[2];

        var p = DIFF_DIR.resolve(patch).toFile();
        var j = Plugin_DIR.resolve(jar).toFile();
        var j2 = Plugin_DIR.resolve(jar + ".tmp").toFile();

        try {
            SimplePatcher.applyPatch(p, j, j2, BaseCompress.GZIP);

            sender.sendMessage("Diff applied to " + j2.getAbsolutePath());
        } catch (IOException e) {
            sender.sendMessage("Error: " + e.getMessage());
            e.printStackTrace();
            return true;
        }

        /*try (var pS = new FileInputStream(p); var jS = new FileInputStream(j)) {
            try (ByteArrayOutputStream fps = (ByteArrayOutputStream) SimplePatcher.applyPatch(pS, jS); var fos = Files.newOutputStream(j2)) {
                fps.writeTo(fos);
                fos.flush();
            }
        } catch (Exception exception) {
            sender.sendMessage("Error: " + exception.getMessage());
            exception.printStackTrace();
            return true;
        }*/

        return false;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            list.add("create");
            list.add("merge");
        }
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("create"))
                list.addAll(getJarFiles());
            if (args[0].equalsIgnoreCase("merge"))
                list.addAll(getDiffFiles());
        }
        if (args.length == 3 && (args[0].equalsIgnoreCase("merge") || args[0].equalsIgnoreCase("create"))) {
            list.addAll(getJarFiles());
        }
        return list;
    }
}
