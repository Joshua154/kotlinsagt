package framework.gamemodes

import framework.Framework
import org.bukkit.Material
import org.bukkit.entity.TNTPrimed
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.ExplosionPrimeEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.inventory.ItemStack
import util.item.ItemBuilder

class TNTRun(private val framework: Framework) : GameMode(framework) {

    override val name: String = "tntrun"
    override val displayName: String = "TNT Run"
    override val displayItem: ItemStack = ItemBuilder(Material.TNT).build()
    override val worldName: String = "tntrun"
    override val description: String =
        "In TNT Run musst du über TNT Blöcke laufen. Wenn du auf einem TNT Block stehst, verschwindet dieser nach ein paar Sekunden. Das Ziel ist es, der letzte Spieler zu sein."
    override val minPlayers: Int = 3
    override val maxPlayers: Int = Int.MAX_VALUE
    override val hasPoints: Boolean = false
    override val hasPreBuildWorld: Boolean = true
    override val isFinale: Boolean = false

    override fun prepare() {
        load()
        registerEventListener()
        start() // COMBAK later to be called by command/ui
    }

    override fun unregisterEventListener() {
        PlayerMoveEvent.getHandlerList().unregister(this);
        ExplosionPrimeEvent.getHandlerList().unregister(this);
        // REVIEW geht auch Event.getHandlerList().unregister(this); ?
    }

    override fun start() {
        tpPlayersToGame()
        isRunning = true
    }

    override fun stop() {
        isRunning = false
    }

    override fun cleanup() {}

    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        if (!isPlayer(event.player) || !isRunning) return

        val blockLocation = event.to.block.location
        // event.player.sendMessage("§c${blockLocation.x}|${blockLocation.y}|${blockLocation.z} §a${getSpawnLocation().x}|${getSpawnLocation().y}|${getSpawnLocation().z}")
        if (blockLocation.y < getSpawnLocation().y - 5) {  // REVIEW die Spawnlocation ist irgendwie falsch
            event.player.gameMode = org.bukkit.GameMode.SPECTATOR
            addToDead(event.player)
            playerLoose(event.player) // "x ist ausgeschieden!"
            return
        }
        if (blockLocation == event.from.block.location) return

        val materialsToReplace = arrayOf(Material.TNT, Material.SAND, Material.GRAVEL)
        blockLocation.subtract(0.0, 1.0, 0.0)

        val world = event.player.location.world
        if (world.getBlockAt(blockLocation).type !in materialsToReplace) return

        val fuxelSagt = framework.getFuxelSagt()
        fuxelSagt.server.scheduler.runTaskLater(fuxelSagt, Runnable {
            world.getBlockAt(blockLocation).type = Material.AIR
            world.spawn(blockLocation.add(0.5, -0.5, 0.5), TNTPrimed::class.java)
//            blockLocation.subtract(0.0, 1.0, 0.0)
//            world.getBlockAt(blockLocation).type = Material.AIR
        }, 20L)
    }

    @EventHandler
    fun onTNTexplosion(event: ExplosionPrimeEvent) {
        event.isCancelled = true
    }
}