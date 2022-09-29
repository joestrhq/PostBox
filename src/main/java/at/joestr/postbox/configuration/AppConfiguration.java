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
package at.joestr.postbox.configuration;

import at.joestr.postbox.PostBoxPlugin;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Joel
 */
public class AppConfiguration {

  private static AppConfiguration instance;
  private static final Logger LOG = Logger.getLogger(AppConfiguration.class.getSimpleName());

  private final YamlFileConfiguration external;
  private final YamlStreamConfiguration bundled;

  private AppConfiguration(File externalConfig, InputStream bundledConfig) throws IOException {
    this.bundled = new YamlStreamConfiguration(bundledConfig);
    if (!externalConfig.exists()) {
      externalConfig.getParentFile().mkdirs();
      this.bundled.saveConfigAsFile(externalConfig);
    }
    this.external = new YamlFileConfiguration(externalConfig);

    int bundledVersion = this.bundled.getInteger(CurrentEntries.CONF_VERSION.toString());

    int externalVersion = this.external.getInteger(CurrentEntries.CONF_VERSION.toString());

    if (bundledVersion != externalVersion) {
      boolean containsRequiredEntries = true;

      for (CurrentEntries currentEntry : CurrentEntries.getConfigurationEntries()) {
        if (!((YamlConfiguration) this.external).contains(currentEntry.toString())) {
          containsRequiredEntries = false;
        }
      }

      if (!containsRequiredEntries) {
        PostBoxPlugin.getInstance()
          .getLogger()
          .log(
            Level.WARNING,
            "The file {0} is missing some entries. Please check the documentation!",
            new Object[]{this.external.getConfigFile().getPath()});
      }
    }
  }

  /**
   * Handles the configuration of this app.
   *
   * @param externalConfigFile
   * @param bundledConfigStream
   * @return
   * @throws FileNotFoundException
   * @throws IOException
   */
  public static AppConfiguration getInstance(
    File externalConfigFile, InputStream bundledConfigStream) throws IOException {
    if (instance != null) {
      throw new RuntimeException("This class has already been instantiated.");
    }

    instance = new AppConfiguration(externalConfigFile, bundledConfigStream);

    return instance;
  }

  public static AppConfiguration getInstance() {
    if (instance == null) {
      throw new RuntimeException("This class has not been initialized yet.");
    }

    return instance;
  }

  private void logMissingExternalPathEntry(String path) {
    LOG.log(
      Level.WARNING,
      "The path {0} was not found in {1}. Using default path.",
      new Object[]{path, external.getConfigFile().getPath()});
  }

  public Boolean getBool(String path) {
    Boolean result = this.external.getBoolean(path);
    if (result != null) {
      return result;
    }
    this.logMissingExternalPathEntry(path);
    result = this.bundled.getBoolean(path);
    if (result != null) {
      return result;
    }
    return null;
  }

  public void setBool(String path, Boolean value) {
    this.external.setBoolean(path, value);
  }

  public Integer getInt(String path) {
    Integer result = this.external.getInteger(path);
    if (result != null) {
      return result;
    }
    this.logMissingExternalPathEntry(path);
    result = this.bundled.getInteger(path);
    if (result != null) {
      return result;
    }
    return null;
  }

  public void setInt(String path, Integer value) {
    this.external.setInteger(path, value);
  }

  public Double getDouble(String path) {
    Double result = this.external.getDouble(path);
    if (result != null) {
      return result;
    }
    this.logMissingExternalPathEntry(path);
    result = this.bundled.getDouble(path);
    if (result != null) {
      return result;
    }
    return null;
  }

  public void setDouble(String path, double value) {
    this.external.setDouble(path, value);
  }

  public Float getFloat(String path) {
    Float result = this.external.getFloat(path);
    if (result != null) {
      return result;
    }
    this.logMissingExternalPathEntry(path);
    result = this.bundled.getFloat(path);
    if (result != null) {
      return result;
    }
    return null;
  }

  public void setFloat(String path, float value) {
    this.external.setFloat(path, value);
  }

  public String getString(String path) {
    String result = this.external.getString(path);
    if (result != null) {
      return result;
    }
    this.logMissingExternalPathEntry(path);
    result = this.bundled.getString(path);
    if (result != null) {
      return result;
    }
    return null;
  }

