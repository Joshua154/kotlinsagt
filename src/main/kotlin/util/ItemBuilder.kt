package util

import FuxelSagt
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

class ItemBuilder() {
    private lateinit var itemStack: ItemStack

    constructor(material: Material?) : this() {
        itemStack = ItemStack(material!!)
    }

    constructor(material: Material?, amount: Int) : this() {
        itemStack = ItemStack(material!!, amount)
    }

    constructor(itemStack: ItemStack) : this() {
        this.itemStack = itemStack
    }

    fun setName(name: Component?): ItemBuilder {
        val itemMeta = itemStack.itemMeta
        itemMeta.displayName(name)
        itemStack.setItemMeta(itemMeta)
        return this
    }

    fun setName(name: String?): ItemBuilder {
        this.setName(Component.text(name!!))
        return this
    }

    fun addUnsafeEnchantment(enchantment: Enchantment?, level: Int): ItemBuilder {
        itemStack.addUnsafeEnchantment(enchantment!!, level)
        return this
    }

    fun addUnsafeEnchantments(enchantments: Map<Enchantment?, Int?>?): ItemBuilder {
        itemStack.addUnsafeEnchantments(enchantments!!)
        return this
    }

    fun addEnchantment(enchantment: Enchantment?, level: Int): ItemBuilder {
        itemStack.addEnchantment(enchantment!!, level)
        return this
    }

    fun addEnchantments(enchantments: Map<Enchantment?, Int?>?): ItemBuilder {
        itemStack.addEnchantments(enchantments!!)
        return this
    }

    fun removeEnchantment(enchantment: Enchantment?): ItemBuilder {
        itemStack.removeEnchantment(enchantment!!)
        return this
    }

    fun setUnbreakable(): ItemBuilder {
        val itemMeta = itemStack.itemMeta
        itemMeta.isUnbreakable = true
        itemStack.setItemMeta(itemMeta)
        return this
    }

    fun setBreakable(): ItemBuilder {
        val itemMeta = itemStack.itemMeta
        itemMeta.isUnbreakable = false
        itemStack.setItemMeta(itemMeta)
        return this
    }

    fun setLore(vararg lore: Component?): ItemBuilder {
        val itemMeta = itemStack.itemMeta
        itemMeta.lore(listOf(*lore))
        itemStack.setItemMeta(itemMeta)
        return this
    }

    fun setLore(vararg lore: String?): ItemBuilder {
        val loreList = ArrayList<Component>()
        for (line in lore) {
            loreList.add(Component.text(line!!))
        }
        this.setLore(loreList)
        return this
    }

    fun setLore(lore: List<Component>?): ItemBuilder {
        val itemMeta = itemStack.itemMeta
        itemMeta.lore(lore)
        itemStack.setItemMeta(itemMeta)
        return this
    }

    fun setCustomModelData(data: Int): ItemBuilder {
        val itemMeta = itemStack.itemMeta
        itemMeta.setCustomModelData(data)
        itemStack.setItemMeta(itemMeta)
        return this
    }

    fun setItemFlag(flag: ItemFlag?, value: Boolean): ItemBuilder {
        val itemMeta = itemStack.itemMeta
        itemMeta.addItemFlags(flag!!)
        itemStack.setItemMeta(itemMeta)
        return this
    }

    fun addPersistentDataContainer(key: String, type: PersistentDataType<Any, Any>, value: Any): ItemBuilder {
        return addPersistentDataContainer(getNameSpaceKey(key), type, value)
    }

    fun removePersistentDataContainer(key: String): ItemBuilder {
        return removePersistentDataContainer(getNameSpaceKey(key))
    }

    fun addPersistentDataContainer(key: NamespacedKey, type: PersistentDataType<Any, Any>, value: Any): ItemBuilder{
        val itemMeta = itemStack.itemMeta
        itemMeta.persistentDataContainer.set(key, type, value)
        return this
    }

    fun removePersistentDataContainer(key: NamespacedKey): ItemBuilder{
        val itemMeta = itemStack.itemMeta
        itemMeta.persistentDataContainer.remove(key)
        return this
    }

    private fun getNameSpaceKey(key: String): NamespacedKey{
        return NamespacedKey("fuxelsagt.namespacedKey", key)
    }

    fun build(): ItemStack {
        return itemStack
    }
}