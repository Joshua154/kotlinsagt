import framework.Framework
import listener.JoinListener
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.plugin.java.JavaPlugin
import util.configuration.Config
import util.gui.GUIEH
import java.io.File

class FuxelSagt : JavaPlugin() {

    private var framework = Framework(this);
    private lateinit var config: Config

    override fun onEnable() {
        config = Config.fromFile(File("config.json"))

        val pluginManager = server.pluginManager
        pluginManager.registerEvents(JoinListener(this), this)
        pluginManager.registerEvents(GUIEH(), this) //GUI Event Handler
    }

    override fun onDisable() {

    }

    fun getConfiguration(): Config {
        return this.config;
    }


}