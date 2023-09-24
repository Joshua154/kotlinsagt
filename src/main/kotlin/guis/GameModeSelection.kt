package guis

import FuxelSagt
import framework.gamemodes.GameMode
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import util.gui.PageGUI
import util.item.ItemBuilder

class GameModeSelection(val fuxelSagt: FuxelSagt) : PageGUI(Component.text("Game Modes").color(NamedTextColor.RED)) {
    private fun getPageGUIKey(key: String): NamespacedKey = NamespacedKey("fuxelsagt.pagegui.item", key)

    private fun genGameModeItem(gameMode: GameMode): ItemStack {
        return ItemBuilder(gameMode.displayItem)
            .setLore(30, Component.text(gameMode.description).color(NamedTextColor.GRAY))
            .setNameCentered(Component.text(gameMode.displayName).color(NamedTextColor.WHITE))
            .addPersistentDataContainer(getPageGUIKey("game_mode_name"), PersistentDataType.STRING, gameMode.name)
            .build()
    }

    override fun getContent(): List<ItemStack> {
        return fuxelSagt.getGameModeManager().getGameModes().stream()
            .sorted { gameMode, gameMode2 -> gameMode.name.compareTo(gameMode2.name) }
            .map { gameMode -> genGameModeItem(gameMode) }.toList()
    }

    override fun onItemClick(player: Player, slot: Int, clickedItem: ItemStack?, clickType: ClickType) {
        if (clickedItem == null) return

        val name: String =
            clickedItem.itemMeta.persistentDataContainer.get(getPageGUIKey("game_mode_name"), PersistentDataType.STRING)
                ?: return println("No name found")
        val gameMode: GameMode =
            fuxelSagt.getGameModeManager().getGameModeByName(name) ?: return println("No game mode found")

        player.closeInventory()
        fuxelSagt.sendPlayerMessage(player, Component.text(gameMode.displayName).color(NamedTextColor.RED).append(Component.text(" is being prepared").color(NamedTextColor.GRAY)))

        fuxelSagt.getGameModeManager().setActiveGameMode(gameMode)

        gameMode.addToPlayers(player)
        gameMode.tpPlayersToGame(player)
    }
}