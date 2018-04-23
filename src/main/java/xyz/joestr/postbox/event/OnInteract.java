package xyz.joestr.postbox.event;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import xyz.joestr.postbox.PostBox;

public class OnInteract implements Listener {
	private PostBox plugin;

	public OnInteract(PostBox postbox) {
		this.plugin = postbox;
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}

	@EventHandler
	public void Inventory(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		if ((player.getOpenInventory().getTitle() == "§6[§2PostBox§6]") && (event.getClickedInventory() != null)) {
			if (event.getClickedInventory().getType() == InventoryType.PLAYER) {
				event.setCancelled(true);
			}
			if (event.getClickedInventory().getName() == "§6[§2PostBox§6]") {
				if (player.getInventory().firstEmpty() == -1) {
					event.setCancelled(true);

					player.sendMessage("§2---PostBox---");
					player.sendMessage("§6Dein Inventar ist voll.");
				} else if (this.plugin.isAdminMode.containsKey(player.getName())) {
					ItemStack item = event.getCurrentItem();

					this.plugin.postbox.remove(event.getCurrentItem());

					ItemMeta meta = item.getItemMeta();

					meta.setLore(null);

					item.setItemMeta(meta);

					player.getInventory().addItem(new ItemStack[] { item });
					for (int i = event.getRawSlot(); i < this.plugin.getbox()
							.getInt((String) this.plugin.isAdminMode.get(player.getName()) + ".count"); i++) {
						this.plugin.getbox().set((String) this.plugin.isAdminMode.get(player.getName()) + ".slot" + i,
								this.plugin.getbox().getItemStack(
										(String) this.plugin.isAdminMode.get(player.getName()) + ".slot" + (i + 1)));
					}
					this.plugin.getbox().set((String) this.plugin.isAdminMode.get(player.getName()) + ".slot"
							+ (this.plugin.getbox()
									.getInt(new StringBuilder(
											String.valueOf((String) this.plugin.isAdminMode.get(player.getName())))
													.append(".count").toString())
									- 1),
							null);

					this.plugin.getbox().set((String) this.plugin.isAdminMode.get(player.getName()) + ".count",
							Integer.valueOf(this.plugin.getbox()
									.getInt((String) this.plugin.isAdminMode.get(player.getName()) + ".count") - 1));

					this.plugin.savebox();
				} else {
					ItemStack item = event.getCurrentItem();

					this.plugin.postbox.remove(event.getCurrentItem());

					ItemMeta meta = item.getItemMeta();

					meta.setLore(null);

					item.setItemMeta(meta);

					player.getInventory().addItem(new ItemStack[] { item });
					for (int i = event.getRawSlot(); i < this.plugin.getbox()
							.getInt(player.getName() + ".count"); i++) {
						this.plugin.getbox().set(player.getName() + ".slot" + i,
								this.plugin.getbox().getItemStack(player.getName() + ".slot" + (i + 1)));
					}

					this.plugin.getbox()
							.set(player.getName() + ".slot" + (this.plugin.getbox().getInt(
									new StringBuilder(String.valueOf(player.getName())).append(".count").toString())
									- 1), null);

					this.plugin.getbox().set(player.getName() + ".count",
							Integer.valueOf(this.plugin.getbox().getInt(player.getName() + ".count") - 1));

					this.plugin.savebox();
				}
			}
		}
	}
}
