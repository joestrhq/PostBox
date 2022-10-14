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
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author joestr
 */
public class CommandPostBoxSend implements TabExecutor {

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
  public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {
    if (strings.length != 1) {
      return false;
    }

    final Locale locale
      = cs instanceof Player ? LocaleHelper.resolve(((Player) cs).getLocale()) : Locale.ENGLISH;

    if (!(cs instanceof Player)) {
      new MessageHelper()
        .prefix(true)
        .path(CurrentEntries.LANG_GEN_NOT_A_PLAYER)
        .locale(locale)
        .receiver(cs)
        .send();
      return true;
    }

    Player player = (Player) cs;

    if (player.getName().equals(strings[0])) {
      new MessageHelper()
        .prefix(true)
        .path(CurrentEntries.LANG_CMD_POSTBOX_SEND_SELF)
        .locale(locale)
        .receiver(cs)
        .send();
      return true;
    }

    // preventing trash accounts
    OfflinePlayer receiver = Bukkit.getOfflinePlayer(strings[0]);

    if (!receiver.hasPlayedBefore()) {
      new MessageHelper()
        .prefix(true)
        .path(CurrentEntries.LANG_CMD_POSTBOX_SEND_RECEIVER_NEVER_PLAYED)
        .modify(s -> s.replace("%playername", strings[0]))
        .locale(locale)
        .receiver(cs)
        .send();
      return true;
    }

    ItemStack itemstack = player.getInventory().getItemInMainHand();

    if (itemstack.getType() == Material.AIR) {
      new MessageHelper()
        .prefix(true)
        .path(CurrentEntries.LANG_CMD_POSTBOX_SEND_SEND_EMPTY)
        .modify(s -> s.replace("%playername", strings[0]))
        .locale(locale)
        .receiver(cs)
        .send();
      return true;
    }

    player.getInventory().clear(player.getInventory().first(itemstack));

    Bukkit.getScheduler()
      .runTaskAsynchronously(
        PostBoxPlugin.getInstance(),
        () -> {
          PostBoxUtils.resolveName(strings[0])
            .whenComplete(
              (targetUuid, exception) -> {
                if (exception != null) {
                  // TODO: Handle resolution error
                  return;
                }

                List<PostBoxModel> llPbo = null;
                try {
                  llPbo
                  = DatabaseConfiguration.getInstance()
                    .getPostBoxDao()
                    .queryBuilder()
                    .where()
                    .eq("receiver", targetUuid)
                    .query();
                } catch (SQLException ex) {
                  Bukkit.getScheduler()
                    .callSyncMethod(
                      PostBoxPlugin.getInstance(),
                      () -> {
                        player.getInventory().addItem(itemstack);
                        return true;
                      });
                  // TODO: send message if exception
                  player.sendMessage("Exception");
                  return;
                }

                if (llPbo.size()
                == AppConfiguration.getInstance()
                  .getInt(CurrentEntries.CONF_SIZE.toString())) {
                  new MessageHelper()
                    .prefix(true)
                    .path(CurrentEntries.LANG_CMD_POSTBOX_SEND_RECEIPIENT_FULL)
                    .modify(s -> s.replace("%playername", strings[0]))
                    .locale(locale)
                    .receiver(cs)
                    .send();

                  Bukkit.getScheduler()
                    .callSyncMethod(
                      PostBoxPlugin.getInstance(),
                      () -> {
                        player.getInventory().addItem(itemstack);
                        return true;
                      });
                  return;
                }

                PostBoxModel newPostBoxEntry = new PostBoxModel();
                newPostBoxEntry.setReceiver(targetUuid);
                newPostBoxEntry.setItemStack(itemstack);
                newPostBoxEntry.setTimestamp(Instant.now());
                newPostBoxEntry.setSender(player.getUniqueId());

                try {
                  DatabaseConfiguration.getInstance()
                    .getPostBoxDao()
                    .create(newPostBoxEntry);
                } catch (SQLException ex) {
                  Bukkit.getScheduler()
                    .callSyncMethod(
                      PostBoxPlugin.getInstance(),
                      () -> {
                        player.getInventory().addItem(itemstack);
                        return true;
                      });
                  Logger.getLogger(CommandPostBoxSend.class.getName())
                    .log(Level.SEVERE, null, ex);
                }

                new MessageHelper()
                  .prefix(true)
                  .path(CurrentEntries.LANG_CMD_POSTBOX_SEND_SUCCESS_SENDER)
                  .modify(s -> s.replace("%playername", strings[0]))
                  .locale(locale)
                  .receiver(cs)
                  .send();

                if (receiver.isOnline()) {
                  new MessageHelper()
                    .prefix(true)
                    .path(CurrentEntries.LANG_CMD_POSTBOX_SEND_SUCCESS_RECEIVER)
                    .modify(s -> s.replace("%playername", strings[0]))
                    .locale(locale)
                    .receiver((CommandSender) receiver)
                    .send();
                }
              });
        });

    return true;
  }
}
