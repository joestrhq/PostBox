package xyz.joestr.postbox.command;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.inventory.Inventory;
import xyz.joestr.postbox.PostBoxPlugin;

public class CommandPost implements CommandExecutor {

    private PostBoxPlugin plugin;
    public static HoverEvent.Action HEA;
    public static ClickEvent.Action CEA;
    public static ChatColor CC;

    public CommandPost(PostBoxPlugin postbox) {
        this.plugin = postbox;
    }

    public boolean onCommand(CommandSender sender, Command command, String string, String[] arg) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§2--- PostBox ---");
            sender.sendMessage("§6Dieser Befehl kann nur von Spielern ausgeführt werden!");
            return true;
        }

        // Sender ist ein Spieler
        Player player = (Player) sender;

        // /post
        if (arg.length == 0) {
            PRINT_HELP("*", player);
            return true;
        }

        // /post open
        if (arg[0].equalsIgnoreCase("open")) {

            // /post open
            if (arg.length == 1) {
                if (!player.hasPermission("post.use")) {
                    player.sendMessage("§2--- PostBox ---");
                    player.sendMessage("§6Du hast keine Berechtigung.");
                    return true;
                }

                int counter;

                try {
                    counter = this.plugin.getbox().getInt(player.getUniqueId().toString() + ".count");
                } catch (Exception e) {
                    counter = 0;
                }

                if (counter == 0) {
                    player.sendMessage("§2--- PostBox ---");
                    player.sendMessage("§6Deine PostBox ist leer.");
                    return true;
                }

                this.plugin.isAdminMode.remove(player.getUniqueId());

                ItemStack itemstack = null;
                Inventory localInventory = player.getPlayer().getServer().createInventory(
                    null,
                    this.plugin.getConfig().getInt("default_postbox_size"), "§6[§2PostBox§6]"
                );

                for (int i = 0; i < counter; i++) {
                    itemstack = this.plugin.itemStackBase64.fromBase64(
                        this.plugin.getbox().getString(player.getUniqueId().toString() + ".slot" + i)
                    );

                    if (itemstack == null) {
                        itemstack = new ItemStack(Material.AIR);
                    }

                    ItemMeta meta = itemstack.getItemMeta();
                    List<String> list = new ArrayList<String>();
                    String str = this.plugin.getbox().getString(player.getUniqueId().toString() + ".slot" + i + "sender");
                    if (str != null) {
                        list.add("§2Absender: §6"
                            + Bukkit.getOfflinePlayer(UUID.fromString(this.plugin.getbox().getString(player.getUniqueId().toString() + ".slot" + i + "sender"))).getName());
                    } else {
                        list.add(
                            "§2Absender: §6?");
                    }

                    meta.setLore(list);
                    itemstack.setItemMeta(meta);

                    localInventory.setItem(i, itemstack);
                }

                player.openInventory(localInventory);
                return true;
            }

            // /post open <Player>
            if (arg.length == 2) {
                if (!player.hasPermission("post.admin")) {
                    player.sendMessage("§2--- PostBox ---");
                    player.sendMessage("§6Du hast keine Berechtigung.");
                    return true;
                }

                int counter;

                // preventing trash accounts
                OfflinePlayer receiver = Bukkit.getOfflinePlayer(arg[1]);

                if (!receiver.hasPlayedBefore()) {
                    sender.sendMessage("§2--- PostBox ---");
                    sender.sendMessage("§2" + arg[1] + " §6hat noch nie zuvor auf diesem Server gespielt.");
                    return true;
                }

                try {
                    counter = this.plugin.getbox().getInt(receiver.getUniqueId() + ".count");
                } catch (Exception e) {
                    counter = 0;
                }

                if (counter == 0) {
                    player.sendMessage("§2--- PostBox ---");
                    player.sendMessage("§6Die PostBox von §2" + receiver.getName() + " §6ist leer.");
                    return true;
                }

                this.plugin.isAdminMode.put(player.getUniqueId(), receiver.getUniqueId());

                ItemStack itemstack = null;
                Inventory localInventory = player.getPlayer().getServer().createInventory(
                    null,
                    this.plugin.getConfig().getInt("default_postbox_size"), "§6[§2PostBox§6]"
                );

                for (int i = 0; i < counter; i++) {
                    itemstack = this.plugin.itemStackBase64.fromBase64(
                        this.plugin.getbox().getString(receiver.getUniqueId().toString() + ".slot" + i)
                    );

                    if (itemstack == null) {
                        itemstack = new ItemStack(Material.AIR);
                    }

                    ItemMeta meta = itemstack.getItemMeta();

                    List<String> list = new ArrayList<String>();
                    list.add("§2Absender: §6"
                        + Bukkit.getOfflinePlayer(UUID.fromString(this.plugin.getbox().getString(receiver.getUniqueId().toString() + ".slot" + i + "sender"))).getName());
                    meta.setLore(list);
                    itemstack.setItemMeta(meta);

                    localInventory.setItem(i, itemstack);
                }

                player.openInventory(localInventory);
                return true;

            }

            // Fall back in case of 3 arguments for example
            PRINT_HELP("open", player);
            return true;
        }

        // /post send
        if (arg[0].equalsIgnoreCase("send")) {

            // /post send <Player>
            if (arg.length == 2) {
                if (!player.hasPermission("post.use") && !player.hasPermission("post.admin")) {
                    player.sendMessage("§2--- PostBox ---");
                    player.sendMessage("§6Du hast keine Berechtigung.");
                    return true;
                }

                if (player.getName().equals(arg[1])) {
                    sender.sendMessage("§2--- PostBox ---");
                    sender.sendMessage("§6Du kannst dir selbst keine Post senden.");
                    return true;
                }

                // preventing trash accounts
                OfflinePlayer receiver = Bukkit.getOfflinePlayer(arg[1]);

                if (!receiver.hasPlayedBefore()) {
                    sender.sendMessage("§2--- PostBox ---");
                    sender.sendMessage("§2" + arg[1] + " §6hat noch nie zuvor auf diesem Server gespielt.");
                    return true;
                }

                int counter;

                try {
                    counter = this.plugin.getbox().getInt(receiver.getUniqueId().toString() + ".count");
                } catch (Exception e) {
                    counter = -1;
                }

                if ((counter > -1) && (counter < 9)) {
                    ItemStack itemstack = null;
                    itemstack = player.getInventory().getItemInMainHand();
                    this.plugin.isAdminMode.remove(player.getUniqueId());

                    try {
                        player.getInventory().clear(player.getInventory().first(itemstack));
                    } catch (Exception e) {
                        player.sendMessage("§2--- PostBox ---");
                        player.sendMessage("§6Du kannst nichts Leeres verschicken.");
                        return true;
                    }

                    this.plugin.getbox().set(receiver.getUniqueId().toString() + ".count", counter + 1);
                    this.plugin.getbox().set(receiver.getUniqueId().toString() + ".slot" + counter, this.plugin.itemStackBase64.toBase64(itemstack));
                    this.plugin.getbox().set(receiver.getUniqueId().toString() + ".slot" + counter + "sender", player.getUniqueId().toString());
                    this.plugin.savebox();

                    player.sendMessage("§2--- PostBox ---");
                    player.sendMessage("§6Du hast Post an §2" + arg[1] + " §6verschickt.");

                    if (receiver.isOnline()) {
                        if ((receiver.getPlayer().hasPermission("post.use"))
                            || (receiver.getPlayer().hasPermission("post.admin"))) {
                            receiver.getPlayer().spigot().sendMessage(
                                new ComponentBuilder("--- PostBox --- (Optionen anklickbar)")
                                    .color(ChatColor.DARK_GREEN)
                                    .append("\n>> Du hast Post erhalten. Klicke hier um deine PostBox zu öffnen.")
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

            PRINT_HELP("send", player);
            return true;
        }

        PRINT_HELP("*", player);
        return true;
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
