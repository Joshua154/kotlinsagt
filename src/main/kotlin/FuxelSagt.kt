import commands.TestCommand
import framework.Framework
import framework.gamemodes.GameMode
import framework.gamemodes.TNTRun
import listener.JoinListener
import org.bukkit.plugin.java.JavaPlugin
import util.configuration.Config
import util.gui.GUIEH
import java.io.File

class FuxelSagt : JavaPlugin() {

    private var framework = Framework(this);
    private lateinit var config: Config

    override fun onEnable() {
        config = Config.fromFile(File("config.json"))

        registerListeners()
        registerCommands()

        val mode1 = TNTRun(framework, "tntRun")
        mode1.start()
    }

    override fun onDisable() {

    }

    private fun registerCommands() {
        getCommand("testcommand")?.setExecutor(TestCommand(server))
    }

    private fun registerListeners() {
        val pluginManager = server.pluginManager
        pluginManager.registerEvents(JoinListener(this), this)
        pluginManager.registerEvents(GUIEH(), this) // GUI Event Handler
    }

    fun getConfiguration(): Config {
        return this.config;
    }


}