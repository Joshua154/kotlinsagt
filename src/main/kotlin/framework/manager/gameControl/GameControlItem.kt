package framework.manager.gameControl

import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import util.gui.IGUI
import util.item.ItemBuilder

class GameControlItem(
    val name: Component,
    private val description: Component,
    private val displayItem: Material,
    private val gui: IGUI
) {
    fun getItem(): ItemStack {
        return ItemBuilder(displayItem)
            .setName(name)
            .setLore(description)
            .build()
    }

    fun getGUI(): IGUI {
        return gui
    }
}