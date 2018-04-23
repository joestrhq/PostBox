package xyz.joestr.postbox.event;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;

import xyz.joestr.postbox.PostBox;

public class OnJoin implements Listener {
	private PostBox plugin;

	public OnJoin(PostBox postbox) {
		this.plugin = postbox;
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}

	@EventHandler
	public void Join(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if ((player.hasPermission("post.use")) || (player.hasPermission("post.admin"))) {
			int counter;
			try {
				counter = this.plugin.getbox().getInt(player.getName() + ".count");
			} catch (Exception e) {
				counter = -1;
			}
			if ((counter != -1) && (counter != 0)) {
				player.spigot().sendMessage(

						new ComponentBuilder("--- PostBox --- (Optionen anklickbar)").color(ChatColor.DARK_GREEN)
								.append("\n>> Du hast Post erhalten. Klicke hier um deine PostBox zu öffnen.")
								.color(ChatColor.GOLD)
								.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
										new ComponentBuilder("/post open").color(ChatColor.DARK_GREEN).create()))
								.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/post open")).create());
			}
		}
	}
}
