package me.FurH.Core.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.channels.Channel;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;
import me.FurH.Core.exceptions.CoreException;

/**
 *
 * @author FurmigaHumana
 */
public class FileUtils {
    
    /**
     * Closes the stream quietly
     * 
     * @param stream the stream to close
     */
    public static void closeQuietly(Reader stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (Exception ex) { }
        }
    }
    
    /**
     * Closes the stream quietly
     * 
     * @param stream the stream to close
     */
    public static void closeQuietly(ResultSet stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (Exception ex) { }
        }
    }
    
    /**
     * Closes the stream quietly
     * 
     * @param stream the stream to close
     */
    public static void closeQuietly(Writer stream) {
        if (stream != null) {
            try {
                stream.flush();
                stream.close();
            } catch (Exception ex) { }
        }
    }
    
    /**
     * Closes the stream quietly
     * 
     * @param stream the stream to close
     */
    public static void closeQuietly(Channel stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (Exception ex) { }
        }
    }

    /**
     * Closes the stream quietly
     * 
     * @param stream the stream to close
     */
    public static void closeQuietly(OutputStream stream) {
        if (stream != null) {
            try {
                stream.flush();
                stream.close();
            } catch (Exception ex) { }
        }
    }

    /**
     * Closes the stream quietly
     * 
     * @param stream the stream to close
     */
    public static void closeQuietly(InputStream stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (Exception ex) { }
        }
    }

    /**
     * Closes the stream quietly
     * 
     * @param stream the stream to close
     */
    public static void closeQuietly(Statement stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (Exception ex) { }
        }
    }
    
    /**
     * Closes the stream quietly
     * 
     * @param stream the stream to close
     */
    public static void closeQuietly(Scanner stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (Exception ex) { }
        }
    }
    
    /**
     * Get all bytes from the file
     *
     * @param file the file to read the bytes
     * @return an byte array with the file bytes
     * @throws CoreException
     */
    public static byte[] getBytesFromFile(File file) throws CoreException {

        InputStream is = null;

        try {

            int offset = 0;
            int read = 0;

            is = new FileInputStream(file);

            byte[] data = new byte[(int) file.length()];

            while (offset < data.length && (read = is.read(data, offset, data.length - offset)) >= 0) {
                offset += read;
                is.close();
            }

            return data;
        } catch (Exception ex) {
            throw new CoreException(ex, "Failed to read '" + file.getName() + "' bytes!");
        }
        
    }
    
    /**
     * Copy the stream (usualy a resource inside the jar) to a file
     * 
     * @param in the resource
     * @param file the destination file
     * @throws CoreException
     */
    public static void copyFile(InputStream in, File file) throws CoreException {
        
        OutputStream out = null;
        
        try {

            if (file.exists()) {  file.delete(); }
            if ((file.getParentFile() != null) && (!file.getParentFile().exists())) {
                file.getParentFile().mkdirs();
            }
            
            out = new FileOutputStream(file);
            byte[] buffer = new byte[512];
            
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            
        } catch (IOException ex) {
            throw new CoreException(ex, "Failed to copy the file '"+file.getName()+"' to '" + file.getAbsolutePath() + "'");
        } finally {
            closeQuietly(out);
        }
    }
}
