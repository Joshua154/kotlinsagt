package util.gui

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import java.util.function.Function

abstract class StaticGUI(private val title: Component = Component.text("Name not set"), private val size: Int) : IGUI {

    private lateinit var player: Player;

    private val items: MutableMap<Int, ItemStack> = HashMap();

    abstract fun draw(player: Player);

    protected fun setItem(slot: Int, itemStack: ItemStack) {
        this.items[slot] = itemStack;
    }

    protected fun getItem(slot: Int): ItemStack? {
        return this.items[slot];
    }

    override fun update(player: Player, inventory: Inventory) {
        this.populateInventory(inventory);
    }

    override fun onClick(event: InventoryClickEvent) {
        event.currentItem ?: return

        val player: Player = event.whoClicked as Player
        val inventory: Inventory = event.inventory

        onItemClick(event);
        update(player, inventory)
    }

    override fun open(player: Player) {
        this.draw(player);
        val inventory = this.getInventory();
        player.openInventory(inventory);
        this.player = player;
    }

    abstract fun onItemClick(event: InventoryClickEvent);

    override fun populateInventory(inventory: Inventory) {
        this.items.forEach { (slot, item) -> inventory.setItem(slot, item)};
    }

    override fun getInventory(): Inventory {
        val inventory: Inventory = Bukkit.createInventory(this, size, title);
        this.populateInventory(inventory);
        return inventory;
    }

}