/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.joestr.postbox.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.InputStream;
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
    public Base64Objectifier(Class<? extends ObjectOutputStream> objectOutputStream, Class<? extends ObjectInputStream> objectInputStream) {

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
            ByteArrayInputStream inputStream
                = new ByteArrayInputStream(Base64.getDecoder().decode(data));

            ObjectInputStream dataInput = this.objectInputStream.getConstructor(InputStream.class).newInstance(inputStream);

            T object = (T) dataInput.readObject();

            dataInput.close();
            return object;
        } catch (Exception ex) {
            return null;
        }
    }
}
