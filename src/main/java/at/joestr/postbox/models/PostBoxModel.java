/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package at.joestr.postbox.models;

import at.joestr.postbox.utils.Base64Objectifier;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.util.UUID;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

/**
 *
 * @author joestr
 */
@DatabaseTable(tableName = "postbox")
public class PostBoxModel {
  @DatabaseField(uniqueIndexName = "uq_player+slot")
  private UUID player;
  @DatabaseField(uniqueIndexName = "uq_player+slot")
  private int slot;
  @DatabaseField
  private String base64ItemStack;
  
  public Base64Objectifier<ItemStack> itemStackBase64
    = new Base64Objectifier<>(BukkitObjectOutputStream.class, BukkitObjectInputStream.class);

  public PostBoxModel() {
  }

  public PostBoxModel(UUID player, int count, String base64ItemStacks) {
    this.player = player;
    this.slot = count;
    this.base64ItemStack = base64ItemStacks;
  }

  public UUID getPlayer() {
    return player;
  }

  public void setPlayer(UUID player) {
    this.player = player;
  }

  public int getCount() {
    return slot;
  }

  public void setCount(int count) {
    this.slot = count;
  }

  public String getBase64ItemStacks() {
    return base64ItemStack;
  }

  public void setBase64ItemStacks(String base64ItemStacks) {
    this.base64ItemStack = base64ItemStacks;
  }
  
  public ItemStack getItemStack() {
    return this.itemStackBase64.fromBase64(base64ItemStack);
  }
  
  public void setItemStack(ItemStack itemStack) {
    this.base64ItemStack = this.itemStackBase64.toBase64(itemStack);
  }
}
