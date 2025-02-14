package cz.raixo.blocks.tasks;

import cz.raixo.blocks.block.MineBlock;
import cz.raixo.blocks.block.cooldown.BlockCoolDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RespawnTask {
    private final BlockCoolDown blockCoolDown;
    private final MineBlock mineBlock;
    private final long executionTime;
}