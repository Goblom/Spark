/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.spark.reflection;

import codes.goblom.spark.reflection.safe.SafeClass;
import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Map;
import lombok.Getter;
import org.bukkit.Bukkit;

/**
 *
 * @author Goblom
 */
public class Reflection {
    
    @Getter
    private static final String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
    private static final Map<String, Map<String, SafeClass>> storage = Maps.newHashMap();
    
    static {
        Arrays.asList("nms", "craft", "other").forEach((type) -> { storage.put(type, Maps.newConcurrentMap()); });
    }
    
    private Reflection() { }
    
    public static SafeClass getNMSClass(String clazz) {
        Map<String, SafeClass> classes = storage.get("nms");
        String classFor = String.format("net.minecraft.server.%s.%s", version, clazz);
        
        if (classes.containsKey(classFor)) {
            return classes.get(classFor);
        }
        
        SafeClass safeClass = new SafeClass(classFor);
        
        if (safeClass.isOk()) {
            classes.put(classFor, safeClass);
        }
        
        return safeClass;
    }
    
    public static SafeClass getCraftClass(String clazz) {
        Map<String, SafeClass> classes = storage.get("craft");
        String classFor = String.format("org.bukkit.craftbukkit.%s.%s", version, clazz);
        
        if (classes.containsKey(classFor)) {
            return classes.get(classFor);
        }
        
        SafeClass safeClass = new SafeClass(classFor);
        
        if (safeClass.isOk()) {
            classes.put(classFor, safeClass);
        }
        
        return safeClass;
    }
    
    public static SafeClass getClass(String clazz) {
        Map<String, SafeClass> classes = storage.get("other");
        
        if (classes.containsKey(clazz)) {
            return classes.get(clazz);
        }
        
        SafeClass safeClass = new SafeClass(clazz);
        
        if (safeClass.isOk()) {
            classes.put(clazz, safeClass);
        }
        
        return safeClass;
    }
    
    public static Enum<?> getEnumValue(SafeClass enumClass, String value) {
        return getEnumValue(enumClass.unsafe2(), value);
    }
    
    public static Enum<?> getEnumValue(Class enumClass, String value) {
        if (!enumClass.isEnum()) {
            return null;
        }
        
        try {
            return Enum.valueOf(enumClass, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public static boolean isEnumType(SafeClass enumClass, String value) {
        return getEnumValue(enumClass.unsafe2(), value) != null;
    }
    
    public static boolean isEnumType(Class<? extends Enum> enumClass, String value) {
        return getEnumValue(enumClass, value) != null;
    }
}
