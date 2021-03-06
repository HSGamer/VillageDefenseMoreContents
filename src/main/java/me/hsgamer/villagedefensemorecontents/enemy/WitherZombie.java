package me.hsgamer.villagedefensemorecontents.enemy;

import me.hsgamer.villagedefensemorecontents.VillageDefenseMoreContents;
import me.hsgamer.villagedefensemorecontents.config.MainConfig;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.WitherSkull;
import org.bukkit.util.Vector;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.commonsbox.minecraft.compat.xseries.XMaterial;
import plugily.projects.villagedefense.creatures.CreatureUtils;

import java.util.List;

public class WitherZombie implements RunnableEnemySpawner {
    @Override
    public Creature createBaseEnemy(Location location) {
        Creature creature = CreatureUtils.getCreatureInitializer().spawnFastZombie(location);
        creature.getEquipment().setHelmet(XMaterial.WITHER_SKELETON_SKULL.parseItem());
        creature.getEquipment().setHelmetDropChance(0.0F);
        return creature;
    }

    @Override
    public void onTick(Creature creature) {
        if (creature.getTarget() == null) {
            return;
        }
        double checkAhead = MainConfig.ZOMBIE_WITHER_CHECK_PLAYER_AHEAD_ANGLE.getValue();
        boolean charged = MainConfig.ZOMBIE_WITHER_CHARGED.getValue();
        double multiply = MainConfig.ZOMBIE_WITHER_SHOOT_POWER.getValue();
        Bukkit.getScheduler().runTask(VillageDefenseMoreContents.getInstance(), () -> {
            List<Entity> nearbyEntities = creature.getNearbyEntities(5, 2, 5);
            if (!nearbyEntities.isEmpty() &&
                    nearbyEntities.parallelStream()
                            .filter(entity -> !CreatureUtils.isEnemy(entity))
                            .map(humanEntity -> Math.abs(humanEntity.getLocation().toVector().angle(creature.getEyeLocation().getDirection()) * Math.PI / 180))
                            .noneMatch(angle -> angle > checkAhead)
            ) {
                return;
            }
            Location location = creature.getLocation();
            Vector power = location.getDirection().multiply(multiply);
            WitherSkull skull = creature.launchProjectile(WitherSkull.class, power);
            skull.setCharged(charged);
        });
    }

    @Override
    public long getPeriod() {
        return MainConfig.ZOMBIE_WITHER_SHOOT_DELAY.getValue();
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    @Override
    public String getName() {
        return "WitherZombie";
    }

    @Override
    public int getMinWave() {
        return MainConfig.ZOMBIE_WITHER_WAVE.getValue();
    }

    @Override
    public double getSpawnRate(Arena arena, int wave, int phase, int spawnAmount) {
        return MainConfig.ZOMBIE_WITHER_RATE.getValue();
    }

    @Override
    public boolean checkPhase(Arena arena, int wave, int phase, int spawnAmount) {
        return MainConfig.ZOMBIE_WITHER_PHASE.getValue().contains(phase);
    }

    @Override
    public int getFinalAmount(Arena arena, int wave, int phase, int spawnAmount) {
        return Math.min(spawnAmount, MainConfig.ZOMBIE_WITHER_AMOUNT.getValue());
    }
}
