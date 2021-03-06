/*
 * Copyright 2015 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.spark.misc.utils;

import com.google.common.collect.Lists;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;

/**
 *
 * @author Goblom
 */
public abstract class Geometry<T> {

    /**
     * Can not be using Async
     */
    public static final Geometry<Location> LOCATION = new LocationGeometry();

    /**
     * Can be used Async
     */
    public static final Geometry<Vector3D> VECTOR = new VectorGeometry();

    public abstract List<T> generateHelix(T center, int radius, double height);

    public abstract List<T> generateCircle(T center, int radius, int height, boolean hollow, boolean sphere);

    public abstract List<T> generateCuboid(T bound1, T bound2);
    
    public abstract boolean isInBorder(T center, T check, int range);
    
    public final CircleBuilder newCircleBuilder(T center) {
        return new CircleBuilder(center);
    }
    
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public final class CircleBuilder {

        final T center;
        int radius = 5, height = 5;
        boolean hollow = false, sphere = true;

        public CircleBuilder radius(int radius) {
            this.radius = radius;
            return this;
        }

        public CircleBuilder height(int height) {
            this.height = height;
            return this;
        }

        public CircleBuilder hollow(boolean hollow) {
            this.hollow = hollow;
            return this;
        }

        public CircleBuilder sphere(boolean sphere) {
            this.sphere = sphere;
            return this;
        }

        public List<T> generateCircle() {
            if (!Utils.isValid(center)) {
                return Lists.newLinkedList();
            }

            return Geometry.this.generateCircle(center, radius, height, hollow, sphere);
        }
    }

    static class LocationGeometry extends Geometry<Location> {

        @Override
        public List<Location> generateHelix(Location center, int radius, double height) {
            List<Location> list = Lists.newLinkedList();

            for (double y = 0; y < height; y += 0.5) {
                double x = radius * Math.cos(y);
                double z = radius * Math.sin(y);

                list.add(new Location(center.getWorld(), center.getX() + x, center.getY() + y, center.getZ() + z));
            }

            return list;
        }

        @Override
        public List<Location> generateCircle(Location center, int radius, int height, boolean hollow, boolean sphere) {
            List<Location> list = Lists.newLinkedList();
            int cx = center.getBlockX(); 
            int cy = center.getBlockY();
            int cz = center.getBlockZ();
            
            for (int x = cx - radius; x <= cx + radius; x++) {
                for (int z = cz - radius; z <= cz + radius; z++) {
                    for (int y = (sphere ? cy - radius : cy); y < (sphere ? cy + radius : cy + height); y++) {
                        double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                        
                        if (dist < radius * radius && !(hollow && dist < (radius - 1) * (radius - 1))) {
                            list.add(new Location(center.getWorld(), x, y, z));
                        }
                    }
                }
            }
            return list;
        }

        @Override
        public boolean isInBorder(Location center, Location check, int range) {
            int x = center.getBlockX(); 
            int z = center.getBlockZ();
            int x1 = check.getBlockX();
            int z1 = check.getBlockZ();
            
            return !(x1 >= (x + range) || z1 >= (z + range) || x1 <= (x - range) || z1 <= (z - range));
        }

        @Override
        public List<Location> generateCuboid(Location bound1, Location bound2) {
            List<Location> list = Lists.newArrayList();
            
            final int minx = Math.min(bound1.getBlockX(), bound2.getBlockX());
            final int miny = Math.min(bound1.getBlockY(), bound2.getBlockY());
            final int minz = Math.min(bound1.getBlockZ(), bound2.getBlockZ());
            final int maxx = Math.max(bound1.getBlockX(), bound2.getBlockX());
            final int maxy = Math.max(bound1.getBlockY(), bound2.getBlockY());
            final int maxz = Math.max(bound1.getBlockZ(), bound2.getBlockZ());
            
            for (int x = minx; x <= maxx; ++x) {
                for (int y = miny; y <= maxy; ++y) {
                    for (int z = minz; z <= maxz; ++z) {
                        list.add(new Location(bound1.getWorld(), x, y, z));
                    }
                }
            }
            
            return list;
        }
    }

    static class VectorGeometry extends Geometry<Vector3D> {

        @Override
        public List<Vector3D> generateHelix(Vector3D center, int radius, double height) {
            List<Vector3D> list = Lists.newLinkedList();

            for (double y = 0; y < height; y += 0.5) {
                double x = radius * Math.cos(y);
                double z = radius * Math.sin(y);

                list.add(new Vector3D(center.getX() + x, center.getY() + y, center.getZ() + z));
            }

            return list;
        }
        
        @Override
        public List<Vector3D> generateCircle(Vector3D center, int radius, int height, boolean hollow, boolean sphere) {
            List<Vector3D> list = Lists.newLinkedList();
            int cx = (int) center.getX(); 
            int cy = (int) center.getY();
            int cz = (int) center.getZ();
            
            for (int x = cx - radius; x <= cx + radius; x++) {
                for (int z = cz - radius; z <= cz + radius; z++) {
                    for (int y = (sphere ? cy - radius : cy); y < (sphere ? cy + radius : cy + height); y++) {
                        double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                        
                        if (dist < radius * radius && !(hollow && dist < (radius - 1) * (radius - 1))) {
                            list.add(new Vector3D(x, y, z));
                        }
                    }
                }
            }
            return list;
        }
        
        @Override
        public boolean isInBorder(Vector3D center, Vector3D check, int range) {
            double x = center.getX();
            double z = center.getZ();
            double x1 = check.getX();
            double z1 = check.getZ();
            
            return !(x1 >= (x + range) || z1 >= (z + range) || x1 <= (x - range) || z1 <= (z - range));
        }
        
        @Override
        public List<Vector3D> generateCuboid(Vector3D bound1, Vector3D bound2) {
            List<Vector3D> list = Lists.newArrayList();
            
            final double minx = Math.min(bound1.getX(), bound2.getX());
            final double miny = Math.min(bound1.getY(), bound2.getY());
            final double minz = Math.min(bound1.getZ(), bound2.getZ());
            final double maxx = Math.max(bound1.getX(), bound2.getX());
            final double maxy = Math.max(bound1.getY(), bound2.getY());
            final double maxz = Math.max(bound1.getZ(), bound2.getZ());
            
            for (double x = minx; x <= maxx; ++x) {
                for (double y = miny; y <= maxy; ++y) {
                    for (double z = minz; z <= maxz; ++z) {
                        list.add(new Vector3D(x, y, z));
                    }
                }
            }
            
            return list;
        }
        
    }
}
