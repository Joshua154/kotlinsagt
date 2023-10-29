package framework.gamemodes

import FuxelSagt
import framework.Framework
import framework.configuration.Configurable
import framework.configuration.ConfigurableValueInterface
import net.kyori.adventure.text.Component
import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import java.io.File

abstract class GameMode(private val framework: Framework) : Listener {

    private val fuxelSagt: FuxelSagt = framework.getFuxelSagt()

    private val players: ArrayList<Player> = ArrayList()
    private val dead: ArrayList<Player> = ArrayList()
    private val points: HashMap<Player, Int> = HashMap()
    protected var isRunning: Boolean = false
    open var remainingTime = -1 // is set to roundTime on load, for possible countdown
    private var taskID: Int = -1

    // Description
    abstract val name: String
    abstract val displayName: String
    abstract val displayItem: ItemStack
    abstract val description: String

    abstract val minPlayers: Int
    abstract val maxPlayers: Int
    abstract val worldName: String
    abstract val hasPreBuildWorld: Boolean

    // Modi
    // Zeit-Modus
    abstract val roundTime: Int  // in seconds, -1 = infinite, to countdown from
    // Punkte-Modus
    abstract val hasPoints: Boolean
    open var survivorRate: Double = 1.0 // on gamestop which top of players survives, 0.0 - 1.0 in Percent
    // Teamwin-Modus
    abstract val hasTeams: Boolean
    open var teamQuantity: Int = 2
    // Final-Modus
    abstract val isFinale: Boolean

    fun getConfigurableValues(): List<ConfigurableValueInterface<out Any>> {
        val values: MutableList<ConfigurableValueInterface<out Any>> = mutableListOf();
        for (field in this.javaClass.fields) {
            for (annotation in field.annotations) {
                if (annotation is Configurable) {
                    values.add(ConfigurableValueInterface(annotation.name, annotation.displayItem, field, field.get(this)));
                }
            }
        }
        return values;
    }

    open fun applyConfiguration() {}

    /** wird aufgerufen, wenn der Gamemode geladen wird **/
    fun load() {
        this.fuxelSagt.logger.info(ChatColor.WHITE.toString() + "Loading ${this.displayName}...")
        if (this.hasPreBuildWorld) this.copyPreBuildWorld()
        else this.fuxelSagt.logger.info("Generating new map for ${this.name}...");
        remainingTime = roundTime
        fuxelSagt.server.createWorld(getWorldCreator())
    }

    fun copyPreBuildWorld() {
        this.fuxelSagt.logger.info("Loading prebuilt map for ${this.name}...");
        val pluginFolder = File(fuxelSagt.dataFolder.absolutePath)
        val serverFolder = getServerFolder()

        val worldFolder = File(pluginFolder, "preBuildWorlds/$worldName")

        if (!worldFolder.exists()) {
            fuxelSagt.logger.warning("PreBuildWorld $worldName not found!")
            return
        }

        deleteCreatedWorld()
        worldFolder.copyRecursively(getCreatedWoldFile())
    }

    private fun getServerFolder(): File{
        val pluginFolder = File(fuxelSagt.dataFolder.absolutePath)

        fuxelSagt.logger.info("PluginFolder: ${pluginFolder.path}")
        val path = pluginFolder.path.split(File.separator).toMutableList()
        fuxelSagt.logger.info("Server-Folder: $path")
        path.removeLast()
        path.removeLast()
        return File(path.stream().reduce("") { a, b -> "$a${File.separator}$b" })
    }

    fun getCreatedWoldFile(): File {
        return File(getServerFolder(), worldName)
    }

    fun deleteCreatedWorld() {
        val worldFolderFiles = getCreatedWoldFile()
        if(worldFolderFiles.exists()){
            worldFolderFiles.deleteRecursively()
        }
    }

    open fun setupPlayer(player: Player) {
        this.sendStartupMessage(player);
    }

    open fun getWorldCreator(): WorldCreator {
        return WorldCreator(worldName)
            .generateStructures(false)
            .type(WorldType.FLAT)
            .generatorSettings("{\"biome\": \"minecraft:the_void\"," +
                                "\"layers\": [{\"block\": \"minecraft:bedrock\",\"height\": 1}, " +
                                            "{\"block\": \"minecraft:dirt\",\"height\": 2}, " +
                                            "{\"block\": \"minecraft:grass_block\",\"height\": 1}]}")
//            .generatorSettings(
//                "{\"biome\": \"minecraft:the_void\"," +
//                        "\"layers\": [{\"block\": \"minecraft:air\",\"height\": 1}]}"
//            )
    }

