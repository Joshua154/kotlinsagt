package framework.gamemodes

import framework.Framework
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.inventory.ItemStack
import util.item.ItemBuilder
import java.util.UUID


/*
TODO
 - Sounds
 - Messages
 - Bossbar
 - win method
 - positions
 - remove gen
 */
class Dropper(private val framework: Framework,
              override val name: String = "dropper",
              override val displayName: String = "Dropper",
              override val displayItem: ItemStack = ItemBuilder(Material.HOPPER).build(),
              override val description: String = "Falle ohne zu sterben.",
              override val minPlayers: Int = 1, //TODO: to be changed
              override val maxPlayers: Int = 100, //TODO: to be changed
              override val worldName: String = "dropper",
              override val hasPreBuildWorld: Boolean = false,
              override val roundTime: Int = 60*10,
              override val hasPoints: Boolean = false,
              override val hasTeams: Boolean = false,
              override val isFinale: Boolean = false
) : GameMode(framework)  {
    private lateinit var locations: Map<Int,Location>
    private val neededToEnd: Int = 1; //TODO: to be changed
    private val playerPositions: MutableMap<UUID,Int> = mutableMapOf()
    private var spawn: Location? = null;
    override fun prepare() {
        load()
        val world = Bukkit.getWorld(this.worldName)
        spawn = Location(world,0.5,100.0,0.5)
        world?.getBlockAt(spawn!!.clone().add(spawn!!,-1.0,-1.0,-1.0))?.setType(Material.STONE,true)
        world?.getBlockAt(spawn!!.clone().add(spawn!!,0.0,-1.0,-1.0))?.setType(Material.STONE,true)
        world?.getBlockAt(spawn!!.clone().add(spawn!!,-1.0,-1.0,0.0))?.setType(Material.STONE,true)
        world?.getBlockAt(spawn!!.clone().add(spawn!!,0.0,-1.0,0.0))?.setType(Material.STONE,true)
        world?.getBlockAt(spawn!!.clone().add(spawn!!,-1.0,-161.0,-1.0))?.setType(Material.WATER,true)
        world?.getBlockAt(spawn!!.clone().add(spawn!!,0.0,-161.0,-1.0))?.setType(Material.WATER,true)
        world?.getBlockAt(spawn!!.clone().add(spawn!!,-1.0,-161.0,0.0))?.setType(Material.WATER,true)
        world?.getBlockAt(spawn!!.clone().add(spawn!!,0.0,-161.0,0.0))?.setType(Material.WATER,true)
        locations = mutableMapOf(
            Pair(0,Location(Bukkit.getWorld(worldName),0.0,100.0,0.0)),
            Pair(1,Location(Bukkit.getWorld(worldName),1.0,100.0,0.0)),
            Pair(2,Location(Bukkit.getWorld(worldName),0.0,100.0,1.0)),
        )
    }

    override fun setupPlayer(player: Player) {
        this.sendStartupMessage(player)
        this.registerEventListener()
        playerPositions[player.uniqueId] = 0
        locations[0]?.let { player.teleport(it) }
        player.teleportAsync(spawn!!.toCenterLocation())
        isRunning
    }

    override fun start() {
        isRunning = true
    }

    override fun stop() {
        TODO("Not yet implemented")
    }

    private fun addStage(player: Player){
        playerPositions[player.uniqueId] = playerPositions[player.uniqueId]!! + 1
        var playersInFinish = 0
        if(locations.size <= playerPositions[player.uniqueId]!!){
            player.gameMode = org.bukkit.GameMode.SPECTATOR
        } else {
            val location = locations[playerPositions[player.uniqueId]]
            player.teleport(location!!)
        }

        playerPositions.forEach {
            if(locations.size == it.value) playersInFinish++
        }
        if(playersInFinish >= neededToEnd){
            isRunning = false
            getPlayers().forEach { p ->
                val uuid = p.uniqueId
                val pos = playerPositions[uuid]
                if(pos!! < locations.size) playerLoose(p)
            }
        }
    }

    @EventHandler
    fun onWater(event: PlayerMoveEvent){
        if(event.to.block.type != Material.WATER) return
        val player = event.player
        //TODO: play sound, send message
        addStage(player)
    }

    @EventHandler
    fun onDamaga(event: EntityDamageEvent){
        if(event.entity !is Player){
            return
        }
        val player: Player = event.entity as Player
        if(!isRunning) return
        event.isCancelled = true
        if(event.cause != EntityDamageEvent.DamageCause.FALL) return
        val pos = playerPositions[player.uniqueId]
        val location = locations[pos]
        player.teleport(location!!)
    }
}