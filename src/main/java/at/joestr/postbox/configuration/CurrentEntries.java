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

package at.joestr.postbox.configuration;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Optional;

/**
 * @author Joel
 */
public enum CurrentEntries {
  // Configuration entries
  CONF_VERSION("version"),
  CONF_JDBCURI("jdbcUri"),
  CONF_SIZE("size"),
  CONF_UPDATER_ENABLED("updater.enabled"),
  CONF_UPDATER_DOWNLOADTOPLUGINUPDATEFOLDER("updater.downloadToPluginUpdateFolder"),
  CONF_UPDATER_TARGETURL("updater.targetUrl"),
  CONF_UPDATER_POMPROPERTIESFILE("updater.pomPropertiesFile"),
  CONF_UPDATER_CLASSIFIER("updater.classifier"),
  // Languages
  LANG_VERSION("version"),
  LANG_PREFIX("prefix"),
  LANG_GEN_NOT_A_PLAYER("generic.not_a_player"),
  LANG_CMD_POSTBOX_X_MSG_SEND("commands.postbox.message_send"),
  LANG_CMD_POSTBOX_X_MSG_OPEN("commands.postbox.message_open"),
  LANG_CMD_POSTBOX_X_MSG_OPENOTHER("commands.postbox.message_openother"),
  LANG_CMD_POSTBOX_X_MSG_UPDATE("commands.postbox.message_update"),
  LANG_CMD_POSTBOX_SEND_SELF("commands.postbox-send.self"),
  LANG_CMD_POSTBOX_SEND_RECEIVER_NEVER_PLAYED(
    "commands.postbox-send.receiver_never_played"),
  LANG_CMD_POSTBOX_SEND_SEND_EMPTY("commands.postbox-send.send_empty"),
  LANG_CMD_POSTBOX_SEND_SUCCESS_RECEIVER("commands.postbox-send.success_receive"),
  LANG_CMD_POSTBOX_SEND_SUCCESS_SENDER("commands.postbox-send.success_sender"),
  LANG_CMD_POSTBOX_SEND_RECEIPIENT_FULL("commands.postbox-send.receipient_full"),
  LANG_CMD_POSTBOX_OPEN_CHEST_TITLE("commands.postbox-open.chest_title"),
  LANG_CMD_POSTBOX_OPEN_EMPTY("commands.postbox-open.empty"),
  LANG_CMD_POSTBOX_OPEN_PLAYERNAME_RESOLVING_ERROR(
    "commands.postbox-open.playername_resolving_error"),
  LANG_CMD_POSTBOX_OPENOTHER_CHEST_TITLE(
    "commands.postbox-openother.chest_title"),
  LANG_CMD_POSTBOX_OPENOTHER_EMPTY("commands.postbox-openother.empty"),
  LANG_CMD_POSTBOX_OPENOTHER_PLAYERNAME_RESOLVING_ERROR(
    "commands.postbox-openother.playername_resolving_error"),
  LANG_CMD_POSTBOX_UPDATE_OFF("commands.postbox-update.off"),
  LANG_CMD_POSTBOX_UPDATE_ASYNCSTART("commands.postbox-update.asyncstart"),
  LANG_CMD_POSTBOX_UPDATE_ERROR("commands.postbox-update.error"),
  LANG_CMD_POSTBOX_UPDATE_UPTODATE("commands.postbox-update.uptodate"),
  LANG_CMD_POSTBOX_UPDATE_AVAILABLE("commands.postbox-update.available"),
  LANG_CMD_POSTBOX_UPDATE_DOWNLOADED("command.postbox-update.downloaded"),
  LANG_EVT_MESSAGE_ON_JOIN("events.message_on_join"),
  LANG_EVT_INVENTORY_FULL("events.inventory_full"),
  // Permissions
  PERM_CMD_POSTBOX("postbox.commands.postbox"),
  PERM_CMD_POSTBOX_SEND("postbox.commands.postbox-send"),
  PERM_CMD_POSTBOX_OPEN("postbox.commands.postbox-open"),
  PERM_CMD_POSTBOX_OPENOTHER("postbox.commands.postbox-openother"),
  PERM_CMD_POSTBOX_UPDATE("postbox.commands.postbox-update");

  private final String text;

  private CurrentEntries(String text) {
    this.text = text;
  }

  @Override
  public String toString() {
    return this.text;
  }

  public static CurrentEntries find(String text) {
    Optional<CurrentEntries> result
      = Arrays.asList(values()).stream()
        .filter(cE -> cE.toString().equalsIgnoreCase(text))
        .findFirst();
    if (result.isPresent()) {
      return result.get();
    }
    throw new NullPointerException(MessageFormat.format("The text {0} is not in this Enum!", text));
  }

  public static CurrentEntries[] getConfigurationEntries() {
    return new CurrentEntries[]{
      CONF_VERSION,
      CONF_JDBCURI,
      CONF_SIZE,
      CONF_UPDATER_ENABLED,
      CONF_UPDATER_DOWNLOADTOPLUGINUPDATEFOLDER,
      CONF_UPDATER_TARGETURL,
      CONF_UPDATER_POMPROPERTIESFILE,
      CONF_UPDATER_CLASSIFIER
    };
  }

  public static CurrentEntries[] getLanguageEntries() {
    return new CurrentEntries[]{
      LANG_VERSION,
      LANG_PREFIX,
      LANG_GEN_NOT_A_PLAYER,
      LANG_CMD_POSTBOX_X_MSG_SEND,
      LANG_CMD_POSTBOX_X_MSG_OPEN,
      LANG_CMD_POSTBOX_X_MSG_OPENOTHER,
      LANG_CMD_POSTBOX_X_MSG_UPDATE,
      LANG_CMD_POSTBOX_SEND_SELF,
      LANG_CMD_POSTBOX_SEND_RECEIVER_NEVER_PLAYED,
      LANG_CMD_POSTBOX_SEND_SEND_EMPTY,
      LANG_CMD_POSTBOX_SEND_SUCCESS_SENDER,
      LANG_CMD_POSTBOX_SEND_SUCCESS_RECEIVER,
      LANG_CMD_POSTBOX_SEND_RECEIPIENT_FULL,
      LANG_CMD_POSTBOX_OPEN_CHEST_TITLE,
      LANG_CMD_POSTBOX_OPEN_EMPTY,
      LANG_CMD_POSTBOX_OPEN_PLAYERNAME_RESOLVING_ERROR,
      LANG_CMD_POSTBOX_OPENOTHER_CHEST_TITLE,
      LANG_CMD_POSTBOX_OPENOTHER_EMPTY,
      LANG_CMD_POSTBOX_OPENOTHER_PLAYERNAME_RESOLVING_ERROR,
      LANG_CMD_POSTBOX_UPDATE_OFF,
      LANG_CMD_POSTBOX_UPDATE_ASYNCSTART,
      LANG_CMD_POSTBOX_UPDATE_ERROR,
      LANG_CMD_POSTBOX_UPDATE_UPTODATE,
      LANG_CMD_POSTBOX_UPDATE_AVAILABLE,
      LANG_CMD_POSTBOX_UPDATE_DOWNLOADED,
      LANG_EVT_MESSAGE_ON_JOIN,
      LANG_EVT_INVENTORY_FULL
    };
  }

  public static CurrentEntries[] getPermissionEntries() {
    return new CurrentEntries[]{
      PERM_CMD_POSTBOX,
      PERM_CMD_POSTBOX_SEND,
      PERM_CMD_POSTBOX_OPEN,
      PERM_CMD_POSTBOX_OPENOTHER,
      PERM_CMD_POSTBOX_UPDATE
    };
  }
}
