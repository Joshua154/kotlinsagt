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

    private val players: ArrayList<Player> = ArrayList()
    private val spectator: ArrayList<Player> = ArrayList()
    protected var isRunning: Boolean = false

    /** wird aufgerufen, wenn der Gamemode geladen wird **/
    fun load() {
        val world: World? = WorldCreator(worldName)
            .generateStructures(false)
            .type(WorldType.FLAT)
            .generatorSettings("{\"biome\": \"minecraft:the_void\"," +
                                "\"layers\": [{\"block\": \"minecraft:bedrock\",\"height\": 1}, " +
                                            "{\"block\": \"minecraft:dirt\",\"height\": 2}, " +
                                            "{\"block\": \"minecraft:grass_block\",\"height\": 1}]\"}")
            .createWorld()
        fuxelSagt.server.worlds.add(world)
    }

    /** wird aufgerufen, wenn der Gamemode entladen wird **/
    fun unload() {
        fuxelSagt.server.unloadWorld(worldName, false)
    }

    fun registerEventListener(){
        fuxelSagt.server.pluginManager.registerEvents(this, fuxelSagt)
    }

    /** wird aufgerufen, wenn der Gamemode gestartet wird **/
    abstract fun start()

    /** wird aufgerufen, wenn der Gamemode gestoppt wird **/
    abstract fun stop()

    /** wird aufgerufen, wenn der Gamemode resettet wird **/
    abstract fun reset()

    /** wird aufgerufen, wenn der Spieler in die GameWorld teleportiert wird **/
    fun tpToGameSpawn(player: Player) {
        fuxelSagt.server.scheduler.runTaskAsynchronously(fuxelSagt, Runnable { //TODO: test for Lag
            player.teleport(fuxelSagt.server.getWorld(worldName)!!.spawnLocation)
        })
    }

    fun tpPlayersToGame(vararg players: Player) {
        for (player in players){
            this.tpToGameSpawn(player)
        }
    }

    fun addPlayer(vararg players: Player){
        for (player in players){
            this.players.add(player)
        }
    }

    fun killPlayer(player: Player) {
        framework.getPlayerManager().killPlayer(player)
        spectator.add(player)
    }

    fun isPlayer(player: Player): Boolean{
        return players.contains(player) && !spectator.contains(player)
    }
}