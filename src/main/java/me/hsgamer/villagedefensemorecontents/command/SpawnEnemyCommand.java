package me.hsgamer.villagedefensemorecontents.command;

import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.villagedefensemorecontents.Utils;
import me.hsgamer.villagedefensemorecontents.VillageDefenseMoreContents;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import plugily.projects.villagedefense.arena.Arena;
import plugily.projects.villagedefense.arena.managers.spawner.EnemySpawner;
import plugily.projects.villagedefense.arena.managers.spawner.SimpleEnemySpawner;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SpawnEnemyCommand extends Command {

    public SpawnEnemyCommand() {
        super("spawnenemy", "Spawn a enemy", "/spawnenemy <zombie_name>", Collections.emptyList());
        Permission permission = new Permission("vdextra.spawnenemy", PermissionDefault.OP);
        setPermission(permission.getName());
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!testPermission(sender)) {
            return false;
        }
        if (!(sender instanceof Player)) {
            MessageUtils.sendMessage(sender, "&cThis command is player only");
            return false;
        }
        if (args.length <= 0) {
            MessageUtils.sendMessage(sender, getUsage());
            return false;
        }
        Optional<EnemySpawner> optionalEnemySpawner = VillageDefenseMoreContents.getInstance().getParentPlugin().getEnemySpawnerRegistry().getSpawnerByName(args[0]);
        if (!optionalEnemySpawner.isPresent()) {
            MessageUtils.sendMessage(sender, "&cThat enemy name is not found");
            return false;
        }
        Location location = ((Player) sender).getLocation();
        EnemySpawner enemySpawner = optionalEnemySpawner.get();
        if (!(enemySpawner instanceof SimpleEnemySpawner)) {
            MessageUtils.sendMessage(sender, "&cThat enemy is not supported");
            return false;
        }
        SimpleEnemySpawner simpleEnemySpawner = (SimpleEnemySpawner) enemySpawner;
        Optional<Arena> optionalArena = Utils.getArena((Player) sender);
        if (optionalArena.isPresent()) {
            simpleEnemySpawner.spawn(location, optionalArena.get());
        } else {
            simpleEnemySpawner.spawn(location);
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            return VillageDefenseMoreContents.getInstance().getParentPlugin()
                    .getEnemySpawnerRegistry()
                    .getEnemySpawnerSet()
                    .stream()
                    .filter(SimpleEnemySpawner.class::isInstance)
                    .map(EnemySpawner::getName)
                    .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }
}