  public void setString(String path, String value) {
    this.external.setString(path, value);
  }

  public List<Integer> getIntegerList(String path) {
    List<Integer> result = this.external.getIntegerList(path);
    if (result != null) {
      return result;
    }
    this.logMissingExternalPathEntry(path);
    result = this.bundled.getIntegerList(path);
    if (result != null) {
      return result;
    }
    return null;
  }

  public void setIntegerList(String path, List<Integer> value) {
    this.external.setIntegerList(path, value);
  }

  public List<Double> getDoubleList(String path) {
    List<Double> result = this.external.getDoubleList(path);
    if (result != null) {
      return result;
    }
    this.logMissingExternalPathEntry(path);
    result = this.bundled.getDoubleList(path);
    if (result != null) {
      return result;
    }
    return null;
  }

  public void setDoubleList(String path, List<Double> value) {
    this.external.setDoubleList(path, value);
  }

  public List<Float> getFloatList(String path) {
    List<Float> result = this.external.getFloatList(path);
    if (result != null) {
      return result;
    }
    this.logMissingExternalPathEntry(path);
    result = this.bundled.getFloatList(path);
    if (result != null) {
      return result;
    }
    return null;
  }

  public void setFloatList(String path, List<Float> value) {
    this.external.setFloatList(path, value);
  }

  public List<String> getStringList(String path) {
    List<String> result = this.external.getStringList(path);
    if (result != null) {
      return result;
    }
    this.logMissingExternalPathEntry(path);
    result = this.bundled.getStringList(path);
    if (result != null) {
      return result;
    }
    return null;
  }

  public void setStringList(String path, List<String> value) {
    this.external.setStringList(path, value);
  }

  public Map<Object, Integer> getIntegerMap(String path) {
    Map<Object, Integer> result = this.external.getIntegerMap(path);
    if (result != null) {
      return result;
    }
    this.logMissingExternalPathEntry(path);
    result = this.bundled.getIntegerMap(path);
    if (result != null) {
      return result;
    }
    return null;
  }

  public void setIntegerMap(String path, Map<Object, Integer> value) {
    this.external.setIntegerMap(path, value);
  }

  public Map<Object, Double> getDoubleMap(String path) {
    Map<Object, Double> result = this.external.getDoubleMap(path);
    if (result != null) {
      return result;
    }
    this.logMissingExternalPathEntry(path);
    result = this.bundled.getDoubleMap(path);
    if (result != null) {
      return result;
    }
    return null;
  }

  public void setDoubleMap(String path, Map<Object, Double> value) {
    this.external.setDoubleMap(path, value);
  }

  public Map<Object, Float> getFloatMap(String path) {
    Map<Object, Float> result = this.external.getFloatMap(path);
    if (result != null) {
      return result;
    }
    this.logMissingExternalPathEntry(path);
    result = this.bundled.getFloatMap(path);
    if (result != null) {
      return result;
    }
    return null;
  }

  public void setFloatMap(String path, Map<Object, Float> value) {
    this.external.setFloatMap(path, value);
  }

  public Map<Object, String> getStringMap(String path) {
    Map<Object, String> result = this.external.getStringMap(path);
    if (result != null) {
      return result;
    }
    this.logMissingExternalPathEntry(path);
    result = this.bundled.getStringMap(path);
    if (result != null) {
      return result;
    }
    return null;
  }

  public void setStringMap(String path, Map<Object, String> value) {
    this.external.setStringMap(path, value);
  }

  public <T> T getCustomObject(String path, Class<T> claszz) {
    T result = this.external.getCustomObject(path, claszz);
    if (result != null) {
      return result;
    }
    this.logMissingExternalPathEntry(path);
    result = this.bundled.getCustomObject(path, claszz);
    if (result != null) {
      return result;
    }
    return null;
  }

  public <T> void setCustomObject(String path, Class<T> value) {
    this.external.setCustomObject(path, value);
  }

  public void loadConfigFile() throws FileNotFoundException {
    this.external.loadConfigFile();
  }

  public void saveConfigFile() throws FileNotFoundException, IOException {
    this.external.saveConfigFile();
  }
}
