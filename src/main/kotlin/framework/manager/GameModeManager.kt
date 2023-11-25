package framework.manager;

import framework.Framework;
import framework.gamemode.GameMode
import java.util.*

class GameModeManager(private val framework: Framework) {

    private var gameModes = ArrayList<GameMode>()
    private var currentGameMode: Optional<GameMode> = Optional.empty()

    init {
        setupGameModes()
    }

    private fun setupGameModes() {
        // alle Gamemodes initialisieren
    }

    fun addGameMode(gameMode: GameMode) {
        gameModes.add(gameMode)
    }

    fun removeGameMode(gameMode: GameMode) {
        gameModes.remove(gameMode)
    }

    fun getGameModes(): ArrayList<GameMode> {
        return gameModes
    }

    fun getGameModeByName(name: String): GameMode? {
        return gameModes.find { mode -> mode.name == name }
    }

    fun getCurrentGameMode(): Optional<GameMode> {
        return currentGameMode
    }

    fun setActiveGameMode(gameMode: GameMode) {
        if (currentGameMode.isPresent) {
            unloadGameMode(currentGameMode.get())
        }
        currentGameMode = Optional.of(gameMode)

        currentGameMode.get().prepare();
    }

    fun unloadGameMode(gameMode: GameMode) {
        gameMode.unload()
        currentGameMode = Optional.empty()
    }
}
