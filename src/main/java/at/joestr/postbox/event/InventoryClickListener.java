package at.joestr.postbox.event;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import at.joestr.postbox.PostBoxPlugin;
import at.joestr.postbox.configuration.CurrentEntries;
import at.joestr.postbox.configuration.DatabaseConfiguration;
import at.joestr.postbox.configuration.DatabaseModels.PostBoxModel;
import at.joestr.postbox.configuration.LocaleHelper;
import at.joestr.postbox.configuration.MessageHelper;
import com.j256.ormlite.stmt.DeleteBuilder;
import java.sql.SQLException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
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
    
    ItemStack itemStack = event.getCurrentItem();
    
    Bukkit.getScheduler().runTaskAsynchronously(PostBoxPlugin.getInstance(), () -> {
      PostBoxModel postBoxModelEntry = null;
      try {
        postBoxModelEntry = DatabaseConfiguration.getInstance()
          .getPostBoxDao()
          .queryBuilder()
          .where()
          .eq("receiver", PostBoxPlugin.getInstance().getInventoryMappings().stream().filter(t -> t.getLeft().equals(player.getUniqueId())).findFirst().get().getRight())
          .query()
          .get(event.getRawSlot());
      } catch (SQLException ex) {
        return;
      }
          
      try {
        DeleteBuilder<PostBoxModel, String> deleteBuilder = DatabaseConfiguration.getInstance().getPostBoxDao().deleteBuilder();
        deleteBuilder.where().eq("id", postBoxModelEntry.getId());
        deleteBuilder.delete();
      } catch (SQLException ex) {
        Logger.getLogger(InventoryClickListener.class.getName()).log(Level.SEVERE, null, ex);
      }
      
      // Circumvent 
      final PostBoxModel get = postBoxModelEntry;
            
      Bukkit.getScheduler().runTask(PostBoxPlugin.getInstance(), () -> {
        player.getInventory().addItem(
          get.getItemStack()
        );
        
        event.getWhoClicked().setItemOnCursor(null);

        ItemStack[] contents = event.getClickedInventory().getContents();
        event.getInventory().clear();

        for (ItemStack i : contents) {
          if (i == null) continue;
          event.getInventory().addItem(i);
        }
      });
    });
  }
}