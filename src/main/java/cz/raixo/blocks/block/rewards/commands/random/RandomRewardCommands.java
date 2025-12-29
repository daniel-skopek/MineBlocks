package cz.raixo.blocks.block.rewards.commands.random;

import cz.raixo.blocks.block.playerdata.PlayerData;
import cz.raixo.blocks.block.rewards.commands.RewardCommands;
import cz.raixo.blocks.block.rewards.commands.RewardEntry;
import cz.raixo.blocks.block.rewards.context.RewardContext;
import cz.raixo.blocks.util.NumberUtil;
import cz.raixo.blocks.util.SimpleRandom;
import lombok.SneakyThrows;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;
import java.util.function.Supplier;

public class RandomRewardCommands implements RewardCommands<RandomCommandEntry> {

    static String MODE_NAME = "random";

    private final List<RandomCommandEntry> entries = new LinkedList<>();
    private final SimpleRandom<RandomCommandEntry> commands = new SimpleRandom<>();

    @SneakyThrows
    public RandomRewardCommands(Object commands) {
        if (commands instanceof List) {
            for (Object command : (List<?>) commands) {
                if (command instanceof String) {
                    String[] cmd = ((String) command).split(";", 3);
                    this.entries.add(
                            new RandomCommandEntry(
                                    cmd[1],
                                    cmd.length > 2 ? cmd[2] : null,
                                    NumberUtil.parseInt(cmd[0]).orElseThrow((Supplier<Throwable>) () -> new IllegalArgumentException("Invalid chance on command " + command))
                            )
                    );
                }
            }
        } else if (commands instanceof ConfigurationSection) {
            ConfigurationSection section = (ConfigurationSection) commands;
            for (String key : section.getKeys(false)) {
                int chance = NumberUtil.parseInt(key).orElse(0);
                if (section.isList(key)) {
                    parseSectionEntries(chance, section.getStringList(key));
                } else {
                    String val = section.getString(key);
                    if (val != null) {
                        parseSectionEntries(chance, Collections.singletonList(val));
                    }
                }
            }
        }
        refresh();
    }

    private void parseSectionEntries(int chance, List<String> lines) {
        String currentCommand = null;
        String currentMessage = null;

        for (String line : lines) {
            String lowerLine = line.toLowerCase();
            if (lowerLine.startsWith("[command]")) {
                if (currentCommand != null) {
                    entries.add(new RandomCommandEntry(currentCommand, currentMessage, chance));
                    currentMessage = null;
                }
                currentCommand = line.substring(9).trim();
            } else if (lowerLine.startsWith("[message]")) {
                currentMessage = line.substring(9).trim();
            } else {
                if (currentCommand != null) {
                    entries.add(new RandomCommandEntry(currentCommand, currentMessage, chance));
                    currentMessage = null;
                }
                currentCommand = line;
            }
        }

        if (currentCommand != null) {
            entries.add(new RandomCommandEntry(currentCommand, currentMessage, chance));
        }
    }

    @Override
    public List<RandomCommandEntry> asList() {
        return Collections.unmodifiableList(entries);
    }

    @Override
    public List<String> saveToList() {
        List<String> value = new LinkedList<>();
        for (RandomCommandEntry entry : entries) {
            StringBuilder val = new StringBuilder();
            val.append(entry.getChance()).append(";").append(entry.getCommand());
            if (entry.getMessage() != null) val.append(";").append(entry.getMessage());
            value.add(val.toString());

        }
        return value;
    }

    @Override
    public void save(ConfigurationSection section) {
        Map<Integer, List<String>> groups = new LinkedHashMap<>();
        for (RandomCommandEntry entry : entries) {
            List<String> lines = groups.computeIfAbsent(entry.getChance(), k -> new LinkedList<>());
            if (entry.getMessage() != null) {
                lines.add("[command] " + entry.getCommand());
                lines.add("[message] " + entry.getMessage());
            } else {
                lines.add(entry.getCommand());
            }
        }

        for (Map.Entry<Integer, List<String>> entry : groups.entrySet()) {
            section.set(String.valueOf(entry.getKey()), entry.getValue());
        }
    }

    @Override
    public void addCommand(RandomCommandEntry entry) {
        entries.add(entry);
        refresh();
    }

    @Override
    public void removeCommand(RewardEntry entry) {
        if (entry instanceof RandomCommandEntry) {
            entries.remove(entry);
            refresh();
        }
    }

    public RandomCommandEntry getRandom(Random random) {
        return commands.next(random);
    }

    public void refresh() {
        commands.clear();
        for (RandomCommandEntry entry : entries) {
            commands.add(entry.getChance(), entry);
        }
    }

    @Override
    public List<RewardEntry> rewardPlayer(PlayerData player, RewardContext context) {
        return Collections.singletonList(getRandom(context.getRandom()));
    }

    @Override
    public String getModeName() {
        return MODE_NAME;
    }

}
