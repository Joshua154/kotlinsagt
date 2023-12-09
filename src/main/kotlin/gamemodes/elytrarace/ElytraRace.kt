package gamemodes.elytrarace

import framework.Framework
import framework.gamemode.GameMode
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import util.item.ItemBuilder

class ElytraRace(private val framework: Framework) : GameMode(framework) {
    override val name: String = "elytrarace";
    override val displayName: String = "Elytra Race";
    override val displayItem: ItemStack = ItemBuilder(Material.ELYTRA).build();
    override val worldName: String = "elytrarace"
    override val description: String = "todo";
    override val minPlayers: Int = 2;
    override val maxPlayers: Int = Int.MAX_VALUE;
    override val hasPreBuiltWorld: Boolean = false;

    // Modus
    override val roundTime: Int = 60 * 5 // 5 minutes

    // var remainingTime for countdown
    override val hasPoints: Boolean = false

    // default survivorRate = 1.0
    override val hasTeams: Boolean = false

    override val isFinale: Boolean = false
    override fun prepare() {
        TODO("Not yet implemented")
    }

    override fun setupPlayer(player: Player) {
        player.inventory.clear();
        player.gameMode = org.bukkit.GameMode.ADVENTURE;
        player.equipment.chestplate = ItemBuilder(Material.ELYTRA).build();
        //TODO; Teleport to start point
    }

    override fun start() {
        TODO("Not yet implemented")
    }

    override fun stop() {
        TODO("Not yet implemented")
    }
}