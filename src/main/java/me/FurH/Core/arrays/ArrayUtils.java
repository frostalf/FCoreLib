package me.FurH.Core.arrays;

import java.io.ByteArrayOutputStream;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import me.FurH.Core.exceptions.CoreException;
import me.FurH.Core.file.FileUtils;

/**
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class ArrayUtils {

    /**
     * Compress the given byte array using the java.util.zip.Deflater
     *
     * @param data the data to be compressed
     * @param level the level of compression (-1 to 9)
     * @return the compressed data
     * @throws CoreException
     */
    public static byte[] compress(byte[] data, int level) throws CoreException {

        if (level < Deflater.DEFAULT_COMPRESSION) {
            throw new CoreException("The compression level can't be lower than " + Deflater.DEFAULT_COMPRESSION);
        }

        if (level > Deflater.BEST_COMPRESSION) {
            throw new CoreException("The compression level can't be higher than " + Deflater.BEST_COMPRESSION);
        }

        ByteArrayOutputStream baos = null;
        Deflater def = null;

        try {

            def = new Deflater(level);
            def.setInput(data);

            baos = new ByteArrayOutputStream(data.length);
            def.finish();

            byte[] buffer = new byte[ 1024 ];
            while (!def.finished()) {
                int read = def.deflate(buffer);
                baos.write(buffer, 0, read);
            }

            return baos.toByteArray();
        } catch (Exception ex) {
            throw new CoreException(ex, "Failed to compress data!");
        } finally {
            FileUtils.closeQuietly(baos);
            closeQuietly(def);
        }
    }
    
    /**
     * Decompress the given compressed data using the java.util.zip.Inflater
     *
     * @param data the data compressed with the @compress method
     * @return the decompressed data
     * @throws CoreException
     */
    public static byte[] decompress(byte[] data) throws CoreException {

        ByteArrayOutputStream baos = null;
        Inflater inf = null;

        try {
            
            inf = new Inflater();
            inf.setInput(data);

            baos = new ByteArrayOutputStream(data.length);
            byte[] buffer = new byte[ 1024 ];

            while (!inf.finished()) {
                int read = inf.inflate(buffer);
                baos.write(buffer, 0, read);
            }

            return baos.toByteArray();
        } catch (Exception ex) {
            throw new CoreException(ex, "Failed to decompress data!");
        } finally {
            FileUtils.closeQuietly(baos);
            closeQuietly(inf);
        }
    }

    /**
     * Closes the stream quietly
     * 
     * @param stream the stream to close
     */
    public static void closeQuietly(Deflater stream) {
        if (stream != null) {
            try {
                stream.end();
            } catch (Exception ex) { }
        }
    }
    
    /**
     * Closes the stream quietly
     * 
     * @param stream the stream to close
     */
    public static void closeQuietly(Inflater stream) {
        if (stream != null) {
            try {
                stream.end();
            } catch (Exception ex) { }
        }
    }
}