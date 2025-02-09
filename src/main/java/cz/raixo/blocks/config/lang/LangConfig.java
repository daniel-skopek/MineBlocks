package cz.raixo.blocks.config.lang;

import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Getter
public class LangConfig {

    private static final long HOUR_MS = TimeUnit.MILLISECONDS.convert(1, TimeUnit.HOURS);
    private static final long MINUTE_MS = TimeUnit.MILLISECONDS.convert(1, TimeUnit.MINUTES);
    private static final long SECOND_MS = TimeUnit.MILLISECONDS.convert(1, TimeUnit.SECONDS);

    private final ConfigurationSection config;

    public LangConfig(ConfigurationSection config) {
        this.config = config;
    }

    public String getNobodyName() {
        return config.getString("top.nobody", "Nobody");
    }

    public String getNobodyBreaks() {
        return config.getString("top.nobody-breaks", "0");
    }

    public String getStatusTimeout() {
        return config.getString("status.timeout", "You can't destroy the block now!");
    }

    public String getStatusAFK() {
        return config.getString("status.afk", "You are AFK!");
    }

    public String getStatusNoPermission() {
        return config.getString("status.no-permission", "You don't have permission to break this block!");
    }

    public String getStatusInvalidTool() {
        return config.getString("status.invalid-tool", "You can't use this tool to break this block!");
    }

    public String getTimeoutFormat() {
        return config.getString("timeout.message");
    }

    public String getTimeoutFormatted(Date end) {
        ConfigurationSection unitSection = config.getConfigurationSection("timeout.units");
        if (unitSection == null) return "Invalid timeout configuration";

        long relative = end.getTime() - System.currentTimeMillis();
        if (relative < 0) {
            relative = 0;
        }

        long hours = relative / HOUR_MS;
        relative %= HOUR_MS;
        long minutes = relative / MINUTE_MS;
        relative %= MINUTE_MS;
        long seconds = relative / SECOND_MS;

        StringBuilder timeString = new StringBuilder();

        if (hours > 0) {
            timeString.append(hours).append(" ").append(unitSection.getString(hours == 1 ? "hour" : "hours", "hours")).append(" ");
        }
        if (minutes > 0) {
            timeString.append(minutes).append(" ").append(unitSection.getString(minutes == 1 ? "minute" : "minutes", "minutes")).append(" ");
        }

        timeString.append(seconds).append(" ").append(unitSection.getString(seconds == 1 ? "second" : "seconds", "seconds"));

        return getTimeoutFormat().replace("%time%", timeString.toString().trim());
    }
}
