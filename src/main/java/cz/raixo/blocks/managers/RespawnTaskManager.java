package cz.raixo.blocks.managers;

import cz.raixo.blocks.MineBlocksPlugin;
import cz.raixo.blocks.block.MineBlock;
import cz.raixo.blocks.block.cooldown.BlockCoolDown;
import cz.raixo.blocks.tasks.RespawnTask;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class RespawnTaskManager {
    private final MineBlocksPlugin plugin;
    private final Map<Integer, RespawnTask> scheduledTasks = new HashMap<>();
    private final Map<Integer, Integer> taskIds = new HashMap<>();
    private int schedulerTaskId;

    public RespawnTaskManager(MineBlocksPlugin plugin) {
        this.plugin = plugin;
    }

    public void scheduleTask(BlockCoolDown blockCoolDown, MineBlock mineBlock, long executionTime) {
        RespawnTask task = new RespawnTask(blockCoolDown, mineBlock, executionTime);
        int taskId = task.hashCode();
        scheduledTasks.put(taskId, task);

        blockCoolDown.setRespawnTaskID(schedulerTaskId);

        if (schedulerTaskId == 0) {
            schedulerTaskId = Bukkit.getScheduler().runTaskTimer(plugin, this::checkTasks, 20L, 20L).getTaskId();
        }
    }

    public void cancelTask(int taskId) {
        Integer currentTaskId = taskIds.remove(taskId);
        if (currentTaskId != null) {
            Bukkit.getScheduler().cancelTask(currentTaskId);
        }

        scheduledTasks.remove(taskId);

        if (scheduledTasks.isEmpty()) {
            Bukkit.getScheduler().cancelTask(schedulerTaskId);
            schedulerTaskId = 0;
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
            Bukkit.getScheduler().cancelTask(schedulerTaskId);
            schedulerTaskId = 0;
        }
    }
}