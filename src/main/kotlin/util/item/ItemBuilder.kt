package util.item

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.persistence.PersistentDataType
import java.lang.reflect.Field
import java.util.*


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

    fun addPersistentDataContainer(key: NamespacedKey, type: PersistentDataType<Any, Any>, value: Any): ItemBuilder { //TODO: Any doesn't work
        val itemMeta = itemStack.itemMeta
        itemMeta.persistentDataContainer.set(key, type, value)
        return this
    }

    fun removePersistentDataContainer(key: NamespacedKey): ItemBuilder {
        val itemMeta = itemStack.itemMeta
        itemMeta.persistentDataContainer.remove(key)
        return this
    }

    fun setSkullOwner(name: String): ItemBuilder {
        if (itemStack.itemMeta !is SkullMeta) return this
        val itemMeta = itemStack.itemMeta as SkullMeta
        itemMeta.setOwningPlayer(Bukkit.getOfflinePlayer(name)) //TODO
        itemStack.setItemMeta(itemMeta)
        return this
    }

    fun setSkullOwnerWithURL(url: String): ItemBuilder {
        if (itemStack.itemMeta !is SkullMeta) return this
        val meta: SkullMeta = itemStack.itemMeta as SkullMeta
        val finalUrl = "https://textures.minecraft.net/texture/$url"
        val gameProfile = GameProfile(UUID.randomUUID(), null)
        val data = Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", finalUrl).toByteArray())
        gameProfile.getProperties().put("textures", Property("textures", String(data)))
        try {
            val field: Field = meta.javaClass.getDeclaredField("profile")
            field.setAccessible(true)
            field[meta] = gameProfile
            field.setAccessible(false)
        } catch (ignored: Exception) {
        }
        return this
    }

    private fun getNameSpaceKey(key: String): NamespacedKey{
        return NamespacedKey("fuxelsagt.item", key)
    }

    fun build(): ItemStack {
        return itemStack
    }
}