    /** wird aufgerufen, wenn der Gamemode entladen wird **/
    fun unload() {
        for (player in this.players) {
            player.teleport(Location(Bukkit.getWorld("world"), player.location.x, player.location.y, player.location.z)); // TODO: Maybe replace with dedicated lobby
        }
        val worldFolder: File? = this.fuxelSagt.server.getWorld(this.worldName)?.worldFolder;
        this.fuxelSagt.server.unloadWorld(this.worldName, false);
        worldFolder?.deleteRecursively();
        unregisterEventListener()
        reset()
        cleanup()
    }

    fun registerEventListener() {
        fuxelSagt.server.pluginManager.registerEvents(this, fuxelSagt)
    }

    abstract fun unregisterEventListener()

    /** wird aufgerufen, wenn der Gamemode gestartet wird **/
    abstract fun prepare()

    abstract fun start()

    fun sendStartupMessage(player: Player) {
        player.sendMessage(Component.text("§b==== ${this.displayName} ====§r"))
        player.sendMessage(Component.text("§7${this.description}§r\n"))
    }

    /** wird aufgerufen, wenn der Gamemode gestoppt wird **/
    abstract fun stop()

    /** wird aufgerufen, wenn der Gamemode resettet wird **/
    private fun reset() {
        this.framework.getFuxelSagt().logger.info("Resetting the gamemode ${this.name}...");
        val server = framework.getFuxelSagt().server
        server.onlinePlayers.stream()
            .filter { player -> player.world.name == worldName }
            .forEach{ player -> player.teleport(server.getWorld("world")!!.spawnLocation) }

        println(server.unloadWorld(worldName, false))
        server.worlds.remove(server.getWorld(worldName))
        deleteCreatedWorld()
    }

    abstract fun cleanup()

    fun getSpawnLocation(): Location {
        return fuxelSagt.server.getWorld(worldName)!!.spawnLocation
    }

    /** wird aufgerufen, wenn der Spieler in die GameWorld teleportiert wird **/
    fun tpToGameSpawn(player: Player) {
        player.teleport(getSpawnLocation())
    }

    fun tpPlayersToGame(vararg players: Player) {
        for (player in players) {
            Bukkit.getScheduler().runTaskLater(fuxelSagt, Runnable {
                this.tpToGameSpawn(player)
            }, 2)
        }
    }

    fun getPlayers(): ArrayList<Player> {
        return players
    }

    fun getDead(): ArrayList<Player> {
        return dead
    }

    fun addToPlayers(vararg players: Player) {
        for (player in players) {
            this.setupPlayer(player);
            this.players.add(player)
            this.points[player] = 0
        }
    }

    fun addToDead(player: Player) {
        if (players.contains(player)) {
            players.remove(player)
        }
        this.dead.add(player)
        checkGameScore()
    }

    fun killPlayer(player: Player) {
        framework.getPlayerManager().killPlayer(player)
        addToDead(player)
    }

    fun isPlayer(player: Player): Boolean {
        return players.contains(player) && !dead.contains(player)
    }

    fun isSpectator(player: Player): Boolean {
        return dead.contains(player)
    }

    fun playerLoose(player: Player) {
        framework.getPlayerManager().playerLoose(player)
        for (p in fuxelSagt.server.onlinePlayers) {
            p.sendMessage(Component.text("§c» ${p.name} ist ausgeschieden!"))
        }
        checkGameScore()
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

    fun checkGameScore() {
        if (players.size == 1) {
            for (p in fuxelSagt.server.onlinePlayers) {
                p.sendMessage(Component.text("§6» ${players[0].name} hat gewonnen!"))
                // TODO Win Animation
                // TODO übergeben an framework?
            }
            if (isRunning) { stop() }
        }
        if (!isRunning) {
            val survivors: List<Player>
            if (hasPoints) {
                // if hasTeams
                // else
                val numberOfSurvivors = (points.size * survivorRate).toInt()
                val sortedPlayers = points.entries.sortedByDescending { it.value }

                survivors = sortedPlayers.take(numberOfSurvivors).map { it.key }  // Die besten Spieler behalten
                for (d in sortedPlayers.drop(numberOfSurvivors)
                    .map { it.key }) {  // Die restlichen Spieler als "dead" hinzufügen
                    addToDead(d)
                }
                // points.keys.retainAll { it in survivors }  // Aktualisieren des "points"-HashMaps, wenn nötig
            } else {
                // if hasTeams
                // else
                survivors = fuxelSagt.server.onlinePlayers as List<Player>
            }
            for (p in survivors) {
                p.sendMessage(Component.text("§6» ${players.size} Spieler haben überlebt!"))
                // TODO übergeben an framework?
            }
        }

    }

    fun startTimer() {
        val timerTask: Runnable = Runnable{
            if (remainingTime <= 0) {
                stop()
                fuxelSagt.server.scheduler.cancelTask(taskID)
            }
            else {
                remainingTime--
            }
        }
        taskID = fuxelSagt.server.scheduler.runTaskTimer(fuxelSagt, timerTask, 0L, 20L).taskId
    }
}