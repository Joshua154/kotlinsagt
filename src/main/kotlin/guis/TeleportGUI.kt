package guis

import FuxelSagt
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import util.gui.PageGUI
import util.item.ItemBuilder
import util.item.UUIDDataType
import java.util.ArrayList
import java.util.UUID

class TeleportGUI(val fuxelSagt: FuxelSagt) : PageGUI(Component.text("Teleporter")) {
    private fun getPageGUIKey(key: String): NamespacedKey = NamespacedKey("fuxelsagt.pagegui.item", key)

    private fun genClickableItem(player: Player): ItemStack{
        return ItemBuilder(Material.PLAYER_HEAD)
            .setSkullOwner(player)
            .addPersistentDataContainer(getPageGUIKey("player_name"), UUIDDataType(), player.uniqueId)
            .setName(Component.text(player.name))
            .build()
    }

    override fun getContent(): List<ItemStack> {
        return fuxelSagt.server.onlinePlayers.stream()
            .sorted { player, player2 -> player.name.compareTo(player2.name) }
            .map { player -> genClickableItem(player) }.toList()
//        return Material.entries.filter { material: Material -> material.isItem }.map { material: Material -> ItemStack(material) }
    }

    override fun onItemClick(player: Player, slot: Int, clickedItem: ItemStack?, clickType: ClickType) {
        if(clickedItem == null) return
        val container = clickedItem.itemMeta.persistentDataContainer
        val key: NamespacedKey = getPageGUIKey("player_name")
        if(container.has(key) && container.get(key, UUIDDataType()) != null){
            val teleportToPlayer = fuxelSagt.server.getPlayer(container.get(key, UUIDDataType())!!) ?: return

            player.teleport(teleportToPlayer.location)
        }
    }
}