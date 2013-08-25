package me.FurH.Core.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.channels.Channel;
import java.nio.charset.Charset;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import me.FurH.Core.arrays.ArrayUtils;
import me.FurH.Core.database.CoreSQLDatabase;
import me.FurH.Core.exceptions.CoreException;
import me.FurH.Core.util.Utils;

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
    public static void closeQuietly(Deflater stream) {
        ArrayUtils.closeQuietly(stream);
    }

    /**
     * Closes the stream quietly
     * 
     * @param stream the stream to close
     */
    public static void closeQuietly(Inflater stream) {
        ArrayUtils.closeQuietly(stream);
    }
    
    /**
     * Closes the stream quietly
     * 
     * @param stream the stream to close
     */
    public static void closeQuietly(Statement stream) {
        CoreSQLDatabase.closeQuietly(stream);
    }
    
    /**
     * Closes the stream quietly
     * 
     * @param stream the stream to close
     */
    public static void closeQuietly(ResultSet stream) {
        CoreSQLDatabase.closeQuietly(stream);
    }
    
    /**
     * Closes the stream quietly
     * 
     * @param stream the stream to close
     */
    public static void closeQuietly(Reader stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (Throwable ex) { }
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
            } catch (Throwable ex) { }
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
            } catch (Throwable ex) { }
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
            } catch (Throwable ex) { }
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
            } catch (Throwable ex) { }
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
            } catch (Throwable ex) { }
        }
    }
    
    /**
     * Get all files inside directory, including files in subfolders.
     * 
     * @see FileUtils#getAllFilesAt(java.util.Collection, java.io.File) 
     *
     * @param directory the directory to get the files
     * @return the list with all files inside the directory
     */
    public static List<File> getAllFilesAt(File directory) {
        List<File> files = new ArrayList<File>();

        getAllFilesAt(files, directory);

        return files;
    }

    /**
     * Populate the given Collection with all the files inside the given directory, including files in subfolders.
     *
     * @param files the Collection to be populated
     * @param directory the directory to get the files
     */
    public static void getAllFilesAt(Collection<File> files, File directory) {

        for (File file : directory.listFiles()) {
            if (file.isFile()) {
                if (!file.isHidden()) {
                    files.add(file);
                }
            } else if (file.isDirectory()) {
                getAllFilesAt(files, file);
            }
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

            if (file.length() > Integer.MAX_VALUE) {
                throw new CoreException("File is too big! Max size is: " + Utils.getFormatedBytes(Integer.MAX_VALUE));
            }

            int offset = 0;
            int read = 0;

            is = new FileInputStream(file);

            byte[] data = new byte[(int) file.length()];

            while (offset < data.length && (read = is.read(data, offset, data.length - offset)) >= 0) {
                offset += read;
            }

            return data;
        } catch (Exception ex) {
            throw new CoreException(ex, "Failed to get the file '" + file.getName() + "' bytes!");
        } finally {
            closeQuietly(is);
        }
    }
    
    /**
     * Return an List<String> with all the lines of a file
     *
     * @param file the file to get the lines
     * @return an array list with the lines, empty in case of an error
     * @throws CoreException
     */
    public static List<String> getLinesFromFile(File file) throws CoreException {

        List<String> ret = new ArrayList<String>();
        
        InputStreamReader reader = null;
        BufferedReader input = null;
        FileInputStream fis = null;

        try {

            fis = new FileInputStream(file);
            reader = new InputStreamReader(fis, Charset.forName("UTF8"));
            input = new BufferedReader(reader);

            String line;
            while ((line = input.readLine()) != null) {
                ret.add(line);
            }

            return ret;
        } catch (Exception ex) {
            throw new CoreException(ex, "Failed to get '" + file.getName() + "' lines!");
        } finally {
            closeQuietly(input);
            closeQuietly(reader);
            closeQuietly(fis);
        }
    }
    
    /**
     * Set the lines of a file
     *
     * @param file the file to set the lines
     * @param lines the array list with the lines to set
     * @throws CoreException
     */
    public static void setLinesOfFile(File file, List<String> lines) throws CoreException {

        FileOutputStream stream = null;
        Writer writer = null;

        try {

            String l = System.getProperty("line.separator");

            stream = new FileOutputStream(file);
            writer = new OutputStreamWriter(stream, Charset.forName("UTF8"));

            for (String line : lines) {
                writer.write(line); writer.write(l);
            }

            writer.flush();
            stream.flush();

        } catch (IOException ex) {
            throw new CoreException(ex, "Failed to set '" + file.getName() + "' lines!");
        } finally {
            closeQuietly(stream);
            closeQuietly(writer);
        }
    }
        
    /**
     * Copy the stream (usually a resource inside the jar) to a file
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
