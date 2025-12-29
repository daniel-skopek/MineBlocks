package cz.raixo.blocks.block.rewards.commands.batch;

import cz.raixo.blocks.block.rewards.commands.RewardEntry;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class BatchCommandEntry implements RewardEntry {

    private final String command;
    private final String message;

    public BatchCommandEntry(String command) {
        String[] split = command.split(";", 2);
        this.command = split[0];
        this.message = split.length > 1 ? split[1] : null;
    }
    
}