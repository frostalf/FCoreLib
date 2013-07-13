package me.FurH.Core.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import javax.tools.ToolProvider;
import me.FurH.Core.exceptions.CoreException;
import me.FurH.Core.reflection.field.DefaultField;
import me.FurH.Core.reflection.field.IReflectField;
import me.FurH.Core.reflection.field.ReflectifyField;

/**
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class ReflectionUtils {

    /**
     * Create a new IReflectField using the available reflector
     *
     * @param field the field name
     * @param cls the class
     * @param set in way to enable set values on reflectify, this must be true
     * @return the new IReflectField object
     */
    public static IReflectField getNewReflectField(String field, Class<?> cls, boolean set) {
        if (isReflectifyAvailable()) {
            return new ReflectifyField(field, cls, set);
        } else {
            return new DefaultField(field, cls, set);
        }
    }

    /**
     * Check if the reflectify api is installed and if there is any java compiler available which is used by reflectify.
     *
     * @return true if reflectify is available, false otherwise
     */
    public static boolean isReflectifyAvailable() {
        try {
            return Class.forName("org.abstractmeta.reflectify.ReflectifyRegistry") != null && ToolProvider.getSystemJavaCompiler() != null;
        } catch (ClassNotFoundException ex) {
            return false;
        }
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
        try {
            Field f = obj.getClass().getDeclaredField(field);
            f.setAccessible(true);

            Field mF = Field.class.getDeclaredField("modifiers");
            mF.setAccessible(true);
            mF.setInt(f, f.getModifiers() & ~Modifier.FINAL);

            f.set(obj, value);
        } catch (Exception ex) {
            throw new CoreException(ex, "Failed to set the final field: " + field + ", of the class: " + obj.getClass().getSimpleName());
        }
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
        try {
            Field f = obj.getClass().getDeclaredField(field);
            f.setAccessible(true);
            return f.get(obj);
        } catch (Exception ex) {
            throw new CoreException(ex, "Failed to get private field data, field: " + field + ", of the class: " + obj.getClass().getSimpleName());
        }
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
        try {
            Field f = obj.getClass().getDeclaredField(field);
            f.setAccessible(true);
            return f.getInt(obj);
        } catch (Exception ex) {
            throw new CoreException(ex, "Failed to get private field data, field: " + field + ", of the class: " + obj.getClass().getSimpleName());
        }
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
        try {
            Field f = obj.getClass().getDeclaredField(field);
            f.setAccessible(true);
            return f.getBoolean(obj);
        } catch (Exception ex) {
            throw new CoreException(ex, "Failed to get private field data, field: " + field + ", of the class: " + obj.getClass().getSimpleName());
        }
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
        try {
            Field f = obj.getDeclaredField(field);
            f.setAccessible(true);
            return f.get(instance);
        } catch (Exception ex) {
            throw new CoreException(ex, "Failed to get private field data, field: " + field + ", of the class: " + obj.getClass().getSimpleName());
        }
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
        try {
            Field f = obj.getDeclaredField(field);
            f.setAccessible(true);
            return f.getInt(instance);
        } catch (Exception ex) {
            throw new CoreException(ex, "Failed to get private field data, field: " + field + ", of the class: " + obj.getClass().getSimpleName());
        }
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
        try {
            Field f = obj.getClass().getDeclaredField(field);
            f.setAccessible(true);
            f.set(obj, value);
        } catch (Exception ex) {
            throw new CoreException(ex, "Failed to set private field data, field: " + field + ", of the class: " + obj.getClass().getSimpleName());
        }
    }
}