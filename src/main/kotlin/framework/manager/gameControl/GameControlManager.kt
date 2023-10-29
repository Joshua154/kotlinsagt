package framework.manager.gameControl

import FuxelSagt
import framework.Framework
import guis.GameModeSelection
import guis.TeleportGUI
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.persistence.PersistentDataType
import util.item.ItemBuilder

class GameControlManager(val framework: Framework) : Listener {
    private val items: Map<String, GameControlItem> = mapOf(
        Pair("teleport", GameControlItem(Component.text("Teleport"), Component.text("TODO"), Material.ENDER_PEARL, TeleportGUI(framework.getFuxelSagt()))),
        Pair("gamemodes", GameControlItem(Component.text("Gamemodes"), Component.text("TODO"), Material.CHEST, GameModeSelection(framework.getFuxelSagt())))
    )
    private fun getItemKey(key: String): NamespacedKey = NamespacedKey("fuxelsagt.gamecontrol.item", key)

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        if(event.item == null) return
        val container = event.item!!.itemMeta.persistentDataContainer
        val key: NamespacedKey = getItemKey("id")

        if (items.keys.contains(container.get(key, PersistentDataType.STRING))) {
            event.setCancelled(true)
            items[container.get(key, PersistentDataType.STRING)]!!.getGUI().open(event.player)
        }
    }

    fun giveItems(player: Player) {
        items.forEach { (id, item) ->
            player.inventory.addItem(
                ItemBuilder(item.getItem())
                    .addPersistentDataContainer(getItemKey("id"), PersistentDataType.STRING, id)
                    .build()
            )
        }
    }
}