package me.FurH.Core.object;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import me.FurH.Core.exceptions.CoreException;
import me.FurH.Core.file.FileUtils;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

/**
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class ObjectUtils {

    /**
     * Get the byte value of this object, only if the object is an instance of Number
     *
     * @param obj the object to be converted
     * @return the byte value of the object, or 0 if it is not a number.
     */
    public static byte toByte(Object obj) {
        return (obj instanceof Number) ? ((Number)obj).byteValue() : 0;
    }

    /**
     * Get the short value of this object, only if the object is an instance of Number
     *
     * @param obj the object to be converted
     * @return the short value of the object, or 0 if it is not a number.
     */
    public static short toShort(Object obj) {
        return (obj instanceof Number) ? ((Number)obj).shortValue(): 0;
    }
    
    /**
     * Get the integer value of this object, only if the object is an instance of Number
     *
     * @param obj the object to be converted
     * @return the integer value of the object, or 0 if it is not a number.
     */
    public static int toInteger(Object obj) {
        return (obj instanceof Number) ? ((Number)obj).intValue() : 0;
    }

    /**
     * Get the long value of this object, only if the object is an instance of Number
     *
     * @param obj the object to be converted
     * @return the long value of the object, or 0L if it is not a number.
     */
    public static long toLong(Object obj) {
        return (obj instanceof Number) ? ((Number)obj).longValue() : 0L;
    }

    /**
     * Get the float value of this object, only if the object is an instance of Number
     *
     * @param obj the object to be converted
     * @return the float value of the object, or 0.0f if it is not a number.
     */
    public static float toFloat(Object obj) {
        return (obj instanceof Number) ? ((Number)obj).floatValue() : 0.0f;
    }

    /**
     * Get the double value of this object, only if the object is an instance of Number
     *
     * @param obj the object to be converted
     * @return the double value of the object, or 0.0d if it is not a number.
     */
    public static double toDouble(Object obj) {
        return (obj instanceof Number) ? ((Number)obj).doubleValue() : 0.0d;
    }

    /**
     * Get the boolean value of this object, only if the object is an instance of Boolean
     *
     * @param obj the object to be converted
     * @return the boolean value of the object, or false if it is not a boolean.
     */
    public static boolean toBoolean(Object obj) {
        return (obj instanceof Boolean) ? ((Boolean)obj).booleanValue() : false;
    }

    /**
     * Get the char value of this object, only if the object is an instance of Character
     *
     * @param obj the object to be converted
     * @return the char value of the object, or '\u0000' if it is not a character.
     */
    public static char toChar(Object obj) {
        return (obj instanceof Character) ? ((Character)obj).charValue() : '\u0000';
    }

    /**
     * Get the String value of this object, only if the object is an instance of String
     *
     * @param obj the object to be converted
     * @return the string value of the object, or null if it is not a string.
     */
    public static String toString(Object obj) {
        return (obj instanceof String) ? ((String)obj) : null;
    }

    /**
     * Serialize an object (must be serializable)
     *
     * @param obj the object to be serialized
     * @return a writable byte array representation of the object
     * @throws Exception
     */
    public static byte[] serialize(Object obj) throws Exception {

        ByteArrayOutputStream baos = null;
        ObjectOutputStream oos = null;
        GZIPOutputStream gos = null;
        
        try {

            baos = new ByteArrayOutputStream();
            gos = new GZIPOutputStream(baos);
            oos = new ObjectOutputStream(gos);

            oos.writeObject(obj);

        } catch (IOException ex) {
            throw new CoreException(ex, "Failed to serialize '"+obj.getClass().getSimpleName() + "'");
        } finally {
            FileUtils.closeQuietly(baos);
            FileUtils.closeQuietly(oos);
            FileUtils.closeQuietly(gos);
        }

        return baos.toByteArray();
    }

    /**
     * Get the object represented by this byte array
     *
     * @param data the serialized object
     * @return the deserialized object
     * @throws Exception
     */
    public static Object deserialize(byte[] data) throws Exception {

        ByteArrayInputStream bais = null;
        ObjectInputStream ois = null;
        GZIPInputStream gis = null;
        Object object = null;

        try {

            bais = new ByteArrayInputStream(data);
            gis = new GZIPInputStream(bais);
            ois = new ObjectInputStream(gis);

            object = ois.readObject();

        } catch (IOException | ClassNotFoundException ex) {
            throw new CoreException(ex, "Failed to deserialize data array!");
        } finally {
            FileUtils.closeQuietly(bais);
            FileUtils.closeQuietly(ois);
            FileUtils.closeQuietly(gis);
        }

        return object;
    }
    
    /**
     * Get the object from the given file
     *
     * @param file the file to read
     * @return the object
     * @throws CoreException if the file is empty or has more than one object
     */
    public static Object getObjectFromFile(File file) throws CoreException {
        
        List<String> line = FileUtils.getLinesFromFile(file);
        if (line.isEmpty()) {
            throw new CoreException("There is nothing written in this file: " + file.getAbsolutePath());
        }
        
        if (line.size() > 1) {
            throw new CoreException("This file has more than one line written: " + file.getAbsolutePath());
        }
        
        return getObjectFromString(line.get(0));
    }

    /**
     * Save the object to a file
     *
     * @param file the file to write
     * @param object the object to write in the file
     * @throws CoreException
     */
    public static void saveObjectToFile(File file, Object object) throws CoreException {
        FileUtils.setLinesOfFile(file, new ArrayList<>(Arrays.asList(new String[] { getStringFromObject(object) })));
    }
    
    /**
     * Get all objects from the file
     *
     * @param file the file to read the objects
     * @return an object array containing all the objects, it will always be on the sequence they was written.
     * @throws CoreException if the file was empty
     */
    public static Object[] getObjectsFromFile(File file) throws CoreException {
        
        List<String> lines = FileUtils.getLinesFromFile(file);
        if (lines.isEmpty()) {
            throw new CoreException("There is nothing written in this file: " + file.getAbsolutePath());
        }

        List<Object> objects = new ArrayList<>();
        for (String line : lines) {
            objects.add(getObjectFromString(line));
        }
        
        return objects.toArray();
    }
    
    /**
     * Save all objects to a file
     *
     * @param file the file to be written
     * @param objects the objects to write in the file, the sequence will be preserved
     * @throws CoreException
     */
    public static void saveObjectsToFile(File file, Object[] objects) throws CoreException {
        List<String> lines = new ArrayList<>();

        for (Object o : objects) {
            lines.add(getStringFromObject(o));
        }

        FileUtils.setLinesOfFile(file, lines);
    }

    /**
     * Get the string representation of an object
     * 
     * @param object the object
     * @return the string representation
     * @throws CoreException  
     */
    public static String getStringFromObject(Object object) throws CoreException {
        String ret = null;
        
        ByteArrayOutputStream baos = null;
        GZIPOutputStream gos = null;
        ObjectOutputStream oos = null;
        
        try {

            baos = new ByteArrayOutputStream();
            gos = new GZIPOutputStream(baos);
            oos = new ObjectOutputStream(gos);

            oos.writeObject(object);

            oos.flush();
            gos.flush();
            baos.flush();

            ret = encode(baos.toByteArray());
        } catch (IOException ex) {
            throw new CoreException(ex, "Failed to parse object '" + object.getClass().getSimpleName() + "' to a string");
        } finally {
            FileUtils.closeQuietly(baos);
            FileUtils.closeQuietly(gos);
            FileUtils.closeQuietly(oos);
        }
        
        return ret;
    }

    /**
     * Get the object from the string
     * 
     * @param string the string
     * @return the object
     * @throws CoreException  
     */
    public static Object getObjectFromString(String string) throws CoreException {
        Object ret = null;

        ByteArrayInputStream bais = null;
        GZIPInputStream gis = null;
        ObjectInputStream ois = null;

        try {

            bais = new ByteArrayInputStream(decode(string));
            gis = new GZIPInputStream(bais);
            ois = new ObjectInputStream(gis);

            ret = ois.readObject();

        } catch (IOException | ClassNotFoundException ex) {
            throw new CoreException(ex, "Failed to parse string '" + string + "' into an object");
        } finally {
            FileUtils.closeQuietly(bais);
            FileUtils.closeQuietly(gis);
            FileUtils.closeQuietly(ois);
        }

        return ret;
    }

    /**
     * Encode a Byte Array into Base64
     * 
     * @param data the byte array to encode
     * @return the encoded data as a string
     */
    public static String encode(byte[] data) {
        return new String(Base64Coder.encode(data));
    }

    /**
     * Decode a Base64 String
     * 
     * @param string the String to decode
     * @return the decoded Byte Array
     */
    public static byte[] decode(String string) {
        return Base64Coder.decode(string);
    }
}
