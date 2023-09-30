package framework.gamemodes

import framework.Framework
import org.bukkit.Material
import org.bukkit.WorldCreator
import org.bukkit.WorldType
import org.bukkit.event.entity.ExplosionPrimeEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.inventory.ItemStack
import util.item.ItemBuilder

class Sammelwahn(private val framework: Framework) : GameMode(framework) {
    override val name: String = "collectingmania"
    override val displayName: String = "Sammelwahn"
    override val displayItem: ItemStack = ItemBuilder(Material.CHEST).build()
    override val worldName: String = "collectingmania"
    override val description: String =
        "placeholder"
    override val minPlayers: Int = 3
    override val maxPlayers: Int = Int.MAX_VALUE
    override val hasPoints: Boolean = false
    override val hasPreBuildWorld: Boolean = false
    override val isFinale: Boolean = false
    override var timeLimit = 60 * 5 // 5 minutes

    override fun getWorldCreator(): WorldCreator {
        return super.getWorldCreator()
            .generateStructures(false)
            .type(WorldType.NORMAL)
            .generatorSettings("{}")
    }

    override fun prepare() {
        load()
        registerEventListener()
        start() // COMBAK later to be called by command/ui
    }

    override fun unregisterEventListener() {

    }

    override fun start() {
        tpPlayersToGame()
        isRunning = true
    }

    override fun stop() {
        isRunning = false
    }
}