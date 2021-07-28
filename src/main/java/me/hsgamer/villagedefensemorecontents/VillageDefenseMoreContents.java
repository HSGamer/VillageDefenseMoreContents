package me.hsgamer.villagedefensemorecontents;

import me.hsgamer.hscore.bukkit.baseplugin.BasePlugin;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.villagedefensemorecontents.command.SpawnEnemyCommand;
import me.hsgamer.villagedefensemorecontents.config.MainConfig;
import me.hsgamer.villagedefensemorecontents.config.MessageConfig;
import me.hsgamer.villagedefensemorecontents.enemy.BomberZombie;
import me.hsgamer.villagedefensemorecontents.enemy.GhostZombie;
import me.hsgamer.villagedefensemorecontents.enemy.TeleporterZombie;
import me.hsgamer.villagedefensemorecontents.enemy.WitherZombie;
import me.hsgamer.villagedefensemorecontents.kit.AngelKit;
import me.hsgamer.villagedefensemorecontents.kit.DefuserKit;
import me.hsgamer.villagedefensemorecontents.powerup.LightningStrikePowerUp;
import org.bukkit.plugin.java.JavaPlugin;
import plugily.projects.villagedefense.Main;
import plugily.projects.villagedefense.arena.managers.EnemySpawnerRegistry;
import plugily.projects.villagedefense.arena.managers.spawner.EnemySpawner;
import plugily.projects.villagedefense.kits.KitRegistry;

import java.util.Set;

public final class VillageDefenseMoreContents extends BasePlugin {
    private static VillageDefenseMoreContents instance;

    private final MainConfig mainConfig = new MainConfig(this);
    private final MessageConfig messageConfig = new MessageConfig(this);

    private Main parentPlugin;

    public static VillageDefenseMoreContents getInstance() {
        return instance;
    }

    @Override
    public void preLoad() {
        instance = this;
    }

    @Override
    public void load() {
        MessageUtils.setPrefix("");
    }

    @Override
    public void enable() {
        mainConfig.setup();
        messageConfig.setup();

        parentPlugin = JavaPlugin.getPlugin(Main.class);

        registerPowerUp();
        registerKit();
        registerEnemy();
        registerCommand();
    }

    private void registerKit() {
        if (MainConfig.KIT_DEFUSER_ENABLED.getValue()) {
            KitRegistry.registerKit(new DefuserKit());
        }
        if (MainConfig.KIT_ANGEL_ENABLED.getValue()) {
            KitRegistry.registerKit(new AngelKit());
        }
    }

    private void registerPowerUp() {
        new LightningStrikePowerUp().tryRegister(parentPlugin);
    }

    private void registerCommand() {
        registerCommand(new SpawnEnemyCommand());
    }

    private void registerEnemy() {
        EnemySpawnerRegistry spawnerRegistry = parentPlugin.getEnemySpawnerRegistry();
        Set<EnemySpawner> enemySpawnerSet = spawnerRegistry.getEnemySpawnerSet();
        enemySpawnerSet.add(new GhostZombie());
        enemySpawnerSet.add(new BomberZombie());
        enemySpawnerSet.add(new WitherZombie());
        enemySpawnerSet.add(new TeleporterZombie());
    }

    public Main getParentPlugin() {
        return parentPlugin;
    }

    public MainConfig getMainConfig() {
        return mainConfig;
    }

    public MessageConfig getMessageConfig() {
        return messageConfig;
    }
}
