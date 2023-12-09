package framework.configuration.configurator

import FuxelSagt
import framework.configuration.ConfigurableValueInterface
import framework.configuration.configurator.gui.TextInputBasedConfiguratorGUI
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.function.Consumer

class LimitlessIntegerValueConfigurator(value: ConfigurableValueInterface<Int>) : ValueConfigurator<Int>(value) {

    override fun getDisplayable(fuxelSagt: FuxelSagt): ItemStack {
        val itemStack: ItemStack = super.getDisplayable(fuxelSagt);
        itemStack.amount = if (this.value.getCurrentValue() > 64 || this.value.getCurrentValue() < 1) 1 else this.value.getCurrentValue();
        return itemStack;
    }

    override fun openConfigurationInterface(player: Player, fuxelSagt: FuxelSagt, afterThat: Consumer<Player>) {
        TextInputBasedConfiguratorGUI(value, fuxelSagt, afterThat, { Integer.valueOf(it) }, { it.toString() }).open(player);
    }

}