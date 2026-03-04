package cz.raixo.blocks.block.reset;

import com.tcoded.folialib.wrapper.task.WrappedTask;
import cz.raixo.blocks.MineBlocksPlugin;
import cz.raixo.blocks.block.MineBlock;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetOptions {

    private MineBlocksPlugin plugin;
    @Getter(AccessLevel.NONE)
    private final MineBlock block;
    private boolean onRestart;
    private int inactiveTime;
    private String inactiveMessage;
    private WrappedTask inactiveTask;

    public ResetOptions(MineBlocksPlugin plugin, MineBlock block, boolean onRestart, int inactiveTime, String inactiveMessage) {
        this.plugin = plugin;
        this.block = block;
        this.onRestart = onRestart;
        this.inactiveTime = inactiveTime;
        this.inactiveMessage = inactiveMessage;
    }

    public void resetInactive() {
        cancelInactive();
        if (inactiveTime > 0) {
            inactiveTask = plugin.getFoliaLib().getScheduler().runLater(() -> {
                block.broadcast(inactiveMessage);
                block.reset();
            }, inactiveTime * 20L);
        }
    }

    public void cancelInactive() {
        if (inactiveTask != null) {
            inactiveTask.cancel();
            inactiveTask = null;
        }
    }

    public void disable() {
        if (inactiveTask != null) {
            inactiveTask.cancel();
            inactiveTask = null;
        }
    }

}
