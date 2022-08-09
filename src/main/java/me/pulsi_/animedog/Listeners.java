package me.pulsi_.animedog;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Listeners implements Listener {

    private final HashMap<UUID, Player> dogOwner = new HashMap<>();
    private final List<UUID> dogs = new ArrayList<>();
    private final List<UUID> angryDogs = new ArrayList<>();

    @EventHandler
    public void onDogFeed(PlayerInteractEntityEvent e) {
        Entity eDog = e.getRightClicked();
        Player p = e.getPlayer();

        if (!(eDog instanceof Wolf)) return;
        if (!p.getItemInHand().getType().equals(Material.ENCHANTED_GOLDEN_APPLE)) return;
        e.setCancelled(true);

        p.getItemInHand().setAmount(p.getItemInHand().getAmount() -1);
        AnimeDog.getInstance().getPlayerData().set(p.getUniqueId() + ".Dog", eDog.getUniqueId().toString());
        AnimeDog.getInstance().savePlayerData();

        dogs.add(eDog.getUniqueId());
        dogOwner.put(eDog.getUniqueId(), p);

        eDog.setCustomName(ChatColor.translateAlternateColorCodes('&', "&6&l" + p.getName() + "'s &2&lA&9&ln&2&li&9&lm&2&le &c&lDog"));
        eDog.setCustomNameVisible(true);

        ParticlesUtils.createHorizontalCircle(eDog.getLocation(), 1.2, 8, Particle.VILLAGER_HAPPY);
        p.playSound(p.getLocation(), Sound.ENTITY_CHICKEN_EGG, Float.MAX_VALUE, 1);
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent e) {
        Entity eDog = e.getRightClicked();
        if (eDog instanceof Wolf && dogs.contains(eDog.getUniqueId())) switchMood(e.getPlayer(), (Wolf) eDog, e);
    }

    private void switchMood(Player p, Wolf wolf, PlayerInteractEntityEvent e) {
        switch (p.getItemInHand().getType()) {
            case CARROT:
                if (angryDogs.contains(wolf.getUniqueId())) {
                    p.sendMessage("The dog is already angry!");
                    return;
                }
                e.setCancelled(true);
                p.getItemInHand().setAmount(p.getItemInHand().getAmount() -1);
                angryDogs.add(wolf.getUniqueId());
                wolf.getWorld().spawnParticle(Particle.FLAME, wolf.getLocation(), 5, 0.4, 0.4, 0.4);
                wolf.setCustomName(ChatColor.translateAlternateColorCodes('&', "&6&l" + p.getName() + "'s &2&lA&9&ln&2&li&9&lm&2&le &c&lDog &4&l(Angry)"));
                loopAttack(p, wolf);
                break;

            case CAKE:
                if (!angryDogs.contains(wolf.getUniqueId())) {
                    p.sendMessage("The dog is not angry!");
                    return;
                }
                e.setCancelled(true);
                p.getItemInHand().setAmount(p.getItemInHand().getAmount() -1);
                angryDogs.remove(wolf.getUniqueId());
                wolf.getWorld().spawnParticle(Particle.HEART, wolf.getLocation(), 5);
                wolf.setCustomName(ChatColor.translateAlternateColorCodes('&', "&6&l" + p.getName() + "'s &2&lA&9&ln&2&li&9&lm&2&le &c&lDog"));
        }
    }

    private void loopAttack(Player p, Wolf wolf) {
        if (!angryDogs.contains(wolf.getUniqueId())) return;

        List<Entity> nearbyEntities = wolf.getNearbyEntities(4, 4, 4);
        for(Entity entity : nearbyEntities) {
            if (!(entity instanceof Damageable) || entity.isDead() || entity.equals(p) || dogs.contains(entity.getUniqueId())) continue;
            Location loc1 = wolf.getLocation();
            wolf.teleport(entity.getLocation());
            Location loc2 = wolf.getLocation();
            spawnParticles(loc1, loc2);

            wolf.getWorld().playSound(wolf.getLocation(), Sound.ENTITY_PLAYER_HURT, 10, 1);
            entity.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, entity.getLocation(), 3);
            ((Damageable) entity).damage(500, wolf);

            Bukkit.getScheduler().runTaskLater(AnimeDog.getInstance(), () -> loopAttack(p, wolf), 3);
            return;
        }
        Bukkit.getScheduler().runTaskLater(AnimeDog.getInstance(), () -> loopAttack(p, wolf), 3);
    }

    @EventHandler
    public void onEntityHit(EntityDamageByEntityEvent e) {
        Entity en = e.getDamager();
        if (!(en instanceof Wolf) || !dogs.contains(en.getUniqueId())) return;
        Entity target = e.getEntity();
        if (!(target instanceof Damageable)) return;

        Wolf wolf = (Wolf) en;
        attack(dogOwner.get(wolf.getUniqueId()), wolf, (Damageable) target);
    }

    @EventHandler
    public void onWolfFall(EntityDamageEvent e) {
        if (dogs.contains(e.getEntity().getUniqueId())) e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        AnimeDog.getInstance().getPlayerData().set(p.getUniqueId() + ".Name", p.getName());
        AnimeDog.getInstance().savePlayerData();
    }

    private void attack(Player p, Wolf wolf, Damageable target) {
        if (wolf.isDead()) return;

        Location loc1 = wolf.getLocation();
        wolf.teleport(target.getLocation());
        Location loc2 = wolf.getLocation();

        spawnParticles(loc1, loc2);
        wolf.getWorld().playSound(wolf.getLocation(), Sound.ENTITY_PLAYER_HURT, 10, 1);
        target.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, target.getLocation(), 3);
        target.damage(500, wolf);

        List<Entity> nearbyEntities = wolf.getNearbyEntities(20, 20, 20);
        for(Entity entity : nearbyEntities) {
            if (!(entity instanceof Damageable) || entity.isDead() || entity.equals(p) || dogs.contains(entity.getUniqueId())) continue;
            Bukkit.getScheduler().runTaskLater(AnimeDog.getInstance(), () -> attack(p, wolf, (Damageable) entity), 3);
            return;
        }
    }

    private void spawnParticles(Location loc1, Location loc2) {
        double distance = loc1.distance(loc2);
        for (int i = 0; i < distance; i++) {
            Location location = loc1.clone();
            Vector direction = loc2.toVector().subtract(loc1.toVector()).normalize();
            Vector v = direction.multiply(i);
            location.add(v.getX(), v.getY(), v.getZ());

            loc1.getWorld().spawnParticle(Particle.FLAME, location, 50, 0.1, 0.1, 0.1, 0);
        }
    }
}