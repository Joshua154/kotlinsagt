package framework.manager;

import framework.Framework;

class GameModeManager(private val framework: Framework) {

    // private var gameModes = ArrayList<GameMode>()
    // private var currentGameMode: Optional<GameMode> = Optional.empty()

    init {
        setupGameModes()
    }

    private fun setupGameModes() {
        // alle Gamemodes initialisieren
    }

//    fun getCurrentGameMode(): Optional<GameMode> {
//        return currentGameMode
//    }

//     fun loadGameMode(gameMode: GameMode) {
//         if (currentGameMode.isPresent) {
//             unloadGameMode(currentGameMode.get())
//         }
//         var gameMode = GameMode()
//         currentGameMode = Optional.of(gameMode)
//     }

//     fun unloadGameMode(gameMode: GameMode) {
//         gameMode.unload()
//         currentGameMode = Optional.empty()
//     }

}
