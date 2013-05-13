package me.FurH.Core.object;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import me.FurH.Core.exceptions.CoreException;
import me.FurH.Core.file.FileUtils;

/**
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class ObjectUtils {

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

        } catch (Exception ex) {
            throw new CoreException(ex, "Failed to parse string '" + string + "' into an object");
        } finally {
            FileUtils.closeQuietly(bais);
            FileUtils.closeQuietly(gis);
            FileUtils.closeQuietly(ois);
        }

        return ret;
    }

    public static String encode(byte[] data) {
        return Base64.encode(data);
    }

    public static byte[] decode(String string) {
        return Base64.decode(string);
    }
}
