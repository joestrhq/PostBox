package at.joestr.postbox.event;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import at.joestr.postbox.PostBoxPlugin;
import at.joestr.postbox.configuration.CurrentEntries;
import at.joestr.postbox.models.PostBoxModel;
import at.joestr.postbox.utils.LocaleHelper;
import at.joestr.postbox.utils.MessageHelper;
import java.sql.SQLException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;

public class InventoryClickListener implements Listener {

  public InventoryClickListener() {
  }

  // Highest Priority and ignore cancelled events
  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
  public void Inventory(InventoryClickEvent event) {
    Player player = (Player) event.getWhoClicked();
    Locale locale = LocaleHelper.resolve(player.getLocale());

    if (!PostBoxPlugin.getInstance().getInventoryMappings().stream().anyMatch(t -> t.getLeft().equals(player.getUniqueId()))) {
      return;
    }
    
    if (!PostBoxPlugin.getInstance().getInventoryMappings().stream().anyMatch(t -> t.getMiddle().equals(event.getClickedInventory()))) {
      event.setCancelled(true);
      return;
    }
    
    if (event.getCurrentItem() == null) {
      return;
    }

    if (player.getInventory().firstEmpty() == -1) {
      event.setCancelled(true);
      player.closeInventory();
      new MessageHelper()
        .prefix(true)
        .path(CurrentEntries.LANG_EVT_INVENTORY_FULL)
        .receiver(player)
        .locale(locale)
        .send();
      return;
    }
    
    event.getClickedInventory().remove(event.getCurrentItem());
    
    PostBoxModel get = null;
    try {
      get = PostBoxPlugin.getInstance()
        .getPostBoxDao()
        .queryBuilder()
        .where()
        .eq("player", PostBoxPlugin.getInstance().getInventoryMappings().stream().filter(t -> t.getLeft().equals(player.getUniqueId())).findFirst().get().getRight())
        .query()
        .get(event.getRawSlot());
    } catch (SQLException ex) {
      return;
    }

    player.getInventory().addItem(
      get.getItemStack()
    );

    try {
      PostBoxPlugin.getInstance().getPostBoxDao().delete(get);
    } catch (SQLException ex) {
      Logger.getLogger(InventoryClickListener.class.getName()).log(Level.SEVERE, null, ex);
    }

    ItemStack[] contents = event.getClickedInventory().getContents();
    event.getInventory().clear();
    event.getInventory().setContents(contents);
  }
}
