package framework.gamemode

enum class GameModeState {
    PREPARING,  // Generating and preparing
    READY,  // Ready to be started
    RUNNING,  // Game is running
    PAUSED,
    FINISHED,  // Finished, but not alle mechanisms are stopped
    STOPPED  // Stopped, ready to be destroyed
}