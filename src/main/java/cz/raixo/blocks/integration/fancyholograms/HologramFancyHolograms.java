package cz.raixo.blocks.integration.fancyholograms;

import com.fancyinnovations.fancyholograms.api.FancyHolograms;
import com.fancyinnovations.fancyholograms.api.HologramController;
import com.fancyinnovations.fancyholograms.api.HologramRegistry;
import com.fancyinnovations.fancyholograms.api.data.TextHologramData;
import com.fancyinnovations.fancyholograms.api.data.property.Visibility;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class HologramFancyHolograms implements cz.raixo.blocks.integration.models.hologram.Hologram {

    private static final Pattern HEX_COLOR_PATTERN = Pattern.compile("(?i)&?#([0-9a-f]{6})");
    private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.legacySection();
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    private final HologramRegistry registry;
    private final HologramController controller;
    private final Hologram hologram;
    private final TextHologramData data;
    private boolean registered;

    public HologramFancyHolograms(String name, Location location) {
        FancyHolograms fancyHolograms = FancyHolograms.get();
        this.registry = fancyHolograms.getRegistry();
        this.controller = fancyHolograms.getController();
        registry.get(name).ifPresent(registry::unregister);
        this.data = new TextHologramData(name, location);
        data.setPersistent(false);
        data.setFilePath("mineblocks/" + name);
        data.setVisibility(Visibility.MANUAL);
        this.hologram = fancyHolograms.getHologramFactory().apply(data);
        this.registered = false;
    }

    @Override
    public void setLocation(Location location) {
        data.setLocation(location);
    }

    @Override
    public void setLine(int line, String text) {
        List<String> lines = new ArrayList<>(data.getText());
        while (lines.size() <= line) {
            lines.add("");
        }
        lines.set(line, toMiniMessage(text));
        data.setText(lines);
    }

    @Override
    public void setLines(List<String> lines) {
        data.setText(lines.stream().map(this::toMiniMessage).collect(Collectors.toList()));
    }

    @Override
    public void refresh() {
        if (registered) {
            controller.refreshHologram(hologram, Bukkit.getOnlinePlayers());
        }
    }

    @Override
    public void setVisible(boolean value) {
        if (value) {
            data.setVisibility(Visibility.ALL);
            if (registered) {
                controller.refreshHologram(hologram, Bukkit.getOnlinePlayers());
            } else {
                registry.register(hologram);
                registered = true;
            }
        } else {
            data.setVisibility(Visibility.MANUAL);
            if (registered) {
                registry.unregister(hologram);
                registered = false;
            }
        }
    }

    @Override
    public void delete() {
        if (registered) {
            registry.unregister(hologram);
            registered = false;
        }
    }

    @Override
    public List<Component> getPreview() {
        return data.getText().stream()
                .map(line -> {
                    Component component = MINI_MESSAGE.deserializeOrNull(line);
                    return component != null ? component : Component.text(line);
                })
                .collect(Collectors.toList());
    }

    @Override
    public String stripColor(String value) {
        return ChatColor.stripColor(toLegacy(value));
    }

    private String toMiniMessage(String value) {
        return MINI_MESSAGE.serialize(LEGACY_SERIALIZER.deserialize(toLegacy(value)));
    }

    private String toLegacy(String value) {
        Matcher matcher = HEX_COLOR_PATTERN.matcher(value);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            StringBuilder legacyHex = new StringBuilder("\u00A7x");
            for (char c : matcher.group(1).toCharArray()) {
                legacyHex.append('\u00A7').append(c);
            }
            matcher.appendReplacement(buffer, Matcher.quoteReplacement(legacyHex.toString()));
        }
        matcher.appendTail(buffer);
        return buffer.toString().replace('&', '§');
    }

}
