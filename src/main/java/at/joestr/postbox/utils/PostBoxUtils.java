//
// MIT License
//
// Copyright (c) 2022 Joel Strasser
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//
package at.joestr.postbox.utils;

import at.joestr.postbox.PostBoxPlugin;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.enginehub.squirrelid.Profile;

/**
 * @author joestr
 */
public class PostBoxUtils {

  public static CompletableFuture<String> resolveUniqueId(UUID uuid) {
    if (PostBoxPlugin.getInstance().getLuckPermsApi() != null) {
      return PostBoxPlugin.getInstance().getLuckPermsApi().getUserManager().lookupUsername(uuid);
    }

    return CompletableFuture.supplyAsync(
      () -> {
        String result = null;
        try {
          Profile cachedProfile = PostBoxPlugin.getInstance().getProfileCache().getIfPresent(uuid);
          if (cachedProfile == null) {
            Profile p = PostBoxPlugin.getInstance().getProfileResolver().findByUuid(uuid);
            cachedProfile = PostBoxPlugin.getInstance().getProfileCache().getIfPresent(uuid);
          }
          result = cachedProfile.getName();
        } catch (IOException ex) {
          Logger.getLogger(PostBoxUtils.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
          Logger.getLogger(PostBoxUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
      });
  }

  public static CompletableFuture<UUID> resolveName(String name) {
    if (PostBoxPlugin.getInstance().getLuckPermsApi() != null) {
      return PostBoxPlugin.getInstance().getLuckPermsApi().getUserManager().lookupUniqueId(name);
    }

    return CompletableFuture.supplyAsync(
      () -> {
        UUID result = null;
        try {
          Profile p = PostBoxPlugin.getInstance().getProfileResolver().findByName(name);
          Profile cachedProfile = PostBoxPlugin.getInstance().getProfileCache().getIfPresent(p.getUniqueId());
          result = cachedProfile.getUniqueId();
        } catch (IOException ex) {
          Logger.getLogger(PostBoxUtils.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
          Logger.getLogger(PostBoxUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
      });
  }
}
