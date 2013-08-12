package me.FurH.Core.arrays;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
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
     * Search for the given pattern inside the byte array
     *
     * @param data the data to search on
     * @param pattern the pattern to match
     * @return the start point of the matched pattern, or -1 if not found.
     */
    public static int indexOf(byte[] data, byte[] pattern) {
        
        int[] failure = computeFailure(pattern);
        int j = 0;

        for (int i = 0; i < data.length; i++) {

            while (j > 0 && pattern[j] != data[i]) {
                j = failure[j - 1];
            }

            if (pattern[j] == data[i]) {
                j++;
            }

            if (j == pattern.length) {
                return i - pattern.length + 1;
            }
        }

        return -1;
    }

    private static int[] computeFailure(byte[] pattern) {
        int[] failure = new int[pattern.length];

        int j = 0;
        for (int i = 1; i < pattern.length; i++) {

            while (j > 0 && pattern[j] != pattern[i]) {
                j = failure[j - 1];
            }

            if (pattern[j] == pattern[i]) {
                j++;
            }

            failure[i] = j;
        }

        return failure;
    }
    
    /**
     * Convert the string array to a single string starting from the given point
     *
     * @param start the start point
     * @param args the string array
     * @return a single string with all contents of the array separated with spaces
     */
    public static String toString(int start, String args[]) {
        String result = "";

        for (int i = start; i < args.length; i++) {
            result += args[i]+ " ";
        }

        return result;
    }

    /**
     * Join the 2D array into a single array
     *
     * @param buffer the 2D array to join
     * @return a single array with all the 2D array data
     */
    public static byte[] joinByteArray(byte[][] buffer) {

        int lenght = 0;
        for (byte[] slice : buffer) {
            lenght += slice.length;
        }

        byte[] ret = new byte [ lenght ];
        int read = 0;

        for (int j1 = 0; j1 < buffer.length; j1++) {
            byte[] copy = buffer[j1];

            System.arraycopy(copy, 0, ret, read, copy.length);

            read += copy.length;
        }

        return ret;
    }

    /**
     * Split the byte array into pieces of the given size, the last piece may not have the same size of the others.
     *
     * @param array the array to split
     * @param each the size of each piece
     * @return the 2D array with the pieces
     */
    public static byte[][] splitByteArray(byte[] array, int each) {
        
        int slice = (int) Math.floor(array.length / each);
        byte[][] slices = new byte[ slice + 1 ][];

        int read = 0;

        for (int j1 = 0; j1 < slices.length; j1++) {

            int to = read + each;

            if (to > array.length) {
                to = array.length;
            }

            slices[j1] = Arrays.copyOfRange(array, read, to);
            read += each;
        }

        return slices;
    }
    
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
            } catch (Throwable ex) { }
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
            } catch (Throwable ex) { }
        }
    }
}