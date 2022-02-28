# Configuration
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
  
## Settings
`version`: Version of the plugin.  
`jdbcUri`: The URI for the database connection.  
`size`: The size of the inventory. Also the maximum of items a player can have in its PostBox.
`updater`: The section for the updater.  
  
## Updater
`enabled`: If the updater is active.  
`downloadToPluginUpdateFolder`: Downloads the update to the plugin update folder.  
`targetUrl`: The URL for fetching updates.  
`pomPropertiesFile`: Where the meta-file `pom.properties` is located (on the `targetUrl`).  
`classifier`: A classifier added to the jar.  
  
With the default settings a download URL can look like `https://github.com/joestrhq/PostBox/latest/download/postbox-1.0.0-shaded.jar`.