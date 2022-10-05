# Configuration

## Plugin

The configuration file is located in `plugins/PostBox/config.yml`.  
  
The default configuration file looks like this:  
  
```
version: 1
jdbcUri: 'jdbc:sqlite:./plugins/PostBox/postbox.db'
size: 9
# Updater config
updater:
  enabled: true
  downloadToPluginUpdateFolder: true
  targetUrl: 'https://github.com/joestrhq/PostBox/releases/latest/download/'
  pomPropertiesFile: 'pom.properties'
  classifier: 'shaded'
```
  
### Settings

`version`: Version of the plugin.  
`jdbcUri`: The URI for the database connection.  
`size`: The size of the inventory. Also the maximum of items a player can have in its PostBox.
`updater`: The section for the updater.  
  
### Updater

`enabled`: If the updater is active.  
`downloadToPluginUpdateFolder`: Downloads the update to the plugin update folder.  
`targetUrl`: The URL for fetching updates.  
`pomPropertiesFile`: Where the meta-file `pom.properties` is located (on the `targetUrl`).  
`classifier`: A classifier added to the jar.  
  
With the default settings a download URL can look like `https://github.com/joestrhq/PostBox/latest/download/postbox-1.0.0-shaded.jar`.  

## Language

The default language configurations are located in folder `plugins/PostBox/languages/`.  
  
The default language configuration file for English looks like this:  
  
```
version: 3
prefix: ',{"text":"[","color":"dark_gray"},{"text":"PostBox","color":"dark_green"},{"text":"]","color":"dark_gray"},{"text":" "}'
generic:
  not_a_player: '[""%prefix,{"text":"This command can only be used by players!","color":"red"}]'
commands:
  postbox:
    message_send: '[""%prefix,{"text":"» ","color":"gray"},{"text":"Send the item you are currently holding in your hand","color":"gray","clickEvent":{"action":"suggest_command","value":"/postbox-send "},"hoverEvent":{"action":"show_text","value":{"text":"/postbox-send <Player name>","color":"white"}}}]'
    message_open: '[""%prefix,{"text":"» ","color":"gray"},{"text":"Open your PostBox","color":"gray","clickEvent":{"action":"run_command","value":"/postbox-open"},"hoverEvent":{"action":"show_text","value":{"text":"/postbox-open","color":"white"}}}]'
    message_openother: '[""%prefix,{"text":"» ","color":"gray"},{"text":"Open another players PostBox","color":"gray","clickEvent":{"action":"suggest_command","value":"/postbox-openother "},"hoverEvent":{"action":"show_text","value":{"text":"/postbox-openother <Player name>","color":"white"}}}]'
    message_update: '[""%prefix,{"text":"» ","color":"gray"},{"text":"Check for an update","color":"gray","clickEvent":{"action":"run_command","value":"/postbox-update"},"hoverEvent":{"action":"show_text","value":{"text":"/postbox-update","color":"white"}}}]'
  postbox-send:
    self: '[""%prefix,{"text":"You cannot send items to yourself!","color":"red"}]'
    receiver_never_played: '[""%prefix,{"text":"The player ","color":"red"},{"text":"%playername","color":"white"},{"text":" has never played before!","color":"red"}]'
    send_empty: '[""%prefix,{"text":"You cannot send nothing!","color":"red"}]'
    success_sender: '[""%prefix,{"text":"The item has been sent to ","color":"green"},{"text":"%playername","color":"white"},{"text":"!","color":"green"}]'
    success_receiver: '[""%prefix,{"text":"You received mail. Click here to open your PostBox!","color":"aqua","clickEvent":{"action":"run_command","value":"/postbox-open"},"hoverEvent":{"action":"show_text","value":{"text":"/postbox-open","color":"white"}}}]'
    receipient_full: '[""%prefix,{"text":"The PostBox of ","color":"red"},{"text":"%playername","color":"white"},{"text":" is already full!","color":"red"}]'
  postbox-open:
    chest_title: "&x&d&4&1&c&6&3Your PostBox"
    empty: '[""%prefix,{"text":"Your PostBox is empty!","color":"red"}]'
    playername_resolving_error: '[""%prefix,{"text":"Error whilst resolving player names!","color":"red"}]'
    itemlore: '&x&d&4&1&c&6&3From: %playername'
  postbox-openother:
    chest_title: "&x&d&4&1&c&6&3PostBox of %playername"
    empty: '[""%prefix,{"text":"The PostBox of ","color":"red"},{"text":"%playername","color":"white"},{"text":" is empty!","color":"red"}]'
    playername_resolving_error: '[""%prefix,{"text":"Error whilst resolving player names!","color":"red"}]'
    itemlore: '&x&d&4&1&c&6&3From: %playername'
  postbox-update:
    false: '[""%prefix,{"text":"Updates are turned off!","color":"red"}]'
    asyncstart: '[""%prefix,{"text":"Checking for updates ...","color":"aqua"}]'
    error: '[""%prefix,{"text":"An error occoured during download!","color":"red"}]'
    uptodate: '[""%prefix,{"text":"You are already using the latest version.","color":"green"}]'
    available: '[""%prefix,{"text":"A new version is available ","color":"green"},{"text":"here","underlined":true,"color":"gray","clickEvent":{"action":"open_url","value":"%update$downloadUrl"},"hoverEvent":{"action":"show_text","value":{"text":"%update$downloadUrl","color":"white"}}},{"text":".","color":"green"}]'
    downloaded: '[""%prefix,{"text":"The new version is in the update-folder!","color":"green"}]'
events:
  message_on_join: '[""%prefix,{"text":"You have received mail. Click here to open your PostBox!","color":"aqua","clickEvent":{"action":"run_command","value":"/postbox-open"},"hoverEvent":{"action":"show_text","value":{"text":"/postbox-open","color":"white"}}}]'
  inventory_full: '[""%prefix,{"text":"Your inventory is full!","color":"red"}]'
```
