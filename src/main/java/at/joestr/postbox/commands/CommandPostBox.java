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

import at.joestr.postbox.configuration.CurrentEntries;
import at.joestr.postbox.configuration.LocaleHelper;
import at.joestr.postbox.configuration.MessageHelper;
import java.util.List;
import java.util.Locale;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

/**
 * @author joestr
 */
public class CommandPostBox implements TabExecutor {
  @Override
  public List<String> onTabComplete(
      CommandSender sender, Command command, String alias, String[] args) {
    return List.of();
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
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
