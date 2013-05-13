package me.FurH.Core.object;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import me.FurH.Core.exceptions.CoreException;
import me.FurH.Core.file.FileUtils;
import me.FurH.Core.inventory.InventoryStack;

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
        ObjectOutputStream oos = null;

        try {

            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            
            oos.writeObject(object);
            oos.flush(); baos.flush();

            ret = new BigInteger(1, baos.toByteArray()).toString(32);
        } catch (IOException ex) {
            throw new CoreException(ex, "Failed to parse object '" + object.getClass().getSimpleName() + "' to a string");
        } finally {
            FileUtils.closeQuietly(baos);
            FileUtils.closeQuietly(oos);
        }

        return encode(ret);
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
        ObjectInputStream dis = null;

        try {

            bais = new ByteArrayInputStream(new BigInteger(decode(string), 32).toByteArray());
            dis = new ObjectInputStream(bais);

            ret = dis.readObject();

        } catch (Exception ex) {
            throw new CoreException(ex, "Failed to parse string '" + string + "' into an object");
        } finally {
            FileUtils.closeQuietly(bais);
            FileUtils.closeQuietly(dis);
        }

        return ret;
    }
    
    private static String encode(String string) {
        return InventoryStack.encode(string);
    }

    private static String decode(String string) {
        return InventoryStack.decode(string);
    }
}
