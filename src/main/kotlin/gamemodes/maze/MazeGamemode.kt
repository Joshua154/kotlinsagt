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
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector


class MazeGamemode(private val framework: Framework) : GameMode(framework) {
    override val name: String = "maze"
    override val displayName: String = "Labirinth"
    override val displayItem: ItemStack = ItemStack(Material.DIRT_PATH)
    override val description: String = "placeholder"
    override val minPlayers: Int = 1
    override val maxPlayers: Int = 500
    override val worldName: String = "maze"
    override val hasPreBuiltWorld: Boolean = false
    override val roundTime: Int = 60 * 5
    override val hasPoints: Boolean = false
    override val hasTeams: Boolean = false
    override val isFinale: Boolean = false

    @Configurable(Material.SMOOTH_QUARTZ, "Size of the maze")
    var size: Int = 51 //default 51

    @Configurable(Material.SMOOTH_QUARTZ, "Size Block")
    var sizeOfBlocks: Int = 3 //best 2 or 3 //TODO: strange offset with 1 and 4++

    @Configurable(Material.SMOOTH_QUARTZ, "Height of the maze")
    var height: Int = 6

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
        mb = MazeBuilder(framework, size)
        mb.buildMaze(
            framework = framework,
            sizeOfBlocks = sizeOfBlocks,
            height = height,
            center = getSpawnLocation().clone().subtract(0.0, 1.0, 0.0)
        )
        registerEventListener()
    }

    override fun start() {
        tpPlayersToGame()
        //TODO: Start Countdown
        //TODO: Start logic

        mb.removeSpawnPartionWall()

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
        if (event.to.block.location == event.from.block.location)
            return
        val player = event.player
        if (!isPlayer(player) || state != GameModeState.RUNNING) return
        if (finishedPlayers.contains(player)){
            if(isOverFinishLine(event.to.block.location.clone())){
                event.player.velocity = Vector(1.0, 0.3, 0.0)
            }
            else if(isOverFinishLine(event.to.block.location.clone().add(1.0, 0.0, 0.0))){
                event.isCancelled = true
            }
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
        val v2 = mb.finishLine().second.clone()
        return isBetween(point, v1, v2)
    }

    private fun isBetween(pointVector: Vector, lineStart: Vector, direction: Vector): Boolean {
        val distance = pointVector.clone().subtract(lineStart).length()
        val dirLen = direction.length() + 1
        if (distance > dirLen)
            return false

        pointVector.y = 0.0
        lineStart.y = 0.0
        direction.y = 0.0
        return isPointOnLine(pointVector, lineStart, direction)
    }

    private fun isPointOnLine(point: Vector, lineStart: Vector, lineDirection: Vector): Boolean {
//        val lineDirectionNormalized = lineDirection.normalize().clone()
//        val directionVector = point.clone().subtract(lineStart)
//        val projection = directionVector.clone().dot(lineDirectionNormalized)
//        return projection >= 0 && projection <= directionVector.length()
        var len = lineDirection.length()
        while (len > 0) {
            val v = lineDirection.clone().normalize().multiply(len)
            if (point.distanceSquared(lineStart.clone().add(v)) < 0.1) {
                return true
            }
            len -= 0.1
        }
        return false
    }

    @EventHandler
    fun onPlayerDeath(event: EntityDamageEvent) {
        if (event.entity !is Player) return
        val player = event.entity as Player

        if (!isPlayer(player)) return
        if (player.health - event.finalDamage > 0) return
        tpToGameSpawn(player)

        player.health = 20.0
        player.foodLevel = 20

        event.isCancelled = true
    }

    @EventHandler
    fun onFoodLevelChange(event: FoodLevelChangeEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        if (event.player.hasPermission("fuxelsagt.admin")) return
        event.isCancelled = true
    }
}