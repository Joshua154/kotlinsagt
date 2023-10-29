import commands.StartCommand
import commands.TestCommand
import framework.Framework
import framework.gamemodes.Blockparty
import framework.gamemodes.GameMode
import framework.gamemodes.Sammelwahn
import framework.gamemodes.TNTRun
import framework.manager.Colors
import framework.manager.GameModeManager
import framework.manager.gameControl.GameControlManager
import listener.JoinListener
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import util.gui.GUIEH

class FuxelSagt : JavaPlugin() {

    private lateinit var framework: Framework

    //    private lateinit var config: Config
    private lateinit var gameModeManager: GameModeManager
    private lateinit var gameControlManager: GameControlManager
    lateinit var testMode: GameMode

    override fun onEnable() {
//        config = Config.fromFile(File("config.json"))
        this.framework = Framework(this)

        registerManager()
        registerListeners()
        registerCommands()
        registerGameModes()
    }

    override fun onDisable() {
        // Unload the current gamemode to ensure that a new map is going to be generated when te server starts back up.
        this.gameModeManager.getCurrentGameMode().ifPresent { gamemode -> this.gameModeManager.unloadGameMode(gamemode); };
    }

    private fun registerManager() {
        this.gameModeManager = GameModeManager(this.framework)
        this.gameControlManager = GameControlManager(this.framework)
    }

    private fun registerCommands() {
        getCommand("testcommand")?.setExecutor(TestCommand(this))
        getCommand("teststart")?.setExecutor(StartCommand(this))
    }

    private fun registerListeners() {
        val pluginManager = server.pluginManager
        pluginManager.registerEvents(JoinListener(this), this)
        pluginManager.registerEvents(GUIEH(), this) // GUI Event Handler
        pluginManager.registerEvents(this.gameControlManager, this) // GUI Event Handler
    }

    private fun registerGameModes() {
        gameModeManager.addGameMode(TNTRun(framework))
        gameModeManager.addGameMode(Sammelwahn(framework))
        gameModeManager.addGameMode(Blockparty(framework))
    }

//    fun getConfiguration(): Config {
//        return this.config;
//    }

    fun getGameModeManager(): GameModeManager {
        return this.gameModeManager
    }

    fun getGameControlManager(): GameControlManager {
        return this.gameControlManager
    }

    fun sendPlayerMessage(player: Player, message: Component) {
        player.sendMessage(
            MiniMessage.miniMessage()
                .deserialize("<gray>[" + Colors.FUXELSAGT.prefix + "FuxelSagt" + Colors.FUXELSAGT.suffix + "]: <gray>")
                .append(message)
        )
    }
}