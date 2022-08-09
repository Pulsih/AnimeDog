package me.pulsi_.animedog;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

public class ParticlesUtils {

    public static void createHorizontalCircle(Location loc, double radius, int pointsAmount, Particle particle) {
        World world = loc.getWorld();
        double increment = (2 * Math.PI) / pointsAmount;
        List<Location> locations = new ArrayList<>();

        for (int i = 0; i < pointsAmount; i++) {
            double angle = i * increment;
            double x = loc.getX() + (radius * Math.cos(angle));
            double z = loc.getZ() + (radius * Math.sin(angle));
            locations.add(new Location(world, x, loc.getY(), z));
        }

        for (Location l : locations) world.spawnParticle(particle, l, 1);
    }
}