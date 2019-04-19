package xyz.joestr.postbox;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.UUID;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import xyz.joestr.postbox.command.CommandPost;
import xyz.joestr.postbox.event.InventoryClickListener;
import xyz.joestr.postbox.event.PlayerJoinListener;
import xyz.joestr.postbox.tabcompleter.TabCompleterPost;
import xyz.joestr.postbox.util.Base64Objectifier;

public class PostBoxPlugin extends JavaPlugin implements Listener {

    public HashMap<UUID, UUID> isAdminMode = new HashMap<UUID, UUID>();
    private FileConfiguration postBox = null;
    private File postBoxFile = null;
    public Base64Objectifier<ItemStack> itemStackBase64
        = new Base64Objectifier<ItemStack>(BukkitObjectOutputStream.class, BukkitObjectInputStream.class);

    public void onEnable() {
        getCommand("post").setExecutor(new CommandPost(this));
        getCommand("post").setTabCompleter(new TabCompleterPost(this));

        new InventoryClickListener(this);
        new PlayerJoinListener(this);

        if (new File("plugins" + File.separator + "PostBox" + File.separator + "config.yml").exists()) {
            if ((getConfig().getInt("default_postbox_size") < 1) || (getConfig().getInt("default_postbox_size") > 54)) {
                getConfig().set("default_postbox_size", Integer.valueOf(9));
                saveConfig();
                reloadConfig();
            }
        }
        if ((getConfig().getBoolean("reset_file_on_start"))
            || (!new File("plugins" + File.separator + "PostBox" + File.separator + "config.yml").exists())) {
            File configFile = new File("plugins" + File.separator + "PostBox" + File.separator + "config.yml");
            configFile.delete();
            saveDefaultConfig();
            reloadConfig();
        }
        if ((getbox().getBoolean("reset_file_on_start"))
            || (!new File("plugins" + File.separator + "PostBox" + File.separator + "box.yml").exists())) {
            defaultbox();
            reloadbox();
        }
    }

    public void onDisable() {
    }

    public void reloadbox() {
        if (this.postBoxFile == null) {
            this.postBoxFile = new File(getDataFolder(), "box.yml");
        }
        this.postBox = YamlConfiguration.loadConfiguration(this.postBoxFile);

        Reader defConfigStream = null;
        try {
            defConfigStream = new InputStreamReader(getResource("box.yml"), "UTF8");
        } catch (UnsupportedEncodingException localUnsupportedEncodingException) {
        }
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            this.postBox.setDefaults(defConfig);
        }
    }

    public FileConfiguration getbox() {
        if (this.postBox == null) {
            reloadbox();
        }
        return this.postBox;
    }

    public void savebox() {
        if ((this.postBox == null) || (this.postBoxFile == null)) {
            return;
        }
        try {
            getbox().save(this.postBoxFile);
        } catch (IOException localIOException) {
        }
    }

    public void defaultbox() {
        if (this.postBoxFile == null) {
            this.postBoxFile = new File(getDataFolder(), "box.yml");
        }
        saveResource("box.yml", true);
    }
}
