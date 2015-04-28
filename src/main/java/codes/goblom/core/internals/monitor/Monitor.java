/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.core.internals.monitor;

import codes.goblom.core.Log;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

/**
 *
 * @author Goblom
 */
public abstract class Monitor {
    BukkitTask runner;
    
    protected Monitor() { }
    
    public final void stop() {
        if (runner != null) {
            runner.cancel();
            
            if (this instanceof Listener) {
                HandlerList.unregisterAll((Listener) this);
            }
        } else {
            Log.warning("Attempted to stop a non-started Monitor[%s]", getClass().getSimpleName());
        }
    }
    
    public final String getName() {
        return getClass().getSimpleName();
    }
    
    public abstract void update();
    
    @Retention( RetentionPolicy.RUNTIME )
    public @interface TickInterval { /* long value() */ }
    
    @Retention( RetentionPolicy.RUNTIME )
    public @interface TickDelay { /* long value() */ }
    
    @Retention( RetentionPolicy.RUNTIME )
    public @interface Async { }
}
