package at.joestr.postbox;

import at.joestr.postbox.commands.CommandPostOpen;
import at.joestr.postbox.commands.CommandPostUpdate;
import at.joestr.postbox.commands.CommandPostSend;
import java.util.HashMap;
import java.util.UUID;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import at.joestr.postbox.commands.CommandPost;
import at.joestr.postbox.commands.CommandPostOpenOther;
import at.joestr.postbox.configuration.AppConfiguration;
import at.joestr.postbox.configuration.CurrentEntries;
import at.joestr.postbox.configuration.LanguageConfiguration;
import at.joestr.postbox.configuration.Updater;
import at.joestr.postbox.event.InventoryClickListener;
import at.joestr.postbox.event.InventoryCloseListener;
import at.joestr.postbox.event.PlayerJoinListener;
import at.joestr.postbox.models.PostBoxModel;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.apache.commons.lang3.tuple.Triple;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.inventory.Inventory;

public class PostBoxPlugin extends JavaPlugin implements Listener {
  private static final Logger LOG = Logger.getLogger(PostBoxPlugin.class.getName());
  public static PostBoxPlugin instance = null;

  private Dao<PostBoxModel, String> postBoxDao;
  private HashMap<String, TabExecutor> commandMap;
  private Updater updater;
  private LuckPerms luckPermsApi;
  private ArrayList<Triple<UUID, Inventory, UUID>> inventoryMappings = new ArrayList<>();
  
  @Override
  public void onLoad() {
    super.onLoad();
    instance = this;
  }

  @Override
  public void onEnable() {
    super.onEnable();
    
    this.commandMap = new HashMap<>();
    
    this.loadAppConfiguration();
    this.loadLanguageConfiguration();
    try {
			this.loadDatabase();
		} catch (SQLException ex) {
			this.getLogger().log(Level.SEVERE, "Error whilst loading database!", ex);
			this.getServer().getPluginManager().disablePlugin(this);
		}
    this.loadExternalPluginIntegrations();
    
    this.updater = new Updater(
			AppConfiguration.getInstance().getBool(CurrentEntries.CONF_UPDATER_ENABLED.toString()),
			AppConfiguration.getInstance().getBool(CurrentEntries.CONF_UPDATER_DOWNLOADTOPLUGINUPDATEFOLDER.toString()),
			this.getDescription().getVersion(),
			AppConfiguration.getInstance().getString(CurrentEntries.CONF_UPDATER_TARGETURL.toString()),
			AppConfiguration.getInstance().getString(CurrentEntries.CONF_UPDATER_POMPROPERTIESFILE.toString()),
			AppConfiguration.getInstance().getString(CurrentEntries.CONF_UPDATER_CLASSIFIER.toString()),
			Bukkit.getUpdateFolderFile()
		);
    
    this.commandMap.put("postbox", new CommandPost());
    this.commandMap.put("postbox-open", new CommandPostOpen());
    this.commandMap.put("postbox-openother", new CommandPostOpenOther());
    this.commandMap.put("postbox-send", new CommandPostSend(this));
    this.commandMap.put("postbox-update", new CommandPostUpdate(this));
    
    this.registerCommands();
    this.registerListeners();
  }

  @Override
  public void onDisable() {
    super.onDisable();
    
    this.postBoxDao.getConnectionSource().closeQuietly();
  }
  
  private void registerCommands() {
    this.commandMap.forEach((s, e) -> {
			PluginCommand pluginCommand = getCommand(s);
			if (pluginCommand == null) {
				return;
			}
			pluginCommand.setExecutor(e);
			pluginCommand.setTabCompleter(e);
		});
  }
  
  private void registerListeners() {
    this.getServer().getPluginManager().registerEvents(new InventoryClickListener(), this);
		this.getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
    this.getServer().getPluginManager().registerEvents(new InventoryCloseListener(), this);
  }
  
  private void loadAppConfiguration() {
		InputStream bundledConfig = this.getClass().getResourceAsStream("config.yml");
		File externalConfig = new File(this.getDataFolder(), "config.yml");

		try {
			AppConfiguration.getInstance(externalConfig, bundledConfig);
		} catch (IOException ex) {
			this.getLogger().log(
				Level.SEVERE, "Error whilst intialising the plugin configuration!", ex
			);
			this.getServer().getPluginManager().disablePlugin(this);
		}
	}
  
  private void loadLanguageConfiguration() {
		Map<String, InputStream> bundledLanguages = new HashMap<>();
		bundledLanguages.put("en.yml", this.getClass().getResourceAsStream("languages/en.yml"));
		bundledLanguages.put("de.yml", this.getClass().getResourceAsStream("languages/de.yml"));
		File externalLanguagesFolder = new File(this.getDataFolder(), "languages");

		try {
			LanguageConfiguration.getInstance(externalLanguagesFolder, bundledLanguages, new Locale("en"));
		} catch (IOException ex) {
			this.getLogger().log(
				Level.SEVERE, "Error whilst intialising the language configuration!", ex
			);
			this.getServer().getPluginManager().disablePlugin(this);
		}
	}

  private void loadDatabase() throws SQLException {
		ConnectionSource connectionSource
			= new JdbcConnectionSource(
				AppConfiguration.getInstance().getString("jdbcUri")
			);

		this.postBoxDao = DaoManager.createDao(connectionSource, PostBoxModel.class);

		TableUtils.createTableIfNotExists(connectionSource, PostBoxModel.class);
	}
  
  private void loadExternalPluginIntegrations() {
    try {
      this.luckPermsApi = LuckPermsProvider.get();
    } catch (IllegalStateException ex) {
      LOG.log(Level.WARNING, "LuckPerms API not found. Using conventional method for resolving player names!");
    }
  }
  
  public static PostBoxPlugin getInstance() {
    return instance;
  }

  public Dao<PostBoxModel, String> getPostBoxDao() {
    return postBoxDao;
  }

  public HashMap<String, TabExecutor> getCommandMap() {
    return commandMap;
  }

  public Updater getUpdater() {
    return updater;
  }

  public LuckPerms getLuckPermsApi() {
    return luckPermsApi;
  }

  public ArrayList<Triple<UUID, Inventory, UUID>> getInventoryMappings() {
    return inventoryMappings;
  }
}
