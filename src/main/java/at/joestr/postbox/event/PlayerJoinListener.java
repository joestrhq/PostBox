package at.joestr.postbox.event;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import at.joestr.postbox.configuration.CurrentEntries;
import at.joestr.postbox.configuration.DatabaseConfiguration;
import at.joestr.postbox.configuration.LocaleHelper;
import at.joestr.postbox.configuration.MessageHelper;
import java.sql.SQLException;
import java.util.Locale;

public class PlayerJoinListener implements Listener {

  public PlayerJoinListener() {
  }

  @EventHandler
  public void onJoin(PlayerJoinEvent event) {
    Player player = event.getPlayer();
    Locale locale = LocaleHelper.resolve(player.getLocale());

    if (!player.hasPermission(CurrentEntries.PERM_CMD_POSTBOX_OPEN.toString())) {
      return;
    }

    long size = -1;
    try {
      size = DatabaseConfiguration.getInstance()
        .getPostBoxDao()
        .queryBuilder()
        .where()
        .eq("receiver", player.getUniqueId())
        .countOf();
    } catch (SQLException ex) {

    }

    if (size > 0) {
      new MessageHelper()
        .prefix(true)
        .path(CurrentEntries.LANG_EVT_MESSAGE_ON_JOIN)
        .locale(locale)
        .receiver(player)
        .send();
    }
  }
}
