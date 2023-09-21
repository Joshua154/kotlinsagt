package framework.gamemodes

import FuxelSagt
import framework.Framework
import net.kyori.adventure.text.Component
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.WorldCreator
import org.bukkit.WorldType
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent

abstract class GameMode(private val framework: Framework, private val shortName: String): Listener {

    private val fuxelSagt: FuxelSagt = framework.getFuxelSagt()

    private val players: ArrayList<Player> = ArrayList()
    private val spectators: ArrayList<Player> = ArrayList()
    protected var isRunning: Boolean = false

    abstract val displayName: String
    abstract val description: String
    abstract val minPlayers: Int
    abstract val maxPlayers: Int
    abstract val worldName: String
    // abstract val gameModeType: GameModeType -> survive (time), points (time or points), finale
    val timeLimit = -1 // in seconds

    /** wird aufgerufen, wenn der Gamemode geladen wird **/
    fun load() {
        val world: World? = WorldCreator(worldName)
            .generateStructures(false)
            .type(WorldType.FLAT)
            .generatorSettings("{\"biome\": \"minecraft:the_void\"," +
                                "\"layers\": [{\"block\": \"minecraft:bedrock\",\"height\": 1}, " +
                                            "{\"block\": \"minecraft:dirt\",\"height\": 2}, " +
                                            "{\"block\": \"minecraft:grass_block\",\"height\": 1}]}")
            .createWorld()
        fuxelSagt.server.worlds.add(world)
    }

    /** wird aufgerufen, wenn der Gamemode entladen wird **/
    fun unload() {
        fuxelSagt.server.unloadWorld(worldName, false)
        unregisterEventListener()
    }

    fun registerEventListener(){
        fuxelSagt.server.pluginManager.registerEvents(this, fuxelSagt)
    }

    abstract fun unregisterEventListener()

    /** wird aufgerufen, wenn der Gamemode gestartet wird **/
    abstract fun prepare()

    abstract fun start()

    fun startupMessage() {
        for (player in players) {
            player.sendMessage(Component.text("§b==== $displayName ====§r"))
            player.sendMessage(Component.text("§7$description§r\n"))
        }
    }

    /** wird aufgerufen, wenn der Gamemode gestoppt wird **/
    abstract fun stop()

    /** wird aufgerufen, wenn der Gamemode resettet wird **/
    abstract fun reset()

    fun getSpawnLocation(): Location {
        return fuxelSagt.server.getWorld(worldName)!!.spawnLocation
    }

    /** wird aufgerufen, wenn der Spieler in die GameWorld teleportiert wird **/
    fun tpToGameSpawn(player: Player) {
        fuxelSagt.server.scheduler.runTaskAsynchronously(fuxelSagt, Runnable { //TODO: test for Lag
            player.teleport(getSpawnLocation())
        })
    }

    fun tpPlayersToGame(vararg players: Player) {
        for (player in players){
            this.tpToGameSpawn(player)
        }
    }

    fun addPlayers(vararg players: Player){
        for (player in players){
            this.players.add(player)
        }
    }

    fun addSpectator(player: Player){
        if (players.contains(player)) {
            players.remove(player)
        }
        this.spectators.add(player)
    }

    fun killPlayer(player: Player) {
        framework.getPlayerManager().killPlayer(player)
        spectators.add(player)
    }

    fun isPlayer(player: Player): Boolean{
        return players.contains(player) && !spectators.contains(player)
    }

    fun isSpectator(player: Player): Boolean{
        return spectators.contains(player)
    }

    fun playerLoose(player: Player) {
        framework.getPlayerManager().playerLoose(player)
        for (p in fuxelSagt.server.onlinePlayers) {
            p.sendMessage(Component.text("§c» ${p.name} ist ausgeschieden!"))
        }
    }

    fun playerAddPoints(player: Player, points: Int) {
        // hashmap mit player und points
        // points++
    }

    fun playerRemovePoints(player: Player, points: Int) {
        // hashmap mit player und points
        // points--
    }

    fun playerGetPoints(player: Player): Int {
        // hashmap mit player und points
        // return points
        return 0
    }

    // TODO countdown und punkte/überlebende auswertung
}