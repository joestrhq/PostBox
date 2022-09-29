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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Joel
 */
public abstract class YamlConfiguration {

  Map<Object, Object> config;

  public YamlConfiguration(Map<Object, Object> config) {
    this.config = config;
  }

  private Object lookupObject(String path, Map<Object, Object> configMap) {
    Map<Object, Object> subMap = new HashMap<>(configMap);

    String[] splitted = path.contains(".") ? path.split("\\.") : new String[]{path};

    for (int i = 0; i < splitted.length - 1; i++) {
      if (!subMap.containsKey(splitted[i])) {
        return null;
      } else {
        subMap = (Map<Object, Object>) subMap.get(splitted[i]);
      }
    }

    return subMap.get(splitted[splitted.length - 1]);
  }

  private <T> T getObject(String path, Class<? extends T> clazz) {
    Object result = this.lookupObject(path, config);

    if (result != null) {
      return clazz.cast(result);
    }

    return null;
  }

  private <T> void setObject(String path, Class<? extends T> clazz, T value) {
    String last = path.split(".")[path.split(".").length - 1];
    Map<Object, Object> result = (Map<Object, Object>) lookupObject(path, this.config);
    result.put(last, clazz.cast(value));
  }

  public boolean contains(String path) {
    return this.lookupObject(path, config) != null;
  }

  public Boolean getBoolean(String path) {
    return getObject(path, Boolean.class);
  }

  public void setBoolean(String path, Boolean value) {
    setObject(path, Boolean.class, value);
  }

  public Integer getInteger(String path) {
    return getObject(path, Integer.class);
  }

  public void setInteger(String path, Integer value) {
    setObject(path, Integer.class, value);
  }

  public Double getDouble(String path) {
    return getObject(path, Double.class);
  }

  public void setDouble(String path, Double value) {
    setObject(path, Double.class, value);
  }

  public Float getFloat(String path) {
    return getObject(path, Float.class);
  }

  public void setFloat(String path, Float value) {
    setObject(path, Float.class, value);
  }

  public String getString(String path) {
    return getObject(path, String.class);
  }

  public void setString(String path, String value) {
    setObject(path, String.class, value);
  }

  public List<Integer> getIntegerList(String path) {
    return getObject(path, new ArrayList<Integer>().getClass());
  }

  public void setIntegerList(String path, List<Integer> value) {
    setObject(path, value.getClass(), value);
  }

  public List<Double> getDoubleList(String path) {
    return getObject(path, new ArrayList<Double>().getClass());
  }

  public void setDoubleList(String path, List<Double> value) {
    setObject(path, value.getClass(), value);
  }

  public List<Float> getFloatList(String path) {
    return getObject(path, new ArrayList<Float>().getClass());
  }

  public void setFloatList(String path, List<Float> value) {
    setObject(path, value.getClass(), value);
  }

  public List<String> getStringList(String path) {
    return getObject(path, new ArrayList<String>().getClass());
  }

  public void setStringList(String path, List<String> value) {
    setObject(path, value.getClass(), value);
  }

  public Map<Object, Integer> getIntegerMap(String path) {
    return getObject(path, new HashMap<Object, Integer>().getClass());
  }

  public void setIntegerMap(String path, Map<Object, Integer> value) {
    setObject(path, value.getClass(), value);
  }

  public Map<Object, Double> getDoubleMap(String path) {
    return getObject(path, new HashMap<Object, Double>().getClass());
  }

  public void setDoubleMap(String path, Map<Object, Double> value) {
    setObject(path, value.getClass(), value);
  }

  public Map<Object, Float> getFloatMap(String path) {
    return getObject(path, new HashMap<Object, Float>().getClass());
  }

  public void setFloatMap(String path, Map<Object, Float> value) {
    setObject(path, value.getClass(), value);
  }

  public Map<Object, String> getStringMap(String path) {
    return getObject(path, new HashMap<Object, String>().getClass());
  }

  public void setStringMap(String path, Map<Object, String> value) {
    setObject(path, value.getClass(), value);
  }

  public <T> T getCustomObject(String path, Class<T> claszz) {
    return getObject(path, claszz);
  }

  public <T> void setCustomObject(String path, Class<T> value) {
    setObject(path, value.getClass(), value);
  }
}
