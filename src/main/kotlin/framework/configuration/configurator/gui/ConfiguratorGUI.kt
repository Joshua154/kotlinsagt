package framework.configuration.configurator.gui

import FuxelSagt
import framework.configuration.ConfigurableValueInterface
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack
import util.gui.StaticGUI
import util.item.ItemBuilder
import java.util.function.Consumer

abstract class ConfiguratorGUI<T : Any>(val value: ConfigurableValueInterface<T>, val fuxelSagt: FuxelSagt, val afterThat: Consumer<Player>) : StaticGUI(Component.text(value.getShortenedName()), 3 * 9) {

    private var configuredValue: T? = null;

    override fun draw(player: Player) {
        this.setItem(18, ItemBuilder(Material.EMERALD).setName(Component.text("Anwenden!").decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE).color(NamedTextColor.GREEN)).build());
        this.setItem(26, ItemBuilder(Material.BARRIER).setName(Component.text("Abbrechen!").decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE).color(NamedTextColor.RED)).build());
    }

    protected fun updateConfiguredValue(value: T) {
        this.configuredValue = value;
    }

    override fun onItemClick(player: Player, slot: Int, clickedItem: ItemStack?, clickType: ClickType) {
        when (clickedItem?.type) {
            Material.BARRIER -> {
                afterThat.accept(player);
                return;
            }
            Material.EMERALD -> {
                if (configuredValue != null) value.changeValue(configuredValue!!, fuxelSagt.getGameModeManager().getCurrentGameMode().get())
                afterThat.accept(player);
                return;
            }
            else -> {}
        }
    }

    fun getConfiguredValue(): T? {
        return this.configuredValue;
    }
}