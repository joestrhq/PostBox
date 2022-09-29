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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Base64;

/**
 * This class heavily abstracts the interaction between Base64 strings and
 * object.
 *
 * @param <T> Represents the type of the object
 * @author Joel
 */
public class Base64Objectifier<T> {

  // The object output stram
  Class<? extends ObjectOutputStream> objectOutputStream = null;

  // The object input stream
  Class<? extends ObjectInputStream> objectInputStream = null;

  /**
   * Create an instance.
   *
   * @param objectOutputStream The object output stream to use.
   * @param objectInputStream The object input stream to use.
   */
  public Base64Objectifier(
    Class<? extends ObjectOutputStream> objectOutputStream,
    Class<? extends ObjectInputStream> objectInputStream) {

    // The object output stram
    this.objectOutputStream = objectOutputStream;

    // The object input stram
    this.objectInputStream = objectInputStream;
  }

  /**
   * Turns an object into a Base64 representation of it. (This method silences
   * exceptions and return null in case of an error.)
   *
   * @param object The object to turn into a Base64 string
   * @return The Base64 string. (In case of an error {@code null})
   */
  public String toBase64(T object) {
    try {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

      ObjectOutputStream dataOutput
        = this.objectOutputStream.getConstructor(OutputStream.class).newInstance(outputStream);

      dataOutput.writeObject(object);
      dataOutput.close();

      return Base64.getEncoder().encodeToString(outputStream.toByteArray());
    } catch (Exception ex) {
      return null;
    }
  }

  /**
   * Turns a Base64 string into an object. (This method silences Exeptions and
   * returns null in case of an error.)
   *
   * @param data The Base64 string
   * @return The rebuilt object. (In case of an error {@code null})
   */
  public T fromBase64(String data) {
    try {
      ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(data));

      ObjectInputStream dataInput
        = this.objectInputStream.getConstructor(InputStream.class).newInstance(inputStream);

      T object = (T) dataInput.readObject();

      dataInput.close();
      return object;
    } catch (Exception ex) {
      return null;
    }
  }
}
