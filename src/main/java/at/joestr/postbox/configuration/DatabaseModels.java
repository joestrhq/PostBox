// 
// MIT License
// Copyright (c) <year> <copyright holders>
// Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
// The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
// 
package at.joestr.postbox.configuration;

import at.joestr.postbox.utils.Base64Objectifier;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.time.LocalDateTime;
import java.util.UUID;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

/**
 *
 * @author joestr
 */
public class DatabaseModels {
  @DatabaseTable(tableName = "postbox")
  public static class PostBoxModel {
    @DatabaseField
    private UUID receiver;
    @DatabaseField
    private UUID sender;
    @DatabaseField
    private LocalDateTime timestamp;
    @DatabaseField
    private String base64ItemStack;

    public Base64Objectifier<ItemStack> itemStackBase64
      = new Base64Objectifier<>(BukkitObjectOutputStream.class, BukkitObjectInputStream.class);

    public PostBoxModel() {
    }

    public PostBoxModel(UUID player, String base64ItemStacks) {
      this.receiver = player;
      this.base64ItemStack = base64ItemStacks;
    }

    public UUID getReceiver() {
      return receiver;
    }

    public void setReceiver(UUID receiver) {
      this.receiver = receiver;
    }

    public UUID getSender() {
      return sender;
    }

    public void setSender(UUID sender) {
      this.sender = sender;
    }

    public LocalDateTime getTimestamp() {
      return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
      this.timestamp = timestamp;
    }
    
    public String getBase64ItemStack() {
      return base64ItemStack;
    }

    public void setBase64ItemStack(String base64ItemStacks) {
      this.base64ItemStack = base64ItemStacks;
    }

    public ItemStack getItemStack() {
      return this.itemStackBase64.fromBase64(base64ItemStack);
    }

    public void setItemStack(ItemStack itemStack) {
      this.base64ItemStack = this.itemStackBase64.toBase64(itemStack);
    }
  }

}
