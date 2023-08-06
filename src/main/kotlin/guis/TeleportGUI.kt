package guis

import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack
import util.gui.PageGUI

class TeleportGUI : PageGUI("teleporter") {
    override fun onItemClick(player: Player?, slot: Int, clickedItem: ItemStack?, clickType: ClickType?) {
        TODO("Not yet implemented")
    }

    override fun getContent(): List<ItemStack> {
        return ArrayList();
    }
}