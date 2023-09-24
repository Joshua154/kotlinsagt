package util.item

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
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

    fun setNameCentered(name: TextComponent): ItemBuilder {
        val textLength = name.content().length
        val maxLength = this.itemStack.itemMeta.lore()
            ?.maxOfOrNull { line -> PlainTextComponentSerializer.plainText().serialize(line).length } ?: 0

        val n = (maxLength - textLength) / 2
        setName(Component.text(" ".repeat((n + n * 1 / 3).coerceAtLeast(0))).append(name))
        return this
    }

    fun setNameCentered(name: String?): ItemBuilder {
        this.setNameCentered(Component.text(name!!))
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
        setLore(lore.map { line -> Component.text(line!!) })
        return this
    }

    fun setLore(lore: List<Component>?): ItemBuilder {
        val itemMeta = itemStack.itemMeta
        itemMeta.lore(lore)
        itemStack.setItemMeta(itemMeta)
        return this
    }

    fun setLore(maxLength: Int, lore: TextComponent): ItemBuilder {
        val lines = splitString(lore.content(), maxLength)
        setLore(lines.map { line -> lore.content(line) })
        return this
    }

    fun setLore(maxLength: Int, lore: String): ItemBuilder {
        setLore(splitString(lore, maxLength).map { line -> Component.text(line) })
        return this
    }

    private fun splitString(input: String, maxLength: Int): List<String> {
        val words = input.split(" ")
        val result = mutableListOf<String>()
        var currentString = ""

        for (word in words) {
            if (currentString.isEmpty()) {
                currentString = word
            } else {
                val potentialString = "$currentString $word"
                if (potentialString.length <= maxLength) {
                    currentString = potentialString
                } else {
                    result.add(currentString)
                    currentString = word
                }
            }
        }
        if (currentString.isNotEmpty()) {
            result.add(currentString)
        }
        return result
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

//    fun addPersistentDataContainer(key: String, type: PersistentDataType<String, String>, value: String): ItemBuilder {
//        return addPersistentDataContainer(getNameSpaceKey(key), type, value)
//    }

    fun addPersistentDataContainer(
        key: NamespacedKey,
        type: PersistentDataType<String, String>,
        value: String
    ): ItemBuilder {
        val itemMeta = itemStack.itemMeta
        itemMeta.persistentDataContainer.set(key, type, value)
        itemStack.setItemMeta(itemMeta)
        return this
    }

    fun addPersistentDataContainer(key: NamespacedKey, type: UUIDDataType, value: UUID): ItemBuilder {
        val itemMeta = itemStack.itemMeta
        itemMeta.persistentDataContainer.set(key, type, value)
        itemStack.setItemMeta(itemMeta)
        return this
    }

//    fun removePersistentDataContainer(key: String): ItemBuilder {
//        return removePersistentDataContainer(getNameSpaceKey(key))
//    }

    fun removePersistentDataContainer(key: NamespacedKey): ItemBuilder {
        val itemMeta = itemStack.itemMeta
        itemMeta.persistentDataContainer.remove(key)
        itemStack.setItemMeta(itemMeta)
        return this
    }

    fun hasPersistentDataContainer(key: NamespacedKey): Boolean {
        return itemStack.itemMeta.persistentDataContainer.has(key)
    }

    fun setSkullOwner(name: String): ItemBuilder {
        if (itemStack.itemMeta !is SkullMeta) return this
        val itemMeta = itemStack.itemMeta as SkullMeta
        itemMeta.setOwningPlayer(Bukkit.getOfflinePlayer(name))
        itemStack.setItemMeta(itemMeta)
        return this
    }

    fun setSkullOwner(player: Player): ItemBuilder {
        if (itemStack.itemMeta !is SkullMeta) return this
        val itemMeta = itemStack.itemMeta as SkullMeta
        itemMeta.setOwningPlayer(player)
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

    private fun getNameSpaceKey(key: String): NamespacedKey {
        return NamespacedKey("fuxelsagt.item", key)
    }

    fun build(): ItemStack {
        return itemStack
    }
}