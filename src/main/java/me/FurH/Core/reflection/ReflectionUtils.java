package me.FurH.Core.reflection;

import me.FurH.Core.exceptions.CoreException;

/**
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class ReflectionUtils {
    
    private static final IReflectionUtils reflection;
    
    static {
        reflection = new DefaultReflectionUtils();
    }

    /**
     * Set a final field data
     * 
     * by polygenelubricants
     * @param obj the object to set the field data
     * @param field the field name
     * @param value the field value to set
     * @throws CoreException
     */
    public static void setFinalField(Object obj, String field, Object value) throws CoreException {
        reflection.setFinalField(obj, field, value);
    }

    /**
     * Get a private field data from an object
     * 
     * @param obj the object to get the field
     * @param field the name of the field
     * @return the field data as an Object
     * @throws CoreException
     */
    public static Object getPrivateField(Object obj, String field) throws CoreException {
        return reflection.getPrivateField(obj, field);
    }
    
    /**
     * Get a private field data from an object
     * 
     * @param obj the object to get the field
     * @param field the name of the field
     * @return the field data as an Object
     * @throws CoreException
     */
    public static int getPrivateIntField(Object obj, String field) throws CoreException {
        return reflection.getPrivateIntField(obj, field);
    }
    
    /**
     * Get a private field data from an object
     * 
     * @param obj the object to get the field
     * @param field the name of the field
     * @return the field data as an Object
     * @throws CoreException
     */
    public static boolean getPrivateBooleanField(Object obj, String field) throws CoreException {
        return reflection.getPrivateBooleanField(obj, field);
    }
    
    /**
     * Get a private field from an class object
     * 
     * @param obj the class object
     * @param instance the instance of the class object
     * @param field the name of the field
     * @return the field data as an Object
     * @throws CoreException
     */
    public static Object getPrivateField(Class<?> obj, Object instance, String field) throws CoreException {
        return reflection.getPrivateField(obj, instance, field);
    }
    
    /**
     * Get a private field from an class object
     * 
     * @param obj the class object
     * @param instance the instance of the class object
     * @param field the name of the field
     * @return the field data as an Object
     * @throws CoreException
     */
    public static int getPrivateIntField(Class<?> obj, Object instance, String field) throws CoreException {
        return reflection.getPrivateIntField(obj, instance, field);
    }

    /**
     * Set a private field data
     * 
     * @param obj the object to set the field data
     * @param field the field name
     * @param value the field value to set
     * @throws CoreException
     */
    public static void setPrivateField(Object obj, String field, Object value) throws CoreException {
        reflection.setPrivateField(obj, field, value);
    }
}
