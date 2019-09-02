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
            sender.sendMessage(ChatColor.RED + "Dieser Befehl kann nur von Spielern ausgeführt werden!");
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
                    player.sendMessage(ChatColor.Red + "Du hast keine Berechtigung.");
                    return true;
                }

                int counter;

                try {
                    counter = this.plugin.getbox().getInt(player.getUniqueId().toString() + ".count");
                } catch (Exception e) {
                    counter = 0;
                }

                if (counter == 0) {
                    player.sendMessage(ChatColor.AQUA + "Deine PostBox ist leer.");
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
                    player.sendMessage(ChatColor.RED + "Du hast keine Berechtigung.");
                    return true;
                }

                int counter;

                // preventing trash accounts
                OfflinePlayer receiver = Bukkit.getOfflinePlayer(arg[1]);

                if (!receiver.hasPlayedBefore()) {
                    sender.sendMessage(ChatColor.GRAY + arg[1] + ChatColor.AQUA + " hat noch nie zuvor auf diesem Server gespielt.");
                    return true;
                }

                try {
                    counter = this.plugin.getbox().getInt(receiver.getUniqueId() + ".count");
                } catch (Exception e) {
                    counter = 0;
                }

                if (counter == 0) {
                    player.sendMessage(ChatColor.AQUA + "Die PostBox von " + ChatColor.GRAY + receiver.getName() + ChatColor.AQUA + " ist leer.");
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
                    player.sendMessage(ChatColor.RED + "Du hast keine Berechtigung.");
                    return true;
                }

                if (player.getName().equals(arg[1])) {
                    sender.sendMessage(ChatColor.RED + "Du kannst dir selbst keine Post senden.");
                    return true;
                }

                // preventing trash accounts
                OfflinePlayer receiver = Bukkit.getOfflinePlayer(arg[1]);

                if (!receiver.hasPlayedBefore()) {
                    sender.sendMessage(ChatColor.GRAY + arg[1] + ChatColor.AQUA + " hat noch nie zuvor auf diesem Server gespielt.");
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
                        player.sendMessage(ChatColor.RED + "Du kannst nichts Leeres verschicken.");
                        return true;
                    }

                    this.plugin.getbox().set(receiver.getUniqueId().toString() + ".count", counter + 1);
                    this.plugin.getbox().set(receiver.getUniqueId().toString() + ".slot" + counter, this.plugin.itemStackBase64.toBase64(itemstack));
                    this.plugin.getbox().set(receiver.getUniqueId().toString() + ".slot" + counter + "sender", player.getUniqueId().toString());
                    this.plugin.savebox();

                    player.sendMessage(ChatColor.AQUA + "Du hast Post an " + ChatColor.GRAY + arg[1] + ChatColor.AQUA + " verschickt.");

                    if (receiver.isOnline()) {
                        if ((receiver.getPlayer().hasPermission("post.use"))
                            || (receiver.getPlayer().hasPermission("post.admin"))) {
                            receiver.getPlayer().spigot().sendMessage(
                                new ComponentBuilder(ChatColor.AQUA + "Du hast Post erhalten. Klicke hier um deine PostBox zu öffnen.")
                                    .color(ChatColor.GRAY)
                                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                        new ComponentBuilder("/post open")
                                            .color(ChatColor.GRAY).create()))
                                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                        "/post open"))
                                    .create());

                            return true;
                        }

                        return true;
                    }

                    return true;
                }

                player.sendMessage(ChatColor.AQAU + "Die PostBox von " + ChatColor.GRAY + arg[1] + ChatColor.AQUA + " ist voll.");
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
                    new ComponentBuilder(ChatColor.AQUA + "------- " + ChatColor.GRAY + "PostBox (anklickbar)" + ChatColor.AQUA +  " -------")
                        .color(ChatColor.AQUA)
                        .append("\n » " + ChatColor.GRAY + "Deine PostBox öffnen.").color(ChatColor.AQUA)
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            new ComponentBuilder("/post open").color(ChatColor.GRAY).create()))
                        .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/post open")).create());
                if (player.hasPermission("post.admin")) {
                    player.spigot().sendMessage(
                        new ComponentBuilder(" » " + ChatColor.GRAY + "PostBox eines Spielers öffnen.").color(ChatColor.AQUA)
                            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                new ComponentBuilder("/post open <Spieler>").color(ChatColor.GRAY)
                                    .create()))
                            .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/post open ")).create());
                }
                player.spigot().sendMessage(
                    new ComponentBuilder(" » " + ChatColor.GRAY + "Gegenstand in der Hand an Spieler senden.").color(ChatColor.AQUA)
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            new ComponentBuilder("/post send <Spieler>").color(ChatColor.GRAY)
                                .create()))
                        .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/post send ")).create());
            } else if (mode.equalsIgnoreCase("open")) {
                player.spigot().sendMessage(
                    new ComponentBuilder(ChatColor.AQUA + "------- " + ChatColor.GRAY + "PostBox (anklickbar)" + ChatColor.AQUA +  " -------").color(ChatColor.DARK_GREEN)
                        .append("\n » " + ChatColor. GRAY + "Deine PostBox öffnen.").color(ChatColor.AQUA)
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            new ComponentBuilder("/post open").color(ChatColor.GRAY).create()))
                        .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/post open")).create());
                if (player.hasPermission("post.admin")) {
                    player.spigot().sendMessage(
                        new ComponentBuilder(" » " + ChatColor.GRAY + "PostBox eines Spielers öffnen.").color(ChatColor.AQUA)
                            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                new ComponentBuilder("/post open <Spieler>").color(ChatColor.GRAY)
                                    .create()))
                            .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/post open ")).create());
                }
            } else if (mode.equalsIgnoreCase("send")) {
                player.spigot().sendMessage(
                    new ComponentBuilder(ChatColor.AQUA + "------- " + ChatColor.GRAY + "PostBox (anklickbar)" + ChatColor.AQUA +  " -------").color(ChatColor.DARK_GREEN)
                        .append(" » " + ChatColor.GRAY + "Gegenstand in der Hand an Spieler senden.").color(ChatColor.AQUA)
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            new ComponentBuilder("/post send <Spieler>").color(ChatColor.GRAY)
                                .create()))
                        .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/post send ")).create());
            }
        } else {
            player.sendMessage(ChatColor.RED + "Du hast keine Berechtigung.");
        }
    }
}
