package cz.raixo.blocks.integration.models.placeholder;

import org.bukkit.OfflinePlayer;

public interface PlaceholderProvider {

    String setPlaceholders(OfflinePlayer player, String text);

}
