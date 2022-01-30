// 
// Private License
// 
// Copyright (c) 2019-2020 Joel Strasser <strasser999@gmail.com>
// 
// Only the copyright holder is allowed to use this software.
// 
package at.joestr.postbox.configuration;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Optional;

/**
 *
 * @author Joel
 */
public enum CurrentEntries {
	// Configuration entries
	CONF_VERSION("version", new int[]{1}),
	CONF_JDBCURI("jdbcUri", new int[]{1}),
	CONF_SIZE("size", new int[]{1}),
	CONF_UPDATER_ENABLED("updater.enabled", new int[]{1}),
	CONF_UPDATER_DOWNLOADTOPLUGINUPDATEFOLDER("updater.downloadToPluginUpdateFolder", new int[]{1}),
	CONF_UPDATER_TARGETURL("updater.targetUrl", new int[]{1}),
	CONF_UPDATER_POMPROPERTIESFILE("updater.pomPropertiesFile", new int[]{1}),
	CONF_UPDATER_CLASSIFIER("updater.classifier", new int[]{1}),
	// Languages
  LANG_VERSION("version", new int[]{1}),
	LANG_PREFIX("prefix", new int[]{1}),
	LANG_GEN_NOT_A_PLAYER("generic.not_a_player", new int[]{1}),
	LANG_CMD_POSTBOX_X_MSG_SEND("commands.postbox.message_send", new int[]{1}),
	LANG_CMD_POSTBOX_X_MSG_OPEN("commands.postbox.message_open", new int[]{1}),
	LANG_CMD_POSTBOX_X_MSG_OPENOTHER("commands.postbox.message_openother", new int[]{1}),
	LANG_CMD_POSTBOX_X_MSG_UPDATE("commands.postbox.message_update", new int[]{1}),
  LANG_CMD_POSTBOX_SEND_SELF("commands.postbox-send.self", new int[]{1}),
  LANG_CMD_POSTBOX_SEND_RECEIVER_NEVER_PLAYED("commands.postbox-send.receiver_never_played", new int[]{1}),
  LANG_CMD_POSTBOX_SEND_SEND_EMPTY("commands.postbox-send.send_empty", new int[]{1}),
	LANG_CMD_POSTBOX_SEND_SUCCESS_RECEIVER("commands.postbox-send.success_receive", new int[]{1}),
  LANG_CMD_POSTBOX_SEND_SUCCESS_SENDER("commands.postbox-send.success_sender", new int[]{1}),
	LANG_CMD_POSTBOX_SEND_RECEIPIENT_FULL("commands.postbox-send.recepient_full", new int[]{1}),
	LANG_CMD_POSTBOX_OPEN_CHEST_TITLE("commands.postbox-open.chest_title", new int[]{1}),
	LANG_CMD_POSTBOX_OPEN_EMPTY("commands.postbox-open.empty", new int[]{1}),
  LANG_CMD_POSTBOX_OPENOTHER_CHEST_TITLE("commands.postbox-openother.chest_title", new int[]{1}),
	LANG_CMD_POSTBOX_OPENOTHER_EMPTY("commands.postbox-openother.empty", new int[]{1}),
	LANG_CMD_POSTBOX_UPDATE_OFF("commands.postbox-update.off", new int[]{1}),
	LANG_CMD_POSTBOX_UPDATE_ASYNCSTART("commands.postbox-update.asyncstart", new int[]{1}),
	LANG_CMD_POSTBOX_UPDATE_ERROR("commands.postbox-update.error", new int[]{1}),
	LANG_CMD_POSTBOX_UPDATE_UPTODATE("commands.postbox-update.uptodate", new int[]{1}),
	LANG_CMD_POSTBOX_UPDATE_AVAILABLE("commands.postbox-update.available", new int[]{1}),
	LANG_CMD_POSTBOX_UPDATE_DOWNLOADED("command.postbox-update.downloaded", new int[]{1}),
  LANG_EVT_MESSAGE_ON_JOIN("events.message_on_join", new int[]{1}),
  LANG_EVT_INVENTORY_FULL("events.inventory_full", new int[]{1}),
	// Permissions
	PERM_CMD_POSTBOX("postbox.commands.postbox", new int[]{0}),
	PERM_CMD_POSTBOX_SEND("postbox.commands.postbox-send", new int[]{0}),
	PERM_CMD_POSTBOX_OPEN("postbox.commands.postbox-open", new int[]{0}),
	PERM_CMD_POSTBOX_OPENOTHER("postbox.commands.postbox-openother", new int[]{0}),
	PERM_CMD_POSTBOX_UPDATE("postbox.commands.postbox-update", new int[]{0});

	private final String text;
  private final int[] inVersions;

	private CurrentEntries(String text, int[] inVersions) {
		this.text = text;
    this.inVersions = inVersions;
	}

	@Override
	public String toString() {
		return this.text;
  }

	public static CurrentEntries find(String text) {
		Optional<CurrentEntries> result = Arrays.asList(values())
			.stream()
			.filter(cE -> cE.toString().equalsIgnoreCase(text))
			.findFirst();
		if (result.isPresent()) {
			return result.get();
		}
		throw new NullPointerException(MessageFormat.format("The text {0} is not in this Enum!", text));
	}

	public static CurrentEntries[] getConfigurationEntries() {
		return new CurrentEntries[] {
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
		return new CurrentEntries[] {
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
      LANG_CMD_POSTBOX_OPENOTHER_CHEST_TITLE,
			LANG_CMD_POSTBOX_OPENOTHER_EMPTY,
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
		return new CurrentEntries[] {
			PERM_CMD_POSTBOX,
			PERM_CMD_POSTBOX_SEND,
			PERM_CMD_POSTBOX_OPEN,
			PERM_CMD_POSTBOX_OPENOTHER,
			PERM_CMD_POSTBOX_UPDATE
		};
	}
}
