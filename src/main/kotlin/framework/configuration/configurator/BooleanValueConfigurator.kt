package framework.configuration.configurator

import FuxelSagt
import framework.configuration.ConfigurableValueInterface
import framework.configuration.configurator.gui.ConfiguratorGUI
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import util.item.ItemBuilder
import java.util.function.Consumer

class BooleanValueConfigurator(value: ConfigurableValueInterface<Boolean>) : ValueConfigurator<Boolean>(value) {

    override fun openConfigurationInterface(player: Player, fuxelSagt: FuxelSagt, afterThat: Consumer<Player>) {
        object : ConfiguratorGUI<Boolean>(this.value, fuxelSagt, afterThat) {

            override fun draw(player: Player) {
                super.draw(player);
                val item = if (value.getCurrentValue()) {
                    ItemBuilder(Material.LIME_STAINED_GLASS_PANE).setName(Component.text(value.getName() + " ✔").decoration(
                        TextDecoration.ITALIC, TextDecoration.State.FALSE).color(NamedTextColor.GREEN)).build();
                } else {
                    ItemBuilder(Material.RED_STAINED_GLASS_PANE).setName(Component.text(value.getName() + " \uD83D\uDFAD").decoration(
                        TextDecoration.ITALIC, TextDecoration.State.FALSE).color(NamedTextColor.RED)).build();
                }
                this.setItem(12, item)
                this.setItem(14, ItemBuilder(Material.DIAMOND).setName(
                    Component.text("Zurücksetzen (" + (if (value.getDefaultValue()) "✔" else "\uD83D\uDFAD") + ")").decoration(
                        TextDecoration.ITALIC, TextDecoration.State.FALSE).color(NamedTextColor.AQUA)).build());
            }

            override fun onItemClick(event: InventoryClickEvent) {
                super.onItemClick(event)

                val clickedItem: ItemStack? = event.currentItem

                when (clickedItem?.type) {
                    Material.RED_STAINED_GLASS_PANE, Material.LIME_STAINED_GLASS_PANE -> {
                        val item: ItemStack = this.getItem(12)!!;
                        if (item.type == Material.LIME_STAINED_GLASS_PANE) {
                            item.type = Material.RED_STAINED_GLASS_PANE;
                            val itemMeta = item.itemMeta;
                            itemMeta.displayName(Component.text(value.getName() + " \uD83D\uDFAD").decoration(
                                TextDecoration.ITALIC, TextDecoration.State.FALSE).color(NamedTextColor.RED))
                            item.setItemMeta(itemMeta)
                            this.updateConfiguredValue(false);
                        } else if (item.type == Material.RED_STAINED_GLASS_PANE) {
                            item.type = Material.LIME_STAINED_GLASS_PANE;
                            val itemMeta = item.itemMeta;
                            itemMeta.displayName(Component.text(value.getName() + " ✔").decoration(
                                TextDecoration.ITALIC, TextDecoration.State.FALSE).color(NamedTextColor.GREEN))
                            item.setItemMeta(itemMeta)
                            this.updateConfiguredValue(true);
                        }
                    }
                    Material.DIAMOND -> {
                        this.updateConfiguredValue(value.getDefaultValue());
                        val item: ItemStack = this.getItem(12)!!;
                        if (value.getDefaultValue()) {
                            item.type = Material.LIME_STAINED_GLASS_PANE;
                            val itemMeta = item.itemMeta;
                            itemMeta.displayName(Component.text(value.getName() + " ✔").decoration(
                                TextDecoration.ITALIC, TextDecoration.State.FALSE).color(NamedTextColor.GREEN))
                            item.setItemMeta(itemMeta)
                        } else {
                            item.type = Material.RED_STAINED_GLASS_PANE;
                            val itemMeta = item.itemMeta;
                            itemMeta.displayName(Component.text(value.getName() + " \uD83D\uDFAD").decoration(
                                TextDecoration.ITALIC, TextDecoration.State.FALSE).color(NamedTextColor.RED))
                            item.setItemMeta(itemMeta)
                        }
                    }
                    else -> {}
                }
            }

        }.open(player)
    }

}