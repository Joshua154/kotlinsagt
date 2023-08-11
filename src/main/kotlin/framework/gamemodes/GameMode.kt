package framework.gamemodes

import FuxelSagt
import framework.Framework
import org.bukkit.World
import org.bukkit.WorldCreator
import org.bukkit.WorldType
import org.bukkit.entity.Player
import org.bukkit.event.Listener

abstract class GameMode(private val framework: Framework, private val worldName: String, private val name: String): Listener {

    private val fuxelSagt: FuxelSagt = framework.getFuxelSagt()

    private lateinit var players: ArrayList<Player>
    private lateinit var winners: ArrayList<Player>
    private lateinit var loosers: ArrayList<Player>

    /** wird aufgerufen, wenn der Gamemode geladen wird **/
    fun load() {
        val world: World? = WorldCreator(worldName)
            .generateStructures(false)
            .type(WorldType.FLAT)
            .generatorSettings("\"layers\": [{\"block\": \"minecraft:bedrock\",\"height\": 1}, " +
                                            "{\"block\": \"minecraft:dirt\",\"height\": 2}, " +
                                            "{\"block\": \"minecraft:grass_block\",\"height\": 1}]\"")
            .createWorld()
        fuxelSagt.server.worlds.add(world)
    }

    /** wird aufgerufen, wenn der Gamemode entladen wird **/
    fun unload() {
        fuxelSagt.server.unloadWorld(worldName, false)
    }

    /** wird aufgerufen, wenn der Gamemode gestartet wird **/
    abstract fun start()

    /** wird aufgerufen, wenn der Gamemode gestoppt wird **/
    abstract fun stop()

    /** wird aufgerufen, wenn der Gamemode resettet wird **/
    abstract fun reset()

    /** wird aufgerufen, wenn der Spieler in die GameWorld teleportiert wird **/
    fun tpToGameSpawn(player: Player) {
        player.teleport(fuxelSagt.server.getWorld(worldName)!!.spawnLocation)
    }

    fun tpPlayersToGame(vararg players: Player) {
        val tpAllPlayerTask = Runnable { //TODO: test for Lag
            for (player in players){
                tpToGameSpawn(player)
            }
        }

        if (fuxelSagt.server.getWorld(worldName)?.players?.count()!! < players.count()) {
            fuxelSagt.server.scheduler.runTaskAsynchronously(fuxelSagt, tpAllPlayerTask)
        } else {
            tpAllPlayerTask.run()
        }
    }

    fun killPlayer(player: Player) {
        framework.getPlayerManager().killPlayer(player)
        loosers.add(player)
    }
}