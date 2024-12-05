package one.tranic.goldpiglin.common.data;

import org.bukkit.ChatColor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class FetchVersion {
    private final URL checkURL;
    private final String oldVersion;
    private String newVersion;
    private Date lastUpdate = new Date();

    public FetchVersion(String local) {
        this.oldVersion = local;
        try {
            this.checkURL = new URL("https://api.spigotmc.org/legacy/update.php?resource=120819");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void run() {
        Scheduler.execute(() -> {
            try {
                TimeUnit.HOURS.sleep(2);
            } catch (Exception ignored) {
            }
            this.checkForUpdates();
        });
    }

    public String getUpdateMessage() {
        return ChatColor.AQUA + "[GoldPiglin] " + ChatColor.GOLD + "The plugin has an update available, from " + ChatColor.AQUA + this.oldVersion + ChatColor.GOLD + " to " + ChatColor.AQUA + this.getLatestVersion() + ChatColor.GOLD + ", download address: " + ChatColor.AQUA + this.getResourceURL();
    }

    public String getNoUpdateMessage() {
        return ChatColor.AQUA + "[GoldPiglin] " + ChatColor.GREEN + " Already the latest version (or because of cache)";
    }

    public boolean isExpired() {
        Date currentTime = new Date();
        long diffInMillis = currentTime.getTime() - lastUpdate.getTime();
        long diffInMinutes = diffInMillis / (1000 * 60);

        return diffInMinutes > 1440;
    }

    public String getLatestVersion() {
        return newVersion;
    }

    public String getResourceURL() {
        return "https://modrinth.com/plugin/goldpiglin";
    }

    public boolean checkForUpdates() {
        if (this.newVersion != null && !this.newVersion.isEmpty()) {
            if (!isExpired()) return !newVersion.equals(this.oldVersion);
        }
        try {
            URLConnection con = checkURL.openConnection();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                this.newVersion = in.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        if (this.newVersion == null || this.newVersion.isEmpty()) {
            return false;
        }

        lastUpdate = new Date();
        return !newVersion.equals(this.oldVersion);
    }
}
