package util.gui

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import util.item.ItemBuilder
import kotlin.math.ceil

abstract class PageGUI(private val guiTitle: Component = Component.text("Name not set")) : IGUI {
    private var page: Int = 0
    private val itemsPerPage: Int = 9 * 5
    private lateinit var player: Player

    private fun getPageGUIKey(key: String): NamespacedKey = NamespacedKey("fuxelsagt.pagegui.item", key)

    private fun getPage(): Int {
        return page
    }

    private fun getPageCount(): Int {
        return ceil(getContent().size.div(itemsPerPage.coerceAtLeast(1).toFloat())).toInt()
    }

    private fun switchPage(direction: Direction) {
        if (getPageCount() == 1) return

        if (direction == Direction.FORWARD) page += 1
        else if (direction == Direction.BACKWARD) page -= 1

        if (page < 0) page = 0
        if (page > getPageCount()) page = getPageCount() - 1

        onPageSwitch()
        refresh()
    }

    private fun getItemsFromPage(page: Int): List<ItemStack> {
        return getContent().subList(
            page * itemsPerPage,
            ((page + 1) * itemsPerPage).coerceAtMost(getContent().size)
        )
    }

    override fun populateInventory(inventory: Inventory) {
        val itemsOnPage: List<ItemStack> = getItemsFromPage(this.page)

        for (i in 0..<itemsPerPage + 1) {
            if (i < itemsOnPage.size) {
                inventory.setItem(i, itemsOnPage[i])
            }
        }

        for (i in (this.itemsPerPage)..(this.itemsPerPage + 8)) {
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
    }

    override fun getInventory(): Inventory {
        val inventory: Inventory = Bukkit.createInventory(this, itemsPerPage + 9, guiTitle)
        this.populateInventory(inventory);
        return inventory
    }

    override fun onClick(event: InventoryClickEvent) {
        val player: Player = event.whoClicked as Player
        val clickedItem: ItemStack = event.currentItem ?: return

        val itemMeta = clickedItem.itemMeta
        val container = itemMeta.persistentDataContainer
        if (container.has(getPageGUIKey("type"))) {
            when (container.get(getPageGUIKey("type"), PersistentDataType.STRING)) {
                "back" -> return switchPage(Direction.BACKWARD)
                "further" -> return switchPage(Direction.FORWARD)
            }
        }

        onItemClick(event)
        this.update(player, inventory);
    }

    override fun open(player: Player) {
        val inventory: Inventory = this.getInventory();
        player.openInventory(inventory);
        this.player = player;
    }

    override fun update(player: Player, inventory: Inventory) {
        this.populateInventory(inventory);
    }

    fun refresh() {
        player.openInventory(inventory)
    }

    abstract fun getContent(): List<ItemStack>

    abstract fun onItemClick(event: InventoryClickEvent)

    open fun onPageSwitch() {
        return
    }

    public enum class Direction {
        BACKWARD,
        FORWARD
    }
}