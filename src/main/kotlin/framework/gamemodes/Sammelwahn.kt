package framework.gamemodes

import framework.Framework
import org.bukkit.Material
import org.bukkit.WorldCreator
import org.bukkit.WorldType
import org.bukkit.inventory.ItemStack
import util.item.ItemBuilder

class Sammelwahn(private val framework: Framework) : GameMode(framework) {
    override val name: String = "collectingmania"
    override val displayName: String = "Sammelwahn"
    override val displayItem: ItemStack = ItemBuilder(Material.BUNDLE).build()
    override val worldName: String = "collectingmania"
    override val description: String =
        "placeholder"
    override val minPlayers: Int = 3
    override val maxPlayers: Int = -1
    override val HasPoints: Boolean = false
    override val IsFinale: Boolean = false
    override var timeLimit = 60 * 5 // 5 minutes

    override fun getWorldCreator(): WorldCreator {
        return WorldCreator(worldName)
            .generateStructures(false)
            .type(WorldType.NORMAL)
    }

    override fun unregisterEventListener() {
        TODO("Not yet implemented")
    }

    override fun prepare() {
        TODO("Not yet implemented")
    }

    override fun start() {
        TODO("Not yet implemented")
    }

    override fun stop() {
        TODO("Not yet implemented")
    }

    override fun reset() {
        TODO("Not yet implemented")
    }
}