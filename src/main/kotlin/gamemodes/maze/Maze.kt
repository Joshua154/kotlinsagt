package gamemodes.maze

import framework.Framework
import framework.configuration.Configurable
import framework.gamemode.GameMode
import framework.gamemode.GameModeState
import org.bukkit.Material
import org.bukkit.WorldCreator
import org.bukkit.inventory.ItemStack


class Maze(private val framework: Framework) : GameMode(framework) {
    override val name: String = "maze"
    override val displayName: String = "Labirinth"
    override val displayItem: ItemStack = ItemStack(Material.DIRT_PATH)
    override val description: String = "placeholder"
    override val minPlayers: Int = 2
    override val maxPlayers: Int = 100
    override val worldName: String = "maze"
    override val hasPreBuiltWorld: Boolean = false
    override val roundTime: Int = 60 * 5
    override val hasPoints: Boolean = false
    override val hasTeams: Boolean = false
    override val isFinale: Boolean = false

    @Configurable(Material.SMOOTH_QUARTZ, "Size of the maze")
    var size: Int = 35

    override fun getWorldCreator(): WorldCreator {
        return super.getWorldCreator()
            .generatorSettings(
                "{\"biome\": \"minecraft:the_void\"," +
                        "\"layers\": [{\"block\": \"minecraft:air\",\"height\": 1}]}"
            )
    }

    override fun prepare() {
        load()
        MazeGenerator(size).buildMaze(
            sizeOfBlocks = 3,
            height = 6,
            center = framework.getFuxelSagt().server.getWorld("maze")!!.spawnLocation
        )
        registerEventListener()
        start()
    }

    override fun start() {
        tpPlayersToGame()
        state = GameModeState.RUNNING
    }

    override fun stop() {
        state = GameModeState.FINISHED
    }
}