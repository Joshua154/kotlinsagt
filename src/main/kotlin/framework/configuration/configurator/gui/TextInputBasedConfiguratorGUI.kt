package framework.configuration.configurator.gui

import FuxelSagt
import framework.configuration.ConfigurableValueInterface
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.wesjd.anvilgui.AnvilGUI
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import util.item.ItemBuilder
import java.util.function.Consumer
import java.util.function.Function

class TextInputBasedConfiguratorGUI<T : Any>(value: ConfigurableValueInterface<T>, fuxelSagt: FuxelSagt, afterThat: Consumer<Player>, private val stringToT: Function<String, T>, private val ttoString: Function<T, String>) : ConfiguratorGUI<T>(value, fuxelSagt, afterThat) {

    override fun draw(player: Player) {
        super.draw(player);

        this.setItem(12, ItemBuilder(Material.OAK_SIGN)
            .setName(Component.text(value.getName() + " (" + ttoString.apply(this.getConfiguredValue()?: this.value.getCurrentValue()) + ")")
                .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                .color(NamedTextColor.YELLOW))
            .setLore(Component.text("Klicken um einen Wert einzugeben.")
                .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                .color(NamedTextColor.GRAY))
            .build()
        );
        this.setItem(14, ItemBuilder(Material.DIAMOND)
            .setName(Component.text("Zurücksetzen (" + value.getDefaultValue() + ")")
                .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                .color(NamedTextColor.AQUA))
            .build()
        );
    }

    override fun onItemClick(event: InventoryClickEvent) {
        super.onItemClick(event)

        val player: Player = event.whoClicked as Player
        val clickedItem: ItemStack? = event.currentItem

        when (clickedItem?.type) {
            Material.OAK_SIGN -> {
                val builder: AnvilGUI.Builder = AnvilGUI.Builder();
                builder
                    .itemLeft(ItemBuilder(Material.PAPER)
                        .setName(Component.text(value.getName())
                            .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                            .color(NamedTextColor.AQUA))
                        .build())
                    .itemOutput(ItemBuilder(Material.EMERALD)
                        .setName(Component.text("OK!")
                            .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                            .color(NamedTextColor.AQUA))
                        .build())
                    .text(ttoString.apply(value.getCurrentValue()))
                    .onClick{ slot, snapshot ->
                        when (slot) {
                            AnvilGUI.Slot.INPUT_LEFT -> player.sendMessage(Component.text("Bitte gib den gewünschten Wert ein.").color(NamedTextColor.AQUA))
                            AnvilGUI.Slot.INPUT_RIGHT -> {}
                            AnvilGUI.Slot.OUTPUT -> {
                                try {
                                    updateConfiguredValue(stringToT.apply(snapshot.text))
                                    if (getConfiguredValue() != null) value.changeValue(getConfiguredValue()!!, fuxelSagt.getGameModeManager().getCurrentGameMode().get())
                                    afterThat.accept(player);
                                    mutableListOf(AnvilGUI.ResponseAction.close());
                                } catch (exception: Exception) {
                                    player.sendMessage(Component.text("'" + snapshot.text + "' ist kein valider Wert!").color(NamedTextColor.RED));
                                }
                            }
                        }
                        mutableListOf()
                    }
                    .title(value.getShortenedName())
                    .plugin(fuxelSagt)
                    .onClose { }
                    .open(player);
            }
            Material.DIAMOND -> {
                this.updateConfiguredValue(value.getDefaultValue());
                val item: ItemStack = this.getItem(12)!!;
                val itemMeta: ItemMeta = item.itemMeta;
                itemMeta.displayName(Component.text(value.getName() + " (" + ttoString.apply(this.getConfiguredValue()?: this.value.getCurrentValue()) + ")")
                    .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                    .color(NamedTextColor.YELLOW));
                item.setItemMeta(itemMeta);
            }
            else -> {}
        }
    }
}