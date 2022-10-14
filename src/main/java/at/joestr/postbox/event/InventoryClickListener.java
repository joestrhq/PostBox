//
// MIT License
//
// Copyright (c) 2022 Joel Strasser
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//
package at.joestr.postbox.event;

import at.joestr.postbox.PostBoxPlugin;
import at.joestr.postbox.configuration.CurrentEntries;
import at.joestr.postbox.configuration.DatabaseConfiguration;
import at.joestr.postbox.configuration.DatabaseModels.PostBoxModel;
import at.joestr.postbox.configuration.LocaleHelper;
import at.joestr.postbox.configuration.MessageHelper;
import com.j256.ormlite.stmt.DeleteBuilder;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.tuple.Triple;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryClickListener implements Listener {

  private static final Logger LOGGER = Logger.getLogger(InventoryClickListener.class.getName());

  public InventoryClickListener() {
  }

  // Highest Priority and ignore cancelled events
  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
  public void Inventory(InventoryClickEvent event) {
    Player player = (Player) event.getWhoClicked();
    Locale locale = LocaleHelper.resolve(player.getLocale());

    if (event.getCurrentItem() == null) {
      return;
    }

    if (!PostBoxPlugin.getInstance().getInventoryMappings().stream()
      .anyMatch(t -> t.getLeft().equals(player.getUniqueId()))) {
      return;
    }

    if (!PostBoxPlugin.getInstance().getInventoryMappings().stream()
      .anyMatch(t -> t.getMiddle().equals(event.getClickedInventory()))) {
      event.setCancelled(true);
      return;
    }

    if (event.isShiftClick() || event.isRightClick() || event.getClick() == ClickType.MIDDLE) {
      event.setCancelled(true);
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

    final int rawEventSlot = event.getRawSlot();
    event.setCancelled(true);

    Bukkit.getScheduler().runTaskAsynchronously(PostBoxPlugin.getInstance(), () -> {
      PostBoxModel postBoxModelEntry = null;

      try {
        postBoxModelEntry
          = DatabaseConfiguration.getInstance()
            .getPostBoxDao()
            .queryBuilder()
            .where()
            .eq(
              "receiver",
              PostBoxPlugin.getInstance().getInventoryMappings().stream()
                .filter(t -> t.getLeft().equals(player.getUniqueId()))
                .findFirst()
                .get()
                .getRight())
            .query()
            .get(rawEventSlot);
      } catch (SQLException ex) {
        LOGGER.log(Level.SEVERE, null, ex);
        return;
      }

      try {
        DeleteBuilder<PostBoxModel, String> deleteBuilder
          = DatabaseConfiguration.getInstance().getPostBoxDao().deleteBuilder();
        deleteBuilder.where().eq("id", postBoxModelEntry.getId());
        deleteBuilder.delete();
      } catch (SQLException ex) {
        LOGGER.log(Level.SEVERE, null, ex);
        return;
      }

      // Circumvent
      final PostBoxModel targetEntry = postBoxModelEntry;

      Bukkit.getScheduler().runTask(PostBoxPlugin.getInstance(), () -> {
        player.setItemOnCursor(null);
        player.getInventory().addItem(targetEntry.getItemStack());

        Optional<Triple<UUID, Inventory, UUID>> tempForInventoryResolution
          = PostBoxPlugin.getInstance().getInventoryMappings().stream()
            .filter(t -> t.getLeft().equals(player.getUniqueId())).findFirst();

        if (tempForInventoryResolution.isEmpty()) {
          return;
        }

        Inventory postBoxInventory
          = tempForInventoryResolution.get().getMiddle();

        postBoxInventory.remove(postBoxInventory.getItem(rawEventSlot));
        ItemStack[] contents = postBoxInventory.getContents();
        postBoxInventory.clear();

        for (ItemStack i : contents) {
          if (i == null) {
            continue;
          }
          postBoxInventory.addItem(i);
        }
      });
    });
  }
}
