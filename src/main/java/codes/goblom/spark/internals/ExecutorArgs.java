/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.spark.internals;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/**
 *
 * @author Goblom
 */
@RequiredArgsConstructor( access = AccessLevel.PROTECTED )
public class ExecutorArgs implements Iterable<Object> {

    protected static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
    
    protected final Object[] objects;

    public Object get() {
        return get(0);
    }

    public Object get(int i) {
        return objects[i];
    }

    public <T> T getAs(Class<T> clazz) {
        return getAs();
    }
    
    public <T> T getAs(int i, Class<T> clazz) {
        return getAs(i);
    }
    
    public <T> T getAs() {
        return (T) get();
    }

    public <T> T getAs(int i) {
        return (T) get(i);
    }
    
    public boolean is(Class type) {
        return type.isAssignableFrom(get().getClass());
    }
    
    public boolean is(int i, Class type) {
        return type.isAssignableFrom(get(i).getClass());
    }
    
    protected static Builder Builder() {
        return new Builder();
    }

    @Override
    public Iterator<Object> iterator() {
        return new ExecutorArgsIterator(this);
    }
    
    public static ExecutorArgs wrap(Object... args) {
        if (args == null || args.length == 0) {
            return Executor.EMPTY_ARGS;
        }
        
        Builder builder = Builder();
        
        for (Object arg : args) {
            builder.put(arg);
        }
        
        return builder.build();
    }
    
    protected static class Builder {
        private final Map<Integer, Object> map = Maps.newHashMap();
        private int slot = 0;
        
        private Builder() { }
        
        public Builder put(Object o) {
            map.put(slot++, o);
            
            return this;
        }
        
        public ExecutorArgs build() {
            if (slot == 0) {
                return new ExecutorArgs(EMPTY_OBJECT_ARRAY);
            }
            
//            int size = slot >= 2 ? slot - 1 : slot; // We dont want the array size to be zero at any time
//            System.out.println(size);
//            
//            Object[] args = new Object[size]; 
//            
//            map.entrySet().forEach((entry) -> {
//                System.out.println(String.format("args[%s] = %s", entry.getKey(), entry.getValue()));
//                args[entry.getKey()] = entry.getValue();
//            });
            
            return new ExecutorArgs(map.values().toArray());
        }
    }
    
    @RequiredArgsConstructor
    static class ExecutorArgsIterator implements Iterator<Object> {
        private final ExecutorArgs args;
        private int index = 0;
        
        @Override
        public boolean hasNext() {
            return index < args.objects.length && args.objects[index] != null;
        }

        @Override
        public Object next() {
            return args.objects[index++];
        }
    }
}
