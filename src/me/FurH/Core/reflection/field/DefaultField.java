package me.FurH.Core.reflection.field;

import java.lang.reflect.Field;
import me.FurH.Core.exceptions.CoreException;

/**
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public class DefaultField extends IReflectField {
    
    private Field javaField;

    /**
     * Creates a new IReflectField using the default java.lang.reflect, this methods ain't fast and this classes does not changes it.
     * It will only be used when Reflectify is not available, this implementation works as a hotswap between reflectify and java reflect.
     *
     * @param field the field to be reflected
     * @param cls the class containing the field
     * @param set ignored
     */
    public DefaultField(String field, Class<?> cls, boolean set) {
        super(field, cls, set);

        try {
            javaField = cls.getDeclaredField(field);
            javaField.setAccessible(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public int getInt(Object handle) throws CoreException {
        try {
            return javaField.getInt(handle);
        } catch (Exception ex) {
            throw new CoreException(ex, "Failed to get field '" + this.field + "' from class '" + handle.getClass().getSimpleName() + "'");
        }
    }

    @Override
    public boolean getBoolean(Object handle) throws CoreException {
        try {
            return javaField.getBoolean(handle);
        } catch (Exception ex) {
            throw new CoreException(ex, "Failed to get field '" + this.field + "' from class '" + handle.getClass().getSimpleName() + "'");
        }
    }

    @Override
    public byte[] getByteArray(Object handle) throws CoreException {
        try {
            return (byte[]) javaField.get(handle);
        } catch (Exception ex) {
            throw new CoreException(ex, "Failed to get field '" + this.field + "' from class '" + handle.getClass().getSimpleName() + "'");
        }
    }

    @Override
    public int[] getIntArray(Object handle) throws CoreException {
        try {
            return (int[]) javaField.get(handle);
        } catch (Exception ex) {
            throw new CoreException(ex, "Failed to get field '" + this.field + "' from class '" + handle.getClass().getSimpleName() + "'");
        }
    }

    @Override
    public byte[][] getDoubleByteArray(Object handle) throws CoreException {
        try {
            return (byte[][]) javaField.get(handle);
        } catch (Exception ex) {
            throw new CoreException(ex, "Failed to get field '" + this.field + "' from class '" + handle.getClass().getSimpleName() + "'");
        }
    }

    @Override
    public void set(Object value, Object handle) throws CoreException {
        try {
            this.javaField.set(value, handle);
        } catch (Exception ex) {
            throw new CoreException(ex, "Failed to set field '" + this.field + "' from class '" + handle.getClass().getSimpleName() + "'");
        }
    }
}