package xyz.joestr.postbox.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import xyz.joestr.postbox.PostBox;

public class PostCommand implements CommandExecutor {
	private PostBox plugin;
	public static HoverEvent.Action HEA;
	public static ClickEvent.Action CEA;
	public static ChatColor CC;

	public PostCommand(PostBox postbox) {
		this.plugin = postbox;
	}

	public boolean onCommand(CommandSender sender, Command command, String string, String[] arg) {
		if ((sender instanceof Player)) {
			// Sender ist ein Spieler
			Player player = (Player) sender;
			if (arg.length > 0) {
				if (arg[0].equalsIgnoreCase("open")) {
					if (arg.length == 1) {
						if (player.hasPermission("post.use")) {
							int counter;

							try {
								counter = this.plugin.getbox().getInt(player.getName() + ".count");
							} catch (Exception e) {
								counter = 0;
							}

							if (counter == 0) {
								player.sendMessage("§2--- PostBox ---");
								player.sendMessage("§6Deine PostBox ist leer.");
								return true;
							}

							this.plugin.isAdminMode.remove(player.getName());

							ItemStack itemstack = null;
							this.plugin.postbox = player.getPlayer().getServer().createInventory(null,
									this.plugin.getConfig().getInt("default_postbox_size"), "§6[§2PostBox§6]");

							for (int i = 0; i < counter; i++) {
								itemstack = this.plugin.getbox().getItemStack(player.getName() + ".slot" + i);

								if (itemstack == null) {
									itemstack = new ItemStack(Material.AIR);
								}

								ItemMeta meta = itemstack.getItemMeta();
								List<String> list = new ArrayList<String>();
								String str = this.plugin.getbox().getString(player.getName() + ".slot" + i + "sender");
								if (str != null) {
									list.add("§2Absender: §6" + this.plugin.getbox()
											.getString(player.getName() + ".slot" + i + "sender"));
								} else {
									list.add(
											"§2Absender: §6UnexpectedCall: java.lang.Thread(Unknown)$org.JavaPlugin.load(Unkown)$xyz.joestr.postbox.PostBox(Unknown)");
								}

								meta.setLore(list);
								itemstack.setItemMeta(meta);

								this.plugin.postbox.setItem(i, itemstack);
							}

							player.openInventory(this.plugin.postbox);
							return true;
						}

						player.sendMessage("§2--- PostBox ---");
						player.sendMessage("§6Du hast keine Berechtigung.");
						return true;
					}
					if (arg.length == 2) {
						if (player.hasPermission("post.admin")) {
							int counter;

							try {
								counter = this.plugin.getbox().getInt(arg[1] + ".count");
							} catch (Exception e) {
								counter = 0;
							}

							if (counter == 0) {
								player.sendMessage("§2--- PostBox ---");
								player.sendMessage("§6Die PostBox von §2" + arg[1] + " §6ist leer.");
								return true;
							}

							this.plugin.isAdminMode.put(player.getName(), arg[1]);

							ItemStack itemstack = null;
							this.plugin.postbox = player.getPlayer().getServer().createInventory(null,
									this.plugin.getConfig().getInt("default_postbox_size"), "§6[§2PostBox§6]");

							for (int i = 0; i < counter; i++) {
								itemstack = this.plugin.getbox().getItemStack(arg[1] + ".slot" + i);

								if (itemstack == null) {
									itemstack = new ItemStack(Material.AIR);
								}

								ItemMeta meta = itemstack.getItemMeta();

								List<String> list = new ArrayList<String>();
								list.add("§2Absender: §6"
										+ this.plugin.getbox().getString(arg[1] + ".slot" + i + "sender"));
								meta.setLore(list);
								itemstack.setItemMeta(meta);

								this.plugin.postbox.setItem(i, itemstack);
							}

							player.openInventory(this.plugin.postbox);
							return true;
						}

						player.sendMessage("§2--- PostBox ---");
						player.sendMessage("§6Du hast keine Berechtigung.");
						return true;
					}

					PRINT_HELP("open", player);
					return true;
				}
				if (arg[0].equalsIgnoreCase("send")) {
					if (arg.length == 2) {
						if ((player.hasPermission("post.use")) || (player.hasPermission("post.admin"))) {
							if (player.getName().equals(arg[1])) {
								sender.sendMessage("§2--- PostBox ---");
								sender.sendMessage("§6Du kannst dir selbst keine Post senden.");
								return true;
							}

							int counter;

							try {
								counter = this.plugin.getbox().getInt(arg[1] + ".count");
							} catch (Exception e) {
								counter = -1;
							}

							if ((counter > -1) && (counter < 9)) {
								ItemStack itemstack = null;
								itemstack = player.getInventory().getItemInMainHand();
								this.plugin.isAdminMode.remove(player.getName());

								try {
									player.getInventory().clear(player.getInventory().first(itemstack));
								} catch (Exception e) {
									player.sendMessage("§2--- PostBox ---");
									player.sendMessage("§6Du kannst nichts Leeres verschicken.");
									return true;
								}

								this.plugin.getbox().set(arg[1] + ".count", Integer.valueOf(counter + 1));
								this.plugin.getbox().set(arg[1] + ".slot" + counter, itemstack);
								this.plugin.getbox().set(arg[1] + ".slot" + counter + "sender", player.getName());
								this.plugin.savebox();

								player.sendMessage("§2--- PostBox ---");
								player.sendMessage("§6Du hast Post an §2" + arg[1] + " §6verschickt.");

								@SuppressWarnings("deprecation")
								OfflinePlayer target = Bukkit.getServer().getOfflinePlayer(arg[1]);
								if (target.isOnline()) {
									if ((target.getPlayer().hasPermission("post.use"))
											|| (target.getPlayer().hasPermission("post.admin"))) {
										target.getPlayer().spigot().sendMessage(

												new ComponentBuilder("--- PostBox --- (Optionen anklickbar)")
														.color(ChatColor.DARK_GREEN)
														.append("\n>> Du hast Post erhalten. Klicke hier um deine PostBox zu Ã¶ffnen.")
														.color(ChatColor.GOLD)
														.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
																new ComponentBuilder("/post open")
																		.color(ChatColor.DARK_GREEN).create()))
														.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
																"/post open"))
														.create());

										return true;
									}
									return true;
								}
								return true;
							}
							player.sendMessage("§2--- PostBox ---");
							player.sendMessage("§6Die PostBox von §2" + arg[1] + " §6ist voll.");
							return true;
						}
						player.sendMessage("§2--- PostBox ---");
						player.sendMessage("§6Du hast keine Berechtigung.");
						return true;
					}
					PRINT_HELP("send", player);
					return true;
				}
				PRINT_HELP("*", player);
				return true;
			}
			PRINT_HELP("*", player);
			return true;
		}
		sender.sendMessage("§2--- PostBox ---");
		sender.sendMessage("§6Die Konsole darf diesen Befehl nicht ausführen.");
		return true;
	}

	@EventHandler
	public void onNoPermission(PlayerCommandPreprocessEvent e) {
		Player p = e.getPlayer();
		if (e.getMessage().equalsIgnoreCase("/post")) {
			if ((!p.hasPermission("post.use")) || (!p.hasPermission("post.admin"))) {
				p.sendMessage("§2--- PostBox ---");
				p.sendMessage("§6Du hast keine Berechtigung.");

				e.setCancelled(true);
			}
		}
	}

	public void PRINT_HELP(String mode, Player player) {
		if ((player.hasPermission("post.use")) || (player.hasPermission("post.admin"))) {
			if (mode.equalsIgnoreCase("*")) {
				player.spigot().sendMessage(

						new ComponentBuilder("--- PostBox --- (Optionen anklickbar)").color(ChatColor.DARK_GREEN)
								.append("\n>> Deine PostBox öffnen.").color(ChatColor.GOLD)
								.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
										new ComponentBuilder("/post open").color(ChatColor.DARK_GREEN).create()))
								.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/post open")).create());
				if (player.hasPermission("post.admin")) {
					player.spigot().sendMessage(

							new ComponentBuilder(">> PostBox eines Spielers öffnen.").color(ChatColor.GOLD)
									.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
											new ComponentBuilder("/post open <Spieler>").color(ChatColor.DARK_GREEN)
													.create()))
									.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/post open ")).create());
				}
				player.spigot().sendMessage(

						new ComponentBuilder(">> Gegenstand in der Hand an Spieler senden.").color(ChatColor.GOLD)
								.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
										new ComponentBuilder("/post send <Spieler>").color(ChatColor.DARK_GREEN)
												.create()))
								.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/post send ")).create());
			} else if (mode.equalsIgnoreCase("open")) {
				player.spigot().sendMessage(

						new ComponentBuilder("--- PostBox --- (Optionen anklickbar)").color(ChatColor.DARK_GREEN)
								.append("\n>> Deine PostBox öffnen.").color(ChatColor.GOLD)
								.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
										new ComponentBuilder("/post open").color(ChatColor.DARK_GREEN).create()))
								.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/post open")).create());
				if (player.hasPermission("post.admin")) {
					player.spigot().sendMessage(

							new ComponentBuilder(">> PostBox eines Spielers öffnen.").color(ChatColor.GOLD)
									.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
											new ComponentBuilder("/post open <Spieler>").color(ChatColor.DARK_GREEN)
													.create()))
									.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/post open ")).create());
				}
			} else if (mode.equalsIgnoreCase("send")) {
				player.spigot().sendMessage(

						new ComponentBuilder("--- PostBox --- (Optionen anklickbar)").color(ChatColor.DARK_GREEN)
								.append("\n>> Gegenstand in der Hand an Spieler senden.").color(ChatColor.GOLD)
								.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
										new ComponentBuilder("/post send <Spieler>").color(ChatColor.DARK_GREEN)
												.create()))
								.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/post send ")).create());
			}
		} else {
			player.sendMessage("§2--- PostBox ---");
			player.sendMessage("§6Du hast keine Berechtigung.");
		}
	}
}
