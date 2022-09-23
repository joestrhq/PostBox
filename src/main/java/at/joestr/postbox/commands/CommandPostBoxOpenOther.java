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
import at.joestr.postbox.utils.PostBoxUtils;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
public class CommandPostBoxOpenOther implements TabExecutor {

  public CommandPostBoxOpenOther() {
  }

  @Override
  public List<String> onTabComplete(
    CommandSender cs, Command cmnd, String string, String[] strings) {
    Stream<String> completions
      = Bukkit.getServer().getOnlinePlayers().stream().map(Player::getName);

    if (strings.length == 0) {
      return completions.collect(Collectors.toList());
    }

    if (strings.length == 1) {
      return completions.filter(c -> c.startsWith(strings[0])).collect(Collectors.toList());
    }

    return List.of();
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String string, String[] args) {
    if (args.length != 1) {
      return false;
    }

    final Locale locale
      = sender instanceof Player
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
        () -> {
          Player player = (Player) sender;

          PostBoxUtils.resolveName(args[0])
            .whenComplete(
              (targetUuid, exception) -> {
                if (exception != null) {
                  // TODO: Handle resolution error
                  return;
                }

                if (targetUuid == null) {
                  new MessageHelper()
                    .prefix(true)
                    .path(CurrentEntries.LANG_CMD_POSTBOX_OPENOTHER_EMPTY)
                    .modify((s) -> s.replace("%playername", args[0]))
                    .locale(locale)
                    .receiver(sender)
                    .send();
                  return;
                }

                List<PostBoxModel> playerPostBox = new ArrayList();

                try {
                  playerPostBox.addAll(
                    DatabaseConfiguration.getInstance()
                      .getPostBoxDao()
                      .queryBuilder()
                      .where()
                      .eq("receiver", targetUuid)
                      .query());
                } catch (SQLException ex) {
                  Logger.getLogger(CommandPostBoxOpen.class.getName())
                    .log(Level.SEVERE, null, ex);
                }

                if (playerPostBox.isEmpty()) {
                  new MessageHelper()
                    .prefix(true)
                    .path(CurrentEntries.LANG_CMD_POSTBOX_OPENOTHER_EMPTY)
                    .locale(locale)
                    .receiver(sender)
                    .modify((s) -> s.replace("%playername", args[0]))
                    .send();
                  return;
                }

                HashMap<UUID, String> resolvedUuids = new HashMap<>();

                for (PostBoxModel lPbo : playerPostBox) {
                  try {
                    resolvedUuids.putIfAbsent(
                      lPbo.getSender(), PostBoxUtils.resolveUniqueId(lPbo.getSender()).get());
                  } catch (InterruptedException | ExecutionException ex) {
                    Logger.getLogger(CommandPostBoxOpen.class.getName())
                      .log(Level.SEVERE, null, ex);
                  }
                }

                Bukkit.getScheduler()
                  .runTask(
                    PostBoxPlugin.getInstance(),
                    () -> {
                      Inventory inventory
                      = Bukkit.getServer()
                        .createInventory(
                          null,
                          AppConfiguration.getInstance()
                            .getInt(CurrentEntries.CONF_SIZE.toString()),
                          new MessageHelper()
                            .locale(locale)
                            .path(
                              CurrentEntries.LANG_CMD_POSTBOX_OPENOTHER_CHEST_TITLE)
                            .string()
                            .replace("%playername", args[0]));
                      PostBoxPlugin.getInstance()
                        .getInventoryMappings()
                        .add(Triple.of(player.getUniqueId(), inventory, targetUuid));
                      int inventoryItemCount = 0;

                      for (PostBoxModel lPbo : playerPostBox) {
                        ItemStack localItemStack = lPbo.getItemStack();
                        ItemMeta localItemMeta = localItemStack.getItemMeta();
                        if (localItemMeta != null) {
                          localItemMeta.setLore(List.of(resolvedUuids.get(lPbo.getSender())));
                        }
                        localItemStack.setItemMeta(localItemMeta);
                        inventory.setItem(inventoryItemCount++, localItemStack);
                      }

                      player.openInventory(inventory);
                    });
              });
        });

    return true;
  }
}
