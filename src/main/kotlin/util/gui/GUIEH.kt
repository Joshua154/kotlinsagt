package util.gui

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent

class GUIEH : Listener {
    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        if (event.inventory.holder !is IGUI) return
        val igui: IGUI = event.inventory.holder as IGUI
        if (event.clickedInventory == null || event.clickedInventory!!.holder is Player) return
        if (event.getCurrentItem() == null) return
        event.isCancelled = true
        igui.onClick(
            event.whoClicked as Player, event.rawSlot, event.getCurrentItem(),
            event.click
        )
    }
}
