package at.joestr.postbox.event;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import at.joestr.postbox.PostBoxPlugin;

public class InventoryClickListener implements Listener {

    private PostBoxPlugin plugin;

    public InventoryClickListener(PostBoxPlugin postbox) {
        this.plugin = postbox;
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    // Highest Priority and ignore cancelled events
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void Inventory(InventoryClickEvent event) {

        Player player = (Player) event.getWhoClicked();
        String playerUUID = player.getUniqueId().toString();

        // Is not a postbox inventory?
        if (!player.getOpenInventory().getTitle().equalsIgnoreCase("�6[�2PostBox�6]") || (event.getClickedInventory() == null)) {
            return;
        }

        // Cancel interactions on the player inventory
        if (event.getClickedInventory().getType() == InventoryType.PLAYER) {
            event.setCancelled(true);
            return;
        }

        // redundant? to the first if statement?
        if (!event.getClickedInventory().getName().equalsIgnoreCase("�6[�2PostBox�6]")) {
            return;
        }

        if (player.getInventory().firstEmpty() == -1) { // Players' inventory is full
            event.setCancelled(true);

            player.closeInventory();

            player.sendMessage("�2---PostBox---");
            player.sendMessage("�6Dein Inventar ist voll.");
            return;

        } else if (this.plugin.isAdminMode.containsKey(player.getUniqueId())) { // Player interacts with a postbox from another player

            event.getInventory().remove(event.getCurrentItem());

            player.getInventory().addItem(
                new ItemStack[]{
                    this.plugin.itemStackBase64.fromBase64(
                        this.plugin.getbox().getString(this.plugin.isAdminMode.get(player.getUniqueId()).toString() + ".slot" + event.getRawSlot())
                    )
                }
            );

            int oldCount = this.plugin.getbox().getInt(this.plugin.isAdminMode.get(player.getUniqueId()).toString() + ".count");
            int newCount = oldCount - 1;

            for (int i = event.getRawSlot(); i < oldCount; i++) {
                this.plugin.getbox().set(
                    this.plugin.isAdminMode.get(player.getUniqueId()).toString() + ".slot" + i,
                    this.plugin.getbox().getString(
                        this.plugin.isAdminMode.get(player.getUniqueId()).toString() + ".slot" + (i + 1)
                    )
                );
                this.plugin.getbox().set(
                    this.plugin.isAdminMode.get(player.getUniqueId()).toString() + ".slot" + i + "sender",
                    this.plugin.getbox().getString(
                        this.plugin.isAdminMode.get(player.getUniqueId()).toString() + ".slot" + (i + 1) + "sender"
                    )
                );
            }
            this.plugin.getbox().set(
                this.plugin.isAdminMode.get(player.getUniqueId()).toString() + ".slot" + oldCount,
                null
            );
            this.plugin.getbox().set(
                this.plugin.isAdminMode.get(player.getUniqueId()).toString() + ".slot" + oldCount + "sender",
                null
            );

            this.plugin.getbox().set(
                this.plugin.isAdminMode.get(player.getUniqueId()).toString() + ".count",
                newCount
            );

            this.plugin.savebox();

            player.closeInventory();

            Bukkit.dispatchCommand(
                player,
                "post open " + Bukkit.getOfflinePlayer(this.plugin.isAdminMode.get(player.getUniqueId())).getName()
            );

        } else { // default case
            event.getInventory().remove(event.getCurrentItem());

            player.getInventory().addItem(
                new ItemStack[]{
                    this.plugin.itemStackBase64.fromBase64(
                        this.plugin.getbox().getString(playerUUID + ".slot" + event.getRawSlot())
                    )
                }
            );

            int oldCount = this.plugin.getbox().getInt(playerUUID + ".count");
            int newCount = oldCount - 1;

            for (int i = event.getRawSlot(); i < oldCount; i++) {
                this.plugin.getbox().set(
                    playerUUID + ".slot" + i,
                    this.plugin.getbox().getString(playerUUID + ".slot" + (i + 1))
                );
                this.plugin.getbox().set(
                    playerUUID + ".slot" + i + "sender",
                    this.plugin.getbox().getString(playerUUID + ".slot" + (i + 1) + "sender")
                );
            }

            this.plugin.getbox().set(
                player.getName() + ".slot" + oldCount,
                null
            );
            this.plugin.getbox().set(
                player.getName() + ".slot" + oldCount + "sender",
                null
            );

            this.plugin.getbox().set(playerUUID + ".count", newCount);

            this.plugin.savebox();

            player.closeInventory();
            Bukkit.dispatchCommand(player, "post open");
        }

    }
}
