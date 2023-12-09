package framework.configuration.configurator

import FuxelSagt
import framework.configuration.ConfigurableValueInterface
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.util.function.Consumer

abstract class ValueConfigurator<T : Any> {

    protected val value: ConfigurableValueInterface<T>;

    companion object {
        fun <T: Any> make(value: ConfigurableValueInterface<T>): ValueConfigurator<out Any>? {
            return when (value.getType()) {
                ConfigurableValueInterface.Type.INTEGER_MAX_64 -> Integer64ValueConfigurator(value as ConfigurableValueInterface<Int>);
                ConfigurableValueInterface.Type.FLOAT -> FloatValueConfigurator(value as ConfigurableValueInterface<Double>);
                ConfigurableValueInterface.Type.STRING -> StringValueConfigurator(value as ConfigurableValueInterface<String>);
                ConfigurableValueInterface.Type.LIMITLESS_INTEGER -> LimitlessIntegerValueConfigurator(value as ConfigurableValueInterface<Int>)
                ConfigurableValueInterface.Type.BOOLEAN -> BooleanValueConfigurator(value as ConfigurableValueInterface<Boolean>)
                else -> null
            }
        }
    }

    constructor(value: ConfigurableValueInterface<T>) {
        this.value = value;
    }

    open fun getDisplayable(fuxelSagt: FuxelSagt): ItemStack {
        val itemStack = ItemStack(this.value.getMaterial());
        val itemMeta = itemStack.itemMeta;
        itemMeta.displayName(Component.text(this.value.getName()).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE).color(NamedTextColor.AQUA))
        itemMeta.lore(listOf(Component.text("Momentaner Wert: " + this.value.getCurrentValue()).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE).color(NamedTextColor.GRAY)));
        itemMeta.persistentDataContainer.set(NamespacedKey.fromString("configurable_value_field", fuxelSagt)!!, PersistentDataType.STRING, this.value.getKProperty().name)
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    abstract fun openConfigurationInterface(player: Player, fuxelSagt: FuxelSagt, afterThat: Consumer<Player>)
}