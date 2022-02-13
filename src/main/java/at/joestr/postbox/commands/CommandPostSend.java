/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package at.joestr.postbox.commands;

import at.joestr.postbox.PostBoxPlugin;
import at.joestr.postbox.configuration.AppConfiguration;
import at.joestr.postbox.configuration.CurrentEntries;
import at.joestr.postbox.configuration.DatabaseConfiguration;
import at.joestr.postbox.configuration.DatabaseModels;
import at.joestr.postbox.configuration.DatabaseModels.PostBoxModel;
import at.joestr.postbox.configuration.LocaleHelper;
import at.joestr.postbox.configuration.MessageHelper;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author joestr
 */
public class CommandPostSend implements TabExecutor {

  public CommandPostSend(PostBoxPlugin aThis) {
  }

  @Override
  public List<String> onTabComplete(CommandSender cs, Command cmnd, String string, String[] strings) {
    Stream<String> completions = Bukkit.getServer().getOnlinePlayers().stream().map(Player::getName);
    
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
			= cs instanceof Player
				? LocaleHelper.resolve(((Player) cs).getLocale())
				: Locale.ENGLISH;

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
    
    List<PostBoxModel> llPbo = null;
    try {
      llPbo = DatabaseConfiguration.getInstance().getPostBoxDao().queryBuilder().where().eq("player", receiver.getUniqueId()).query();
    } catch (SQLException ex) {
      // TODO: send message if exception
      player.sendMessage("Exception");
      return true;
    }
    
    if (llPbo.size() == AppConfiguration.getInstance().getInt(CurrentEntries.CONF_SIZE.toString())) {
      new MessageHelper()
        .prefix(true)
        .path(CurrentEntries.LANG_CMD_POSTBOX_SEND_RECEIPIENT_FULL)
        .locale(locale)
        .modify(s -> s.replace("%playername", strings[0]))
        .receiver(cs)
        .send();
      return true;
    }
    
    ItemStack itemstack = player.getInventory().getItemInMainHand();

    if (itemstack == null) {
      new MessageHelper()
        .prefix(true)
        .path(CurrentEntries.LANG_CMD_POSTBOX_SEND_SEND_EMPTY)
        .locale(locale)
        .receiver(cs)
        .send();
      return true;
    }
    
    player.getInventory().clear(player.getInventory().first(itemstack));
    
    PostBoxModel newM;
    newM = new PostBoxModel();
    newM.setReceiver(receiver.getUniqueId());
    newM.setItemStack(itemstack);
    newM.setTimestamp(LocalDateTime.now());
    newM.setSender(player.getUniqueId());
    
    try {
      int count = (int) DatabaseConfiguration.getInstance().getPostBoxDao().queryBuilder()
        .where().eq("player", receiver.getUniqueId()).countOf();
      
      // TODO: check if count over configured limit
      
      DatabaseConfiguration.getInstance().getPostBoxDao().create(
        newM
      );
    } catch (SQLException ex) {
      Logger.getLogger(CommandPostSend.class.getName()).log(Level.SEVERE, null, ex);
    }
    
    new MessageHelper()
      .prefix(true)
      .path(CurrentEntries.LANG_CMD_POSTBOX_SEND_SUCCESS_SENDER)
      .locale(locale)
      .modify(s -> s.replace("%player", strings[0]))
      .receiver(cs)
      .send();
    
    if (receiver.isOnline()) {
      new MessageHelper()
        .prefix(true)
        .path(CurrentEntries.LANG_CMD_POSTBOX_SEND_SUCCESS_RECEIVER)
        .locale(locale)
        .modify(s -> s.replace("%player", strings[0]))
        .receiver((CommandSender) receiver)
        .send();
    }
    
    return true;
  }
}
