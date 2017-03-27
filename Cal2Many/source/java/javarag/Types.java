package javarag;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

public class Types {
	public static Type findSuperclass(Type type, Class<?> target) {
		if (type == null) {
			return null;
		} else if (isOfClass(type, target)) {
			return type;
		} else {
			Type superclass = getSuperclass(type);
			return findSuperclass(superclass, target);
		}
	}
	
	public static Type getSuperclass(Type type) {
		if (type instanceof Class) {
			Class<?> classObject = (Class<?>) type;
			return classObject.getGenericSuperclass();
		} else if (type instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) type;
			return getSuperclass(parameterizedType.getRawType());
		} else {
			return null;
		}
	}
	
	public static boolean isOfClass(Type type, Class<?> klass) {
		if (type instanceof Class) {
			return type.equals(klass);
		} else if (type instanceof ParameterizedType) {
			Type raw = ((ParameterizedType) type).getRawType();
			return raw.equals(klass);
		}
		return false;
	}
	
	public static Type[] getTypeParameters(Type type) {
		if (type instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) type;
			return parameterizedType.getActualTypeArguments();
		} else {
			return null;
		}
	}
	
	public static Class<?> getRawType(Type type) {
		if (type instanceof Class) {
			return (Class<?>) type;
		} else if (type instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) type;
			return getRawType(parameterizedType.getRawType());
		} else {
			return null;
		}
	}

	public static Class<?>[] getTypeAsInterfaces(Type param) {
		if (param instanceof Class) {
			Class<?> interf = ((Class<?>) param);
			if (!interf.isInterface()) {
				throw new RuntimeException();
			}
			return new Class[] { interf };
		} else if (param instanceof TypeVariable<?>) {
			Type[] bounds = ((TypeVariable<?>) param).getBounds();
			Class<?>[] interfaces = new Class[bounds.length];
			int i = 0;
			for (Type bound : bounds) {
				Class<?> interf = Types.getRawType(bound);
				if (!interf.isInterface()) {
					throw new RuntimeException();
				}
				interfaces[i++] = interf;
			}
			return interfaces;
		}
		return null;
	}
}
