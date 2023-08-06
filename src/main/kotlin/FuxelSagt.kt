import framework.Framework
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.plugin.java.JavaPlugin
import util.configuration.Config
import java.io.File

class FuxelSagt : JavaPlugin() {

    private var framework = Framework(this);
    private lateinit var config: Config

    override fun onEnable() {
        config = Config.fromFile(File("config.json"))
    }

    override fun onDisable() {

    }

    fun getConfiguration(): Config {
        return this.config;
    }


}