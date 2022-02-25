/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package at.joestr.postbox.commands;

import at.joestr.postbox.configuration.CurrentEntries;
import at.joestr.postbox.configuration.LocaleHelper;
import at.joestr.postbox.configuration.MessageHelper;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Locale;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

/**
 *
 * @author joestr
 */
public class CommandPostBox implements TabExecutor {
  @Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		return ImmutableList.of();
	}
  
  @Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		if (args.length != 0) {
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

		if (sender.hasPermission(CurrentEntries.PERM_CMD_POSTBOX_SEND.toString())) {
			new MessageHelper()
        .prefix(true)
				.path(CurrentEntries.LANG_CMD_POSTBOX_X_MSG_SEND)
				.locale(locale)
				.receiver(sender)
				.send();
		}

		if (sender.hasPermission(CurrentEntries.PERM_CMD_POSTBOX_OPEN.toString())) {
			new MessageHelper()
        .prefix(true)
				.path(CurrentEntries.LANG_CMD_POSTBOX_X_MSG_OPEN)
				.locale(locale)
				.receiver(sender)
				.send();
		}

		if (sender.hasPermission(CurrentEntries.PERM_CMD_POSTBOX_OPENOTHER.toString())) {
			new MessageHelper()
        .prefix(true)
				.path(CurrentEntries.LANG_CMD_POSTBOX_X_MSG_OPENOTHER)
				.locale(locale)
				.receiver(sender)
				.send();
		}

		if (sender.hasPermission(CurrentEntries.PERM_CMD_POSTBOX_UPDATE.toString())) {
			new MessageHelper()
        .prefix(true)
				.path(CurrentEntries.LANG_CMD_POSTBOX_X_MSG_UPDATE)
				.locale(locale)
				.receiver(sender)
				.send();
		}
    
		return true;
	}
}
