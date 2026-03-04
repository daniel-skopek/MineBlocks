package cz.raixo.blocks.managers;

import com.tcoded.folialib.wrapper.task.WrappedTask;
import cz.raixo.blocks.MineBlocksPlugin;
import cz.raixo.blocks.block.MineBlock;
import cz.raixo.blocks.block.cooldown.BlockCoolDown;
import cz.raixo.blocks.tasks.RespawnTask;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class RespawnTaskManager {
    private final MineBlocksPlugin plugin;
    private final Map<Integer, RespawnTask> scheduledTasks = new HashMap<>();
    private final Map<Integer, WrappedTask> taskList = new HashMap<>();
    private WrappedTask schedulerTask;

    public RespawnTaskManager(MineBlocksPlugin plugin) {
        this.plugin = plugin;
    }

    public void scheduleTask(BlockCoolDown blockCoolDown, MineBlock mineBlock, long executionTime) {
        RespawnTask task = new RespawnTask(blockCoolDown, mineBlock, executionTime);
        int taskId = task.hashCode();
        scheduledTasks.put(taskId, task);

        blockCoolDown.setRespawnTaskID(taskId);

        if (schedulerTask == null) {
            schedulerTask = plugin.getFoliaLib().getScheduler().runTimer(this::checkTasks, 20L, 20L);
        }
    }

    public void cancelTask(int taskId) {
        WrappedTask currentTask = taskList.remove(taskId);
        if (currentTask != null) {
            plugin.getFoliaLib().getScheduler().cancelTask(currentTask);
        }

        scheduledTasks.remove(taskId);

        if (scheduledTasks.isEmpty()) {
            plugin.getFoliaLib().getScheduler().cancelTask(schedulerTask);
            schedulerTask = null;
        }
    }

    private void checkTasks() {
        long currentTime = System.currentTimeMillis();
        for (Iterator<Map.Entry<Integer, RespawnTask>> iterator = scheduledTasks.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<Integer, RespawnTask> entry = iterator.next();
            RespawnTask task = entry.getValue();

            if (currentTime >= task.getExecutionTime() - 1000) {
                task.getBlockCoolDown().deactivate();
                task.getMineBlock().broadcast(task.getBlockCoolDown().getRespawnMessage());
                iterator.remove();
            }
        }

        if (scheduledTasks.isEmpty()) {
            plugin.getFoliaLib().getScheduler().cancelTask(schedulerTask);
            schedulerTask = null;
        }
    }
}