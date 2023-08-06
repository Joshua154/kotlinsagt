package util.gui

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import util.ItemBuilder
import kotlin.math.ceil

abstract class PageGUI(private val guiName: String = "Name not set") : IGUI {
    private val page: Int = 0
    private val itemsPerPage: Int = 9*5

    fun getPage(): Int {
        return page
    }

    fun getPageCount(): Int{
        return ceil(getContent().size.div(itemsPerPage.coerceAtLeast(1).toFloat())).toInt()
    }

    private fun getItemsFromPage(page: Int): List<ItemStack> {
        return getContent().subList(
            page * itemsPerPage,
            ((page + 1) * itemsPerPage - 1).coerceAtMost(getContent().size)
        )
    }

    abstract fun onItemClick(player: Player?, slot: Int, clickedItem: ItemStack?, clickType: ClickType?)

    override fun getInventory(): Inventory {
        val inventory: Inventory = Bukkit.createInventory(this, itemsPerPage + 9, Component.text(guiName))

        val itemsOnPage: List<ItemStack> = getItemsFromPage(this.page)

        for (i in 0..<itemsPerPage) {
            inventory.setItem(i, itemsOnPage[i])
        }

        for (i in (this.itemsPerPage)..(this.itemsPerPage+9)){
            inventory.setItem(
                i,
                ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                    .setName("")
                    .build()
            )
        }

        if (getPage() != 0) { //if not first page
            inventory.setItem(5*9 + 1, ItemBuilder(Material.ARROW).setName("Zurück").build())//nbt
        }
        if (getPage() + 1 != getPageCount()) { //if not last page
            inventory.setItem(5*9 + 7, ItemBuilder(Material.ARROW).setName("Weiter").build())//nbt
        }

        return inventory
    }

    override fun onClick(player: Player, slot: Int, clickedItem: ItemStack?, clickType: ClickType) {
        TODO("Not yet implemented")
    }

    override fun open(player: Player) {
        player.openInventory(inventory)
    }

    abstract fun getContent(): List<ItemStack>
}