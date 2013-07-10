package me.FurH.Core.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import me.FurH.Core.exceptions.CoreException;

/**
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class DefaultReflectionUtils implements IReflectionUtils {    

    @Override
    public void setFinalField(Object obj, String field, Object value) throws CoreException {
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

    @Override
    public Object getPrivateField(Object obj, String field) throws CoreException {
        try {
            Field f = obj.getClass().getDeclaredField(field);
            f.setAccessible(true);
            return f.get(obj);
        } catch (Exception ex) {
            throw new CoreException(ex, "Failed to get private field data, field: " + field + ", of the class: " + obj.getClass().getSimpleName());
        }
    }

    @Override
    public int getPrivateIntField(Object obj, String field) throws CoreException {
        try {
            Field f = obj.getClass().getDeclaredField(field);
            f.setAccessible(true);
            return f.getInt(obj);
        } catch (Exception ex) {
            throw new CoreException(ex, "Failed to get private field data, field: " + field + ", of the class: " + obj.getClass().getSimpleName());
        }
    }

    @Override
    public boolean getPrivateBooleanField(Object obj, String field) throws CoreException {
        try {
            Field f = obj.getClass().getDeclaredField(field);
            f.setAccessible(true);
            return f.getBoolean(obj);
        } catch (Exception ex) {
            throw new CoreException(ex, "Failed to get private field data, field: " + field + ", of the class: " + obj.getClass().getSimpleName());
        }
    }

    @Override
    public Object getPrivateField(Class<?> obj, Object instance, String field) throws CoreException {
        try {
            Field f = obj.getDeclaredField(field);
            f.setAccessible(true);
            return f.get(instance);
        } catch (Exception ex) {
            throw new CoreException(ex, "Failed to get private field data, field: " + field + ", of the class: " + obj.getClass().getSimpleName());
        }
    }

    @Override
    public int getPrivateIntField(Class<?> obj, Object instance, String field) throws CoreException {
        try {
            Field f = obj.getDeclaredField(field);
            f.setAccessible(true);
            return f.getInt(instance);
        } catch (Exception ex) {
            throw new CoreException(ex, "Failed to get private field data, field: " + field + ", of the class: " + obj.getClass().getSimpleName());
        }
    }

    @Override
    public void setPrivateField(Object obj, String field, Object value) throws CoreException {    
        try {
            Field f = obj.getClass().getDeclaredField(field);
            f.setAccessible(true);
            f.set(obj, value);
        } catch (Exception ex) {
            throw new CoreException(ex, "Failed to set private field data, field: " + field + ", of the class: " + obj.getClass().getSimpleName());
        }
    }
}
