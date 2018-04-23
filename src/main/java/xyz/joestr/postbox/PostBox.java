package xyz.joestr.postbox;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.joestr.postbox.command.PostCommand;
import xyz.joestr.postbox.event.OnInteract;
import xyz.joestr.postbox.event.OnJoin;
import xyz.joestr.postbox.tab.PostTab;

public class PostBox extends JavaPlugin implements Listener {
	public Inventory postbox = null;
	public HashMap<String, String> isAdminMode = new HashMap<String, String>();
	private FileConfiguration customConfig = null;
	private File customConfigFile = null;

	public void onEnable() {
		getCommand("post").setExecutor(new PostCommand(this));
		getCommand("post").setTabCompleter(new PostTab(this));
		new OnInteract(this);
		new OnJoin(this);
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
		if (this.customConfigFile == null) {
			this.customConfigFile = new File(getDataFolder(), "box.yml");
		}
		this.customConfig = YamlConfiguration.loadConfiguration(this.customConfigFile);

		Reader defConfigStream = null;
		try {
			defConfigStream = new InputStreamReader(getResource("box.yml"), "UTF8");
		} catch (UnsupportedEncodingException localUnsupportedEncodingException) {
		}
		if (defConfigStream != null) {
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			this.customConfig.setDefaults(defConfig);
		}
	}

	public FileConfiguration getbox() {
		if (this.customConfig == null) {
			reloadbox();
		}
		return this.customConfig;
	}

	public void savebox() {
		if ((this.customConfig == null) || (this.customConfigFile == null)) {
			return;
		}
		try {
			getbox().save(this.customConfigFile);
		} catch (IOException localIOException) {
		}
	}

	public void defaultbox() {
		if (this.customConfigFile == null) {
			this.customConfigFile = new File(getDataFolder(), "box.yml");
		}
		saveResource("box.yml", true);
	}
}
