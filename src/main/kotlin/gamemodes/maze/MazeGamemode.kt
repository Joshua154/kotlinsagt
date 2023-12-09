package gamemodes.maze

import framework.Framework
import framework.configuration.Configurable
import framework.gamemode.GameMode
import framework.gamemode.GameModeState
import net.kyori.adventure.text.Component
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.WorldCreator
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector


class MazeGamemode(private val framework: Framework) : GameMode(framework) {
    override val name: String = "maze"
    override val displayName: String = "Labirinth"
    override val displayItem: ItemStack = ItemStack(Material.DIRT_PATH)
    override val description: String = "placeholder"
    override val minPlayers: Int = 1
    override val maxPlayers: Int = 100
    override val worldName: String = "maze"
    override val hasPreBuiltWorld: Boolean = false
    override val roundTime: Int = 60 * 5
    override val hasPoints: Boolean = false
    override val hasTeams: Boolean = false
    override val isFinale: Boolean = false

    @Configurable(Material.SMOOTH_QUARTZ, "Size of the maze")
    var size: Int = 51

    @Configurable(Material.SMOOTH_QUARTZ, "Size Block")
    var sizeOfBlocks: Int = 3 //best 2 or 3 //TODO: strange offset with 1 and 4++

    private lateinit var mb: MazeBuilder
    private val finishedPlayers: MutableSet<Player> = mutableSetOf()

    override fun getWorldCreator(): WorldCreator {
        return super.getWorldCreator()
            .generatorSettings(
                "{\"biome\": \"minecraft:the_void\"," +
                        "\"layers\": [{\"block\": \"minecraft:air\",\"height\": 1}]}"
            )
    }

    override fun prepare() {
        load()
        mb = MazeBuilder(size)
        mb.buildMaze(
            sizeOfBlocks = sizeOfBlocks,
            height = 6,
            center = getSpawnLocation().clone().subtract(0.0, 1.0, 0.0)
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

    override fun getSpawnLocation(): Location {
        return (mb.getSpawnLocation() ?: framework.getFuxelSagt().server.getWorld("maze")!!.spawnLocation.clone())
            .apply { y = 0.0 }
    }

    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        val player = event.player
        if (!isPlayer(player) || state != GameModeState.RUNNING) return
        if (finishedPlayers.contains(player)){
            if(isOverFinishLine(event.to.block.location.clone()))
                event.isCancelled = true
            return
        }

        if (isOverFinishLine(event.to.block.location.clone().subtract(1.0, 0.0, 0.0))) {
            finishedPlayers.add(player)
            framework.getFuxelSagt().sendPlayerMessage(
                Component.text(player.name + " hat das Ziel erreicht!")
            )
        }
    }

    private fun isOverFinishLine(location: Location): Boolean {
        val point = location.block.location.clone().toVector()
        val v1 = mb.finishLine().first.block.location.clone().toVector()
        val v2 = mb.finishLine().first.block.location.clone().add(mb.finishLine().second).toVector()
        val yVec = Vector(0.0, 1.0, 0.0)
        return isBetween(point, v1.subtract(yVec), v2.subtract(yVec)) ||
                isBetween(point, v1.add(yVec), v2.add(yVec)) ||
                isBetween(point, v1.add(yVec), v2.add(yVec))
    }

    private fun isBetween(pointVector: Vector, vector1: Vector, vector2: Vector): Boolean {
        val diffVec = vector2.clone().subtract(vector1)
        val dotProd = diffVec.clone().dot(pointVector.clone().subtract(vector1))

        return dotProd >= 0
                && dotProd <= vector1.distance(vector2)
                && pointVector.clone().subtract(vector1).crossProduct(diffVec).lengthSquared() == 0.0
    }
}