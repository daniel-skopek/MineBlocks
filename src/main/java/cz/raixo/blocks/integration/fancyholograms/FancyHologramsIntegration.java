package cz.raixo.blocks.integration.fancyholograms;

import cz.raixo.blocks.MineBlocksPlugin;
import cz.raixo.blocks.integration.Integration;
import cz.raixo.blocks.integration.models.hologram.Hologram;
import cz.raixo.blocks.integration.models.hologram.HologramProvider;
import org.bukkit.Location;

public class FancyHologramsIntegration implements Integration, HologramProvider {

    public static final String PLUGIN_NAME = "FancyHolograms";

    public FancyHologramsIntegration(MineBlocksPlugin mineBlocksPlugin) {
    }

    @Override
    public String getPluginName() {
        return PLUGIN_NAME;
    }

    @Override
    public int getPriority() {
        return 100;
    }

    @Override
    public Hologram provide(String name, Location location) {
        return new HologramFancyHolograms(name, location);
    }

}
