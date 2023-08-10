package util.gui

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import util.item.ItemBuilder
import kotlin.math.ceil

abstract class PageGUI(private val guiTitle: Component = Component.text("Name not set")) : IGUI {
    private var page: Int = 0
    private val itemsPerPage: Int = 9 * 5
    private fun getPageGUIKey(key: String): NamespacedKey = NamespacedKey("fuxelsagt.pageGUI.item", key)

    private fun getPage(): Int {
        return page
    }

    private fun getPageCount(): Int {
        return ceil(getContent().size.div(itemsPerPage.coerceAtLeast(1).toFloat())).toInt()
    }

    private fun switchPage(direction: Boolean){
        //direction: false -> back, true -> further
        if(getPageCount() == 1) return

        if(direction) page += 1
        else page -= 1

        if(page < 0) page = 0
        if(page > getPageCount()) page = getPageCount() - 1

        onPageSwitch()
    }

    private fun getItemsFromPage(page: Int): List<ItemStack> {
        return getContent().subList(
            page * itemsPerPage,
            ((page + 1) * itemsPerPage - 1).coerceAtMost(getContent().size)
        )
    }

    override fun getInventory(): Inventory {
        val inventory: Inventory = Bukkit.createInventory(this, itemsPerPage + 9, guiTitle)

        val itemsOnPage: List<ItemStack> = getItemsFromPage(this.page)

        for (i in 0..<itemsPerPage) {
            inventory.setItem(i, itemsOnPage[i])
        }

        for (i in (this.itemsPerPage)..(this.itemsPerPage + 9)) {
            inventory.setItem(
                i,
                ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                    .setName("")
                    .build()
            )
        }

        if (getPage() != 0) { //if not first page
            inventory.setItem(
                5 * 9 + 1, ItemBuilder(Material.PLAYER_HEAD)
                    .setName("Zurück")
                    .setSkullOwnerWithURL("5f133e91919db0acefdc272d67fd87b4be88dc44a958958824474e21e06d53e6")
                    .addPersistentDataContainer(getPageGUIKey("type"), PersistentDataType.STRING, "back")
                    .build()
            )
        }
        if (getPage() + 1 != getPageCount()) { //if not last page
            inventory.setItem(
                5 * 9 + 7, ItemBuilder(Material.PLAYER_HEAD)
                .setName("Weiter")
                .setSkullOwnerWithURL("e3fc52264d8ad9e654f415bef01a23947edbccccf649373289bea4d149541f70")
                .addPersistentDataContainer(getPageGUIKey("type"), PersistentDataType.STRING, "further")
                .build()
            )
        }

        return inventory
    }

    override fun onClick(player: Player, slot: Int, clickedItem: ItemStack?, clickType: ClickType) {
        if(clickedItem == null) return
        val itemMeta = clickedItem.itemMeta
        val container = itemMeta.persistentDataContainer
        if(container.has(getPageGUIKey("type"))){
            when (container.get(getPageGUIKey("type"), PersistentDataType.STRING)){
                "back" -> return switchPage(false)
                "further" -> return switchPage(true)
            }
        }

        onItemClick(player, slot, clickedItem, clickType)
    }

    override fun open(player: Player) {
        player.openInventory(inventory)
    }

    abstract fun getContent(): List<ItemStack>
    abstract fun onItemClick(player: Player, slot: Int, clickedItem: ItemStack?, clickType: ClickType)
    abstract fun onPageSwitch()
}