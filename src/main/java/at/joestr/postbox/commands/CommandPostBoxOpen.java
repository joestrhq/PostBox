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

package at.joestr.postbox.commands;

import at.joestr.postbox.PostBoxPlugin;
import at.joestr.postbox.configuration.AppConfiguration;
import at.joestr.postbox.configuration.CurrentEntries;
import at.joestr.postbox.configuration.DatabaseConfiguration;
import at.joestr.postbox.configuration.DatabaseModels.PostBoxModel;
import at.joestr.postbox.configuration.LocaleHelper;
import at.joestr.postbox.configuration.MessageHelper;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.tuple.Triple;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * @author joestr
 */
public class CommandPostBoxOpen implements TabExecutor {

  public CommandPostBoxOpen() {}

  @Override
  public List<String> onTabComplete(
      CommandSender cs, Command cmnd, String string, String[] strings) {
    return List.of();
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String string, String[] args) {
    if (args.length != 0) {
      return false;
    }

    final Locale locale =
        sender instanceof Player
            ? LocaleHelper.resolve(((Player) sender).getLocale())
            : Locale.ENGLISH;

    if (!(sender instanceof Player)) {
      new MessageHelper()
          .prefix(true)
          .path(CurrentEntries.LANG_GEN_NOT_A_PLAYER)
          .locale(locale)
          .receiver(sender)
          .send();
      return true;
    }

    Bukkit.getScheduler()
        .runTaskAsynchronously(
            PostBoxPlugin.getInstance(),
            new Runnable() {
              @Override
              public void run() {
                Player player = (Player) sender;

                List<PostBoxModel> playerPostBox = new ArrayList();
                try {
                  playerPostBox.addAll(
                      DatabaseConfiguration.getInstance()
                          .getPostBoxDao()
                          .queryBuilder()
                          .where()
                          .eq("receiver", player.getUniqueId())
                          .query());
                } catch (SQLException ex) {
                  Logger.getLogger(CommandPostBoxOpen.class.getName()).log(Level.SEVERE, null, ex);
                }

                // Messages can be async
                if (playerPostBox.isEmpty()) {
                  new MessageHelper()
                      .prefix(true)
                      .path(CurrentEntries.LANG_CMD_POSTBOX_OPEN_EMPTY)
                      .locale(locale)
                      .receiver(sender)
                      .send();
                  return;
                }

                Bukkit.getScheduler()
                    .runTask(
                        PostBoxPlugin.getInstance(),
                        new Runnable() {
                          @Override
                          public void run() {
                            Inventory inventory =
                                Bukkit.getServer()
                                    .createInventory(
                                        null,
                                        AppConfiguration.getInstance()
                                            .getInt(CurrentEntries.CONF_SIZE.toString()),
                                        new MessageHelper()
                                            .locale(locale)
                                            .path(CurrentEntries.LANG_CMD_POSTBOX_OPEN_CHEST_TITLE)
                                            .string());
                            PostBoxPlugin.getInstance()
                                .getInventoryMappings()
                                .add(
                                    Triple.of(
                                        player.getUniqueId(), inventory, player.getUniqueId()));

                            int inventoryItemCount = 0;
                            for (PostBoxModel lPbo : playerPostBox) {
                              ItemStack localItemStack = lPbo.getItemStack();
                              ItemMeta localItemMeta = localItemStack.getItemMeta();

                              localItemStack.setItemMeta(localItemMeta);
                              inventory.setItem(inventoryItemCount++, localItemStack);
                            }

                            player.openInventory(inventory);
                          }
                        });
              }
            });

    return true;
  }
}
