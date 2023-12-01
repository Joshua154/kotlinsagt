package gamemodes.tntrun

import framework.Framework
import framework.gamemode.GameMode
import org.bukkit.Material
import org.bukkit.entity.TNTPrimed
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.ExplosionPrimeEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.inventory.ItemStack
import util.item.ItemBuilder

class TNTRun(private val framework: Framework) : GameMode(framework) {
    // Description
    override val name: String = "tntrun"
    override val displayName: String = "TNT Run"
    override val displayItem: ItemStack = ItemBuilder(Material.TNT).build()
    override val worldName: String = "tntrun"
    override val description: String =
        "In TNT Run musst du über TNT Blöcke laufen. Wenn du auf einem TNT Block stehst, " +
                "verschwindet dieser nach ein paar Sekunden. Das Ziel ist es, der letzte Spieler zu sein."
    override val minPlayers: Int = 1 // COMBAK change to 3 later
    override val maxPlayers: Int = Int.MAX_VALUE
    override val hasPreBuiltWorld: Boolean = true

    // Modus
    override val roundTime: Int = 60 * 5 // 5 minutes

    // var remainingTime for countdown
    override val hasPoints: Boolean = false

    // default survivorRate = 1.0
    override val hasTeams: Boolean = false

    // default teamQuantity = 2
    override val isFinale: Boolean = false

    override fun prepare() {
        load()
        registerEventListener()
    }

    override fun unregisterEventListener() {
        PlayerMoveEvent.getHandlerList().unregister(this);
        ExplosionPrimeEvent.getHandlerList().unregister(this);
        // REVIEW geht auch Event.getHandlerList().unregister(this); ?
    }

    override fun start() {
        tpPlayersToGame()
        startTimer()
        isRunning = true
    }

    override fun stop() {
        checkGameScore()
        isRunning = false
    }

    override fun cleanup() {}

    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        if (!isPlayer(event.player) || !isRunning) return

        val blockLocation = event.to.block.location
        if (isInDeathZone(blockLocation)) {  // REVIEW die Spawnlocation ist irgendwie falsch
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
            //world.spawn(blockLocation.add(0.5, -0.5, 0.5), TNTPrimed::class.java)
            spawnTNT(blockLocation.add(0.5, -0.5, 0.5))
//            blockLocation.subtract(0.0, 1.0, 0.0)
//            world.getBlockAt(blockLocation).type = Material.AIR
        }, 20L)
    }

    @EventHandler
    fun onTNTSandFall(event: EntityMoveEvent) {
        if(event.entity !is FallingBlock) return
        if(isInDeathZone(event.entity.location))
            event.entity.remove()
    }

    private fun spawnTNT(location: Location){
        val world = location.world
        val tnt: FallingBlock = world.spawn(location, FallingBlock::class.java)
        tnt.blockData = Material.TNT.createBlockData()
        tnt.cancelDrop = true
        tnt.dropItem = false
    }

    private fun isInDeathZone(location: Location): Boolean {
        return location.y < getSpawnLocation().y - 5
    }
}