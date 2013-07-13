package me.FurH.Core.reflection.field;

import me.FurH.Core.exceptions.CoreException;

/**
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public abstract class IReflectField {

    protected final String field;
    
    /**
     * The default IReflectField constructor
     *
     * @param field the field name
     * @param cls the class that contains the field
     * @param set the set flag 
     */
    public IReflectField(String field, Class<?> cls, boolean set) {
        this.field = field;
    }
    
    /**
     * Return an int value from the given object, note that this object must be of the same class that was used on the constructor
     *
     * @param handle the object to return the value
     * @return the integer value
     * @throws CoreException
     */
    public abstract int getInt(Object handle) throws CoreException;
    
    /**
     * Return an boolean value from the given object, note that this object must be of the same class that was used on the constructor
     *
     * @param handle the object to return the value
     * @return the boolean value
     * @throws CoreException
     */
    public abstract boolean getBoolean(Object handle) throws CoreException;
    
    /**
     * Return an byte array value from the given object, note that this object must be of the same class that was used on the constructor
     *
     * @param handle the object to return the value
     * @return the byte array value
     * @throws CoreException
     */
    public abstract byte[] getByteArray(Object handle) throws CoreException;
    
    /**
     * Return an int array value from the given object, note that this object must be of the same class that was used on the constructor
     *
     * @param handle the object to return the value
     * @return the integer array value
     * @throws CoreException
     */
    public abstract int[] getIntArray(Object handle) throws CoreException;
    
    /**
     * Return an double byte array value from the given object, note that this object must be of the same class that was used on the constructor
     *
     * @param handle the object to return the value
     * @return the double byte array value
     * @throws CoreException
     */
    public abstract byte[][] getDoubleByteArray(Object handle) throws CoreException;

    /**
     * Set the field value
     *
     * @param value the value to set the field
     * @param handle the object containing the field
     * @throws CoreException
     */
    public abstract void set(Object value, Object handle) throws CoreException;
}
