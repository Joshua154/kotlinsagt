package guis

import FuxelSagt
import framework.configuration.ConfigurableValueInterface
import net.kyori.adventure.text.Component
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import util.gui.PageGUI
import java.util.*
import kotlin.collections.ArrayList

class ConfigurationGUI(private val fuxelSagt: FuxelSagt) : PageGUI(Component.text("Einstellungen")) {

    override fun getContent(): List<ItemStack> {
        //TODO: Make Prettier...
        if (fuxelSagt.getGameModeManager().getCurrentGameMode().isEmpty) return ArrayList();
        return fuxelSagt.getGameModeManager().getCurrentGameMode().get().getConfigurableValues().map {
            it.getConfigurator().getDisplayable(fuxelSagt)
        }
    }

    override fun onItemClick(event: InventoryClickEvent) {
        val player: Player = event.whoClicked as Player
        val clickedItem: ItemStack? = event.currentItem

        if (clickedItem == null || clickedItem.itemMeta == null) return
        val data: String? = clickedItem.itemMeta.persistentDataContainer.get(NamespacedKey.fromString("configurable_value_field", fuxelSagt)!!, PersistentDataType.STRING);
        val value: Optional<ConfigurableValueInterface<*>> = fuxelSagt.getGameModeManager().getCurrentGameMode().get().getConfigurableValues().stream().filter { value -> value.getKProperty().name.equals(data) }.findAny();

        if (value.isEmpty) return;
        value.get().getConfigurator().openConfigurationInterface(player, fuxelSagt) { p -> ConfigurationGUI(fuxelSagt).open(p); };
    }

}