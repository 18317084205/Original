package com.jianbo.toolkit.prompt;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ClassUtils {
    public static Type[] getParameterizedTypesWithInterfaces(Object object) {
        return object.getClass().getGenericInterfaces();
    }

    public static List<Type> methodHandler(Type[] types) {
        List<Type> needtypes = new ArrayList<>();
        for (Type paramType : types) {
            System.out.println("  " + paramType);
            // if Type is T
            if (paramType instanceof ParameterizedType) {
                Type[] parentypes = ((ParameterizedType) paramType).getActualTypeArguments();
                for (Type childtype : parentypes) {
                    needtypes.add(childtype);
                    if (childtype instanceof ParameterizedType) {
                        Type[] childtypes = ((ParameterizedType) childtype).getActualTypeArguments();
                        for (Type type : childtypes) {
                            needtypes.add(type);
                        }
                    }
                }
            }
        }
        return needtypes;
    }

    public static Class<?> getTClass(Type type) {
        String className = getClassName(type);
        if (className == null || className.isEmpty()) {
            return null;
        }
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getClassName(Type type) {
        if (type == null) {
            return "";
        }
        String className = type.toString();
        if (className.startsWith("class ")) {
            className = className.substring("interface ".length());
        } else if (className.startsWith("interface ")) {
            className = className.substring("interface ".length());
        }
        return className;
    }
}
