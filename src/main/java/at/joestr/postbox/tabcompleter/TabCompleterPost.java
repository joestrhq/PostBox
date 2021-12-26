package at.joestr.postbox.tabcompleter;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import at.joestr.postbox.PostBoxPlugin;

public class TabCompleterPost implements TabCompleter {
    //private PostBox plugin;

    public TabCompleterPost(PostBoxPlugin postbox) {
        //this.plugin = postbox;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String string, String[] arg) {
        List<String> list = new ArrayList<String>();
        List<String> l = new ArrayList<String>();
        if ((sender instanceof Player)) {
            Player player = ((Player) sender).getPlayer();
            if (arg.length <= 1) {
                if ((player.hasPermission("post.admin")) || (player.hasPermission("post.use"))) {
                    list.add("open");
                    list.add("send");

                    if (arg.length == 1) {
                        for (String key : list) {
                            if (key.startsWith(arg[0])) {
                                l.add(key);
                            }
                        }
                        list = l;
                    }

                    return list;
                }
                return list;
            }
            if (arg.length <= 2) {
                if (player.hasPermission("post.admin")) {
                    for (Player pl : Bukkit.getOnlinePlayers()) {
                        list.add(pl.getName());
                    }
                    if (arg.length == 2) {
                        for (String key : list) {
                            if (key.startsWith(arg[1])) {
                                l.add(key);
                            }
                        }
                        list = l;
                    }
                    return list;
                }
                if (player.hasPermission("post.use")) {
                    if (arg[0].equalsIgnoreCase("open")) {
                        return list;
                    }
                    if (arg[0].equalsIgnoreCase("send")) {
                        for (Player pl : Bukkit.getOnlinePlayers()) {
                            list.add(pl.getName());
                        }
                        if (arg.length == 2) {
                            for (String key : list) {
                                if (key.startsWith(arg[1])) {
                                    l.add(key);
                                }
                            }
                            list = l;
                        }
                        return list;
                    }
                    return list;
                }
                return list;
            }
            return list;
        }
        return list;
    }
}
