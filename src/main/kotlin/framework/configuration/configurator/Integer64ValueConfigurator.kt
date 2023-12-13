package framework.configuration.configurator

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
import framework.configuration.configurator.gui.ConfiguratorGUI;
import org.bukkit.event.inventory.InventoryClickEvent

class Integer64ValueConfigurator(value: ConfigurableValueInterface<Int>) : ValueConfigurator<Int>(value) {

    override fun getDisplayable(fuxelSagt: FuxelSagt): ItemStack {
        val itemStack: ItemStack = super.getDisplayable(fuxelSagt);
        itemStack.amount = this.value.getCurrentValue();
        return itemStack;
    }

    override fun openConfigurationInterface(player: Player, fuxelSagt: FuxelSagt, afterThat: Consumer<Player>) {
        object : ConfiguratorGUI<Int>(this.value, fuxelSagt, afterThat) {

            override fun draw(player: Player) {
                super.draw(player);
                this.setItem(12, ItemBuilder(Material.OAK_SIGN, value.getCurrentValue()).setName(Component.text(value.getName()).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE).color(NamedTextColor.YELLOW)).setLore(Component.text("Linksklick zum Erhöhen, Rechtsklick zum Verringern").decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE).color(NamedTextColor.GRAY)).build());
                this.setItem(14, ItemBuilder(Material.DIAMOND).setName(Component.text("Zurücksetzen (" + value.getDefaultValue() + ")").decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE).color(NamedTextColor.AQUA)).build());
            }

            override fun onItemClick(event: InventoryClickEvent) {
                super.onItemClick(event);

                val p: Player = event.whoClicked as Player
                val clickedItem: ItemStack? = event.currentItem
                val clickType: ClickType = event.click

                when (clickedItem?.type) {
                    Material.OAK_SIGN -> {
                        if (clickType.isLeftClick) {
                            if (clickedItem.amount == 64) {
                                p.sendMessage(Component.text("Das Maximum für diese Einstellungsmöglichkeit ist 64.").decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE).color(NamedTextColor.RED))
                                return;
                            }
                            this.getItem(12)?.amount = clickedItem.amount + 1;
                            this.updateConfiguredValue(clickedItem.amount + 1);
                        } else if (clickType.isRightClick) {
                            if (clickedItem.amount == 1) {
                                p.sendMessage(Component.text("Das Minimum für diese Einstellungsmöglichkeit ist 1.").decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE).color(NamedTextColor.RED))
                                return;
                            }
                            this.getItem(12)?.amount = clickedItem.amount - 1;
                            this.updateConfiguredValue(clickedItem.amount - 1);
                        };
                    }
                    Material.DIAMOND -> {
                        this.updateConfiguredValue(value.getDefaultValue());
                        this.getItem(12)?.amount = this.getConfiguredValue()?: value.getCurrentValue();
                    }
                    else -> {}
                }
            }

        }.open(player)
    }
}