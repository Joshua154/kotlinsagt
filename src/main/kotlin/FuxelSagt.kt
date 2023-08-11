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

    private lateinit var framework: Framework
    private lateinit var config: Config
    lateinit var testMode: GameMode

    override fun onEnable() {
        config = Config.fromFile(File("config.json"))
        this.framework = Framework(this)

        registerListeners()
        registerCommands()

        this.testMode = TNTRun(framework, "tntRun")
        this.testMode.start()
    }

    override fun onDisable() {

    }

    private fun registerCommands() {
        getCommand("testcommand")?.setExecutor(TestCommand(this))
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