package util.gui

import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack


interface IGUI : InventoryHolder {  // Interface Graphical User Interface
    fun onClick(event: InventoryClickEvent)

    fun open(player: Player)

    fun update(player: Player, inventory: Inventory);

    fun populateInventory(inventory: Inventory);
}