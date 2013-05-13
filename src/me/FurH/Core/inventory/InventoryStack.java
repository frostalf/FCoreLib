package me.FurH.Core.inventory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.math.BigInteger;
import me.FurH.Core.exceptions.CoreException;
import me.FurH.Core.file.FileUtils;
import net.minecraft.server.v1_5_R3.ItemStack;
import net.minecraft.server.v1_5_R3.NBTBase;
import net.minecraft.server.v1_5_R3.NBTTagCompound;
import net.minecraft.server.v1_5_R3.NBTTagList;
import org.bukkit.craftbukkit.v1_5_R3.inventory.CraftItemStack;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

/**
 *
 * @author FurmigaHumana All Rights Reserved unless otherwise explicitly stated.
 */
public class InventoryStack {

    /**
     * Get the String representation of the given ItemStack
     * 
     * @param stack the ItemStack
     * @return the string representation of the @param stack
     * @throws CoreException
     */
    public static String getStringFromItemStack(org.bukkit.inventory.ItemStack stack) throws CoreException {
        String ret = null;

        ByteArrayOutputStream baos = null;
        DataOutputStream dos = null;

        try {

            baos = new ByteArrayOutputStream();
            dos = new DataOutputStream(baos);

            NBTTagCompound base = new NBTTagCompound();
            ItemStack craft = getCraftVersion(stack);

            if (craft != null) {
                craft.save(base);
            }

            NBTBase.a(base, dos);
            
            dos.flush();
            baos.flush();

            ret = new BigInteger(1, baos.toByteArray()).toString(32);
        } catch (Exception ex) {
            throw new CoreException(ex, "Failed to convert the ItemStack '" + stack.toString() + "' into a string.");
        } finally {
            FileUtils.closeQuietly(baos);
            FileUtils.closeQuietly(dos);
        }

        return encode(ret);
    }

    /**
     * Get the String representation of the given ItemStack Array
     * 
     * @param source the ItemStack Array
     * @return the string representation
     * @throws CoreException
     */
    public static String getStringFromArray(org.bukkit.inventory.ItemStack[] source) throws CoreException {
        String ret = null;

        ByteArrayOutputStream baos = null;
        DataOutputStream dos = null;

        try {

            baos = new ByteArrayOutputStream();
            dos = new DataOutputStream(baos);
            NBTTagList list = new NBTTagList();

            for (int j1 = 0; j1 < source.length; j1++) {
                ItemStack craft = getCraftVersion(source[j1]);
                NBTTagCompound base = new NBTTagCompound();

                if (craft != null) {
                    craft.save(base);
                }

                list.add(base);
            }

            NBTBase.a(list, dos);
            
            baos.flush();
            dos.flush();

            ret = new BigInteger(1, baos.toByteArray()).toString(32);
        } catch (Exception ex) {
            throw new CoreException(ex, "Failed to convert the ItemStack Array into a string.");
        } finally {
            FileUtils.closeQuietly(baos);
            FileUtils.closeQuietly(dos);
        }

        return encode(ret);
    }

    /**
     * Get the ItemStack represented by the given String
     * 
     * @param string the ItemStack string
     * @return the ItemStack
     * @throws CoreException
     */
    public static org.bukkit.inventory.ItemStack getItemStackFromString(String string) throws CoreException {
        org.bukkit.inventory.ItemStack ret = null;

        ByteArrayInputStream bais = null;
        DataInputStream dis = null;

        try {

            bais = new ByteArrayInputStream(new BigInteger(decode(string), 32).toByteArray());
            dis = new DataInputStream(bais);

            NBTTagCompound base = (NBTTagCompound) NBTBase.b(dis);

            if (!base.isEmpty()) {
                ret = CraftItemStack.asBukkitCopy(net.minecraft.server.v1_5_R3.ItemStack.createStack(base));
            }

        } catch (Exception ex) {
            throw new CoreException(ex, "Failed to convert the String '" + string + "' into an ItemStack.");
        } finally {
            FileUtils.closeQuietly(bais);
            FileUtils.closeQuietly(dis);
        }

        return ret;
    }

    /**
     * Get the ItemStack Array represented by the given String
     * 
     * @param string the ItemStack Array string
     * @return the ItemStack Array
     * @throws CoreException
     */
    public static org.bukkit.inventory.ItemStack[] getArrayFromString(String string) throws CoreException {
        org.bukkit.inventory.ItemStack[] ret = null;

        ByteArrayInputStream bais = null;
        DataInputStream dis = null;

        try {

            bais = new ByteArrayInputStream(new BigInteger(decode(string), 32).toByteArray());
            dis = new DataInputStream(bais);

            NBTTagList nbtlist = (NBTTagList) NBTBase.b(dis);
            ret = new org.bukkit.inventory.ItemStack[ nbtlist.size() ];

            for (int i = 0; i < nbtlist.size(); i++) {
                NBTTagCompound compound = (NBTTagCompound) nbtlist.get(i);

                if (!compound.isEmpty()) {
                    ret[ i ] = CraftItemStack.asBukkitCopy(net.minecraft.server.v1_5_R3.ItemStack.createStack(compound));
                }
                
            }

        } catch (Exception ex) {
            throw new CoreException(ex, "Failed to convert the String '" + string + "' into an ItemStack Array.");
        } finally {
            FileUtils.closeQuietly(bais);
            FileUtils.closeQuietly(dis);
        }

        return ret;
    }

    private static net.minecraft.server.v1_5_R3.ItemStack getCraftVersion(org.bukkit.inventory.ItemStack stack) {

        if (stack != null) {
            return CraftItemStack.asNMSCopy(stack);
        }

        return null;
    }

    /**
     * Encode a String into Base64
     * 
     * @param string the String to encode
     * @return the encoded String, or "0" if the string is null.
     */
    public static String encode(String string) {
        if (string == null) {
            return "MA==";
        }
        return Base64Coder.encodeString(string);
    }

    /**
     * Decode a Base64 String
     * 
     * @param string the String to decode
     * @return the decoded String, or an empty String if the @param string is null
     */
    public static String decode(String string) {
        if (string == null) {
            return "";
        }
        return Base64Coder.decodeString(string);
    }
}