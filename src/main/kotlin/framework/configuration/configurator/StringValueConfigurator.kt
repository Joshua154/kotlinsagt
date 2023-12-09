package framework.configuration.configurator

import FuxelSagt
import framework.configuration.ConfigurableValueInterface
import framework.configuration.configurator.gui.TextInputBasedConfiguratorGUI
import org.bukkit.entity.Player
import java.util.function.Consumer

class StringValueConfigurator(value: ConfigurableValueInterface<String>) : ValueConfigurator<String>(value) {

    override fun openConfigurationInterface(player: Player, fuxelSagt: FuxelSagt, afterThat: Consumer<Player>) {
        TextInputBasedConfiguratorGUI(value, fuxelSagt, afterThat, { it }, { it }).open(player);
    }

}