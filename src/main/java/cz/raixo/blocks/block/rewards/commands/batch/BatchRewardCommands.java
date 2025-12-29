package cz.raixo.blocks.block.rewards.commands.batch;

import cz.raixo.blocks.block.playerdata.PlayerData;
import cz.raixo.blocks.block.rewards.commands.RewardCommands;
import cz.raixo.blocks.block.rewards.commands.RewardEntry;
import cz.raixo.blocks.block.rewards.context.RewardContext;
import lombok.SneakyThrows;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class BatchRewardCommands implements RewardCommands<BatchCommandEntry> {

    static String MODE_NAME = "all";

    private final List<BatchCommandEntry> commands;


    @SneakyThrows
    public BatchRewardCommands(Object commands) {
        if (commands instanceof List) {
            this.commands = ((List<?>) commands).stream()
                    .filter(obj -> obj instanceof String)
                    .map(obj -> new BatchCommandEntry((String) obj))
                    .collect(Collectors.toList());
        } else if (commands instanceof ConfigurationSection) {
            ConfigurationSection section = (ConfigurationSection) commands;
            this.commands = new LinkedList<>();
            for (String key : section.getKeys(false)) {
                if (section.isList(key)) {
                    parseSectionEntries(section.getStringList(key));
                } else {
                    String val = section.getString(key);
                    if (val != null) {
                        parseSectionEntries(Collections.singletonList(val));
                    }
                }
            }
        } else {
            this.commands = new LinkedList<>();
        }
    }

    private void parseSectionEntries(List<String> lines) {
        String currentCommand = null;
        String currentMessage = null;

        for (String line : lines) {
            String lowerLine = line.toLowerCase();
            if (lowerLine.startsWith("[command]")) {
                if (currentCommand != null) {
                    commands.add(new BatchCommandEntry(currentCommand, currentMessage));
                    currentMessage = null;
                }
                currentCommand = line.substring(9).trim();
            } else if (lowerLine.startsWith("[message]")) {
                currentMessage = line.substring(9).trim();
            } else {
                if (currentCommand != null) {
                    commands.add(new BatchCommandEntry(currentCommand, currentMessage));
                    currentMessage = null;
                }
                currentCommand = line;
            }
        }

        if (currentCommand != null) {
            commands.add(new BatchCommandEntry(currentCommand, currentMessage));
        }
    }

    @Override
    public List<BatchCommandEntry> asList() {
        return Collections.unmodifiableList(commands);
    }

    @Override
    public List<String> saveToList() {
        return commands.stream().map(entry -> {
            String val = entry.getCommand();
            if (entry.getMessage() != null) val += ";" + entry.getMessage();
            return val;
        }).collect(Collectors.toUnmodifiableList());
    }

    @Override
    public void save(ConfigurationSection section) {
        List<String> lines = new LinkedList<>();
        for (BatchCommandEntry entry : commands) {
            if (entry.getMessage() != null) {
                lines.add("[command] " + entry.getCommand());
                lines.add("[message] " + entry.getMessage());
            } else {
                lines.add(entry.getCommand());
            }
        }
        section.getParent().set(section.getName(), lines);
    }

    @Override
    public void addCommand(BatchCommandEntry command) {
        commands.add(command);
    }

    @Override
    public void removeCommand(RewardEntry command) {
        commands.remove(command);
    }

    @Override
    public List<RewardEntry> rewardPlayer(PlayerData player, RewardContext context) {
        return List.copyOf(commands);
    }

    @Override
    public String getModeName() {
        return MODE_NAME;
    }

}