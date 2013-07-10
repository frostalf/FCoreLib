package me.FurH.Core.reflection;

import me.FurH.Core.exceptions.CoreException;

/**
 *
 * @author FurmigaHumana
 * All Rights Reserved unless otherwise explicitly stated.
 */
public interface IReflectionUtils {

    public void setFinalField(Object obj, String field, Object value) throws CoreException;

    public Object getPrivateField(Object obj, String field) throws CoreException;

    public int getPrivateIntField(Object obj, String field) throws CoreException;

    public boolean getPrivateBooleanField(Object obj, String field) throws CoreException;

    public Object getPrivateField(Class<?> obj, Object instance, String field) throws CoreException;

    public int getPrivateIntField(Class<?> obj, Object instance, String field) throws CoreException;

    public void setPrivateField(Object obj, String field, Object value) throws CoreException;
    
}
