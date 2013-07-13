package me.FurH.Core.reflection.field;

import me.FurH.Core.exceptions.CoreException;
import me.FurH.Core.reflection.ReflectionUtils;
import org.abstractmeta.reflectify.Accessor;
import org.abstractmeta.reflectify.Reflectify;
import org.abstractmeta.reflectify.ReflectifyRegistry;
import org.abstractmeta.reflectify.runtime.ReflectifyRuntimeRegistry;

/**
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class ReflectifyField extends IReflectField {

    private static ReflectifyRegistry registry;
    private final Accessor accessor;

    /**
     * Creates a new IReflectField using the Reflectify API, this implementation is suposed to be as fast as accessing these fields directly.
     * Most environments wont support this feature, it requires the java tools.jar and the external reflectify inside the classpath, in this case,
     * the default java.lang.reflect will be used.
     *
     * @param field
     * @param cls
     * @param set
     */
    public ReflectifyField(String field, Class<?> cls, boolean set) {
        super(field, cls, set);
        
        if (registry == null) {
            registry = new ReflectifyRuntimeRegistry(); 
        }

        Reflectify<?> reflectify = registry.get(cls);
        accessor = reflectify.getAccessor(field);
    }

    @Override
    public int getInt(Object handle) throws CoreException {
        return ((Accessor<Object, Integer>) accessor).get(handle).intValue();
    }

    @Override
    public boolean getBoolean(Object handle) throws CoreException {
        return ((Accessor<Object, Boolean>) accessor).get(handle).booleanValue();
    }

    @Override
    public byte[] getByteArray(Object handle) throws CoreException {
        return ((Accessor<Object, byte[]>) accessor).get(handle);
    }

    @Override
    public int[] getIntArray(Object handle) throws CoreException {
        return ((Accessor<Object, int[]>) accessor).get(handle);
    }

    @Override
    public byte[][] getDoubleByteArray(Object handle) throws CoreException {
        return ((Accessor<Object, byte[][]>) accessor).get(handle);
    }

    @Override
    public void set(Object value, Object handle) throws CoreException {
        ReflectionUtils.setPrivateField(handle, field, value); // meh
    }
}