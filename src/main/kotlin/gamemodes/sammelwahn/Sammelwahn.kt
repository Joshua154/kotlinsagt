package gamemodes.sammelwahn

import framework.Framework
import framework.gamemode.GameMode
import org.bukkit.Material
import org.bukkit.WorldCreator
import org.bukkit.WorldType
import org.bukkit.inventory.ItemStack
import util.item.ItemBuilder

class Sammelwahn(private val framework: Framework) : GameMode(framework) {
    // Description
    override val name: String = "collectingmania"
    override val displayName: String = "Sammelwahn"
    override val displayItem: ItemStack = ItemBuilder(Material.CHEST).build()
    override val worldName: String = "collectingmania"
    override val description: String =
        "placeholder"
    override val minPlayers: Int = 3
    override val maxPlayers: Int = Int.MAX_VALUE
    override val hasPreBuiltWorld: Boolean = false

    // Modus
    override val roundTime: Int = 60 * 5 // 5 minutes

    // var remainingTime for countdown
    override val hasPoints: Boolean = false

    // default survivorRate = 1.0
    override val hasTeams: Boolean = false

    // default teamQuantity = 2
    override val isFinale: Boolean = false

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

    override fun start() {
        tpPlayersToGame()
        isRunning = true
    }

    override fun stop() {
        isRunning = false
    }
}