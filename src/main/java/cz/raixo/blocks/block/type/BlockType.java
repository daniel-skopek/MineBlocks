package cz.raixo.blocks.block.type;

import cz.raixo.blocks.block.MineBlock;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.util.Optional;

@Getter
public class BlockType {

    @Getter(AccessLevel.NONE)
    private final MineBlock block;
    private Material type;
    private Material override;

    public BlockType(MineBlock block, Material type) {
        this.block = block;
        this.type = type;
    }

    public void setType(Material type) {
        this.type = type;
        update();
    }

    public void setOverride(Material override) {
        this.override = override;
        update();
    }

    public void update() {
        Bukkit.getLogger().info("[MineBlocks] BlockType.update(): Updating block " + block.getId());
        block.getLocation().getBlock().setType(get(), false);
        block.getHologram().update();
    }

    public Material get() {
        return Optional.ofNullable(override).orElse(type);
    }

}
