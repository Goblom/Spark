/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.spark.internals.task;

import codes.goblom.spark.Log;
import codes.goblom.spark.SparkPlugin;
import codes.goblom.spark.internals.Callback;
import codes.goblom.spark.internals.ExecutorArgs;
import codes.goblom.spark.internals.ExecutorNoArgs;
import codes.goblom.spark.internals.Spark;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

/**
 *
 * @author Goblom
 */
public abstract class AsyncTask<T> implements ExecutorNoArgs<T, Throwable> {    
    private Callback<T> callback = null;
    private final Caller<T> caller;
    
    protected BukkitTask task;
    protected Spark spark;
    
    public AsyncTask() {
        this(null);
    }
    
    public AsyncTask(Callback<T> callback) {
        this.spark = Spark.getMainInstance();
        this.callback = callback;
        this.caller = new Caller<>(this);
    }
        
    public final void cancel() {
        if (task != null) {
            task.cancel();
        } else {
            log("Attempted to cancel a task that has not yet started");
        }
    }
    
    private void log(String message) {
        Log.find(spark).warning(message);
    }
    
    public AsyncTask<T> with(SparkPlugin plugin) {
        this.spark = plugin;
        
        return this;
    }
    
    @Override
    public final T execute(ExecutorArgs args) throws Throwable {
        return execute();
    }
    
    public final BukkitTask run() {
        this.task = Bukkit.getScheduler().runTaskAsynchronously(spark, caller);
        return task;
    }
    
    public final BukkitTask runLater(long delay) {
        this.task = Bukkit.getScheduler().runTaskLaterAsynchronously(spark, caller, delay);
        return task;
    }
    
    public final BukkitTask runTimer(long delay, long period) {
        this.task = Bukkit.getScheduler().runTaskTimerAsynchronously(spark, caller, delay, period);
        return task;
    }

    @RequiredArgsConstructor
    private static final class Caller<T> implements Runnable {
        private final AsyncTask<T> runnable;
        
        @Override
        public void run() {
            Throwable thrown = null;
            T obj = null;

            try {
                obj = runnable.execute();
            } catch (Throwable t) {
                thrown = t;
            }

            if (runnable.callback != null) {
                runnable.callback.onFinish(obj, thrown);
            }

            if (thrown != null) {
                thrown.printStackTrace();
            }
        }
        
    }
}
