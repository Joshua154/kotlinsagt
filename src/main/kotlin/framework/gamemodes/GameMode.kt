package framework.gamemodes

import FuxelSagt
import framework.Framework
import net.kyori.adventure.text.Component
import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack

abstract class GameMode(private val framework: Framework): Listener {

    private val fuxelSagt: FuxelSagt = framework.getFuxelSagt()

    private val players: ArrayList<Player> = ArrayList()
    private val dead: ArrayList<Player> = ArrayList()
    private val points: HashMap<Player, Int> = HashMap()
    protected var isRunning: Boolean = false

    abstract val name: String
    abstract val displayName: String
    abstract val displayItem: ItemStack
    abstract val description: String
    abstract val minPlayers: Int
    abstract val maxPlayers: Int
    abstract val worldName: String
    abstract val HasPoints: Boolean
    abstract val IsFinale: Boolean
    open var timeLimit = -1 // in seconds

    /** wird aufgerufen, wenn der Gamemode geladen wird **/
    fun load() {
        val world: World? = getWorldCreator().createWorld()
        fuxelSagt.server.worlds.add(world)
    }

    open fun getWorldCreator(): WorldCreator{
        return WorldCreator(worldName)
            .generateStructures(false)
            .type(WorldType.FLAT)
//            .generatorSettings("{\"biome\": \"minecraft:the_void\"," +
//                                "\"layers\": [{\"block\": \"minecraft:bedrock\",\"height\": 1}, " +
//                                            "{\"block\": \"minecraft:dirt\",\"height\": 2}, " +
//                                            "{\"block\": \"minecraft:grass_block\",\"height\": 1}]}")
            .generatorSettings("{\"biome\": \"minecraft:the_void\"," +
                    "\"layers\": [{\"block\": \"minecraft:air\",\"height\": 1}]}")
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
        player.teleport(getSpawnLocation())
    }

    fun tpPlayersToGame(vararg players: Player) {
        for (player in players){
            Bukkit.getScheduler().runTaskLater(fuxelSagt, Runnable {
                this.tpToGameSpawn(player)
            }, 2)
        }
    }

    fun getPlayers(): ArrayList<Player> { return players }

    fun getDead(): ArrayList<Player> { return dead }

    fun addToPlayers(vararg players: Player){
        for (player in players){
            this.players.add(player)
            this.points[player] = 0
        }
    }

    fun addToDead(player: Player){
        if (players.contains(player)) {
            players.remove(player)
        }
        this.dead.add(player)
    }

    fun killPlayer(player: Player) {
        framework.getPlayerManager().killPlayer(player)
        dead.add(player)
    }

    fun isPlayer(player: Player): Boolean{
        return players.contains(player) && !dead.contains(player)
    }

    fun isSpectator(player: Player): Boolean{
        return dead.contains(player)
    }

    fun playerLoose(player: Player) {
        framework.getPlayerManager().playerLoose(player)
        for (p in fuxelSagt.server.onlinePlayers) {
            p.sendMessage(Component.text("§c» ${p.name} ist ausgeschieden!"))
        }
        if (players.size == 1) {
            for (p in fuxelSagt.server.onlinePlayers) {
                p.sendMessage(Component.text("§e» ${players[0].name} hat gewonnen!"))
                // TODO Win Animation
            }
            stop()
        }
    }

    fun playerAddPoints(player: Player, newPoints: Int) {
        this.points[player] = (this.points[player] ?: 0) + newPoints
    }

    fun playerRemovePoints(player: Player, newPoints: Int) {
        this.points[player] = (this.points[player] ?: 0) - newPoints
    }

    fun playerGetPoints(player: Player): Int {
        return this.points[player] ?: 0
    }

    fun getPoints(): HashMap<Player, Int> {
        if (points.isEmpty()) return hashMapOf<Player, Int>()
        return points.toList().sortedBy { (_, value) -> value }.toMap() as HashMap<Player, Int>
    }

    // TODO countdown und punkte/überlebende auswertung
}