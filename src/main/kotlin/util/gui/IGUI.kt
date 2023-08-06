package util.gui

import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack


interface IGUI : InventoryHolder {  // Interface Graphical User Interface
    fun onClick(player: Player, slot: Int, clickedItem: ItemStack?, clickType: ClickType)
    fun open(player: Player)
}