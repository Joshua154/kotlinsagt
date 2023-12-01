package framework.manager

import framework.Framework
import net.kyori.adventure.text.Component
import org.bukkit.GameMode
import org.bukkit.entity.Player
import java.util.*

class PlayerManager(private val framework: Framework) {
    private val players: MutableSet<UUID> = mutableSetOf()
    private var spectators:MutableSet<UUID> = mutableSetOf()
    private var fuxel: Optional<UUID> = Optional.empty()

    fun uuidToPlayer(uuid: UUID): Player? {
        return framework.getFuxelSagt().server.getPlayer(uuid)
    }

    // SETTER
    fun setFuxel(uuid: UUID) {
        this.fuxel = Optional.of(uuid)
    }

    fun setPlayer(uuid: UUID) {
        this.spectators.remove(uuid)
        this.players.add(uuid)
    }

    fun setSpectator(uuid: UUID): Boolean {
        this.players.remove(uuid)
        return this.spectators.add(uuid)
    }

    // GETTER
    fun getPlayerUUIDList() : Set<UUID> {
        return players
    }

    fun getPlayerList() : Set<Player?> {
        return players.map { framework.getFuxelSagt().server.getPlayer(it) }.toSet()
    }

    // ADDER
    fun addPlayer(player: Player) {
        this.players.add(player.uniqueId)
    }

    fun addPlayer(uuid: UUID) {
        this.players.add(uuid)
    }

    fun addSpectator(player: UUID) {
        this.spectators.add(player)
    }

    fun addSpectator(player: Player) {
        this.spectators.add(player.uniqueId)
    }

    // CHECKER
    fun isFuxel(player: Player): Boolean {
        if (this.fuxel.isEmpty) return false
        return player.uniqueId == this.fuxel.get()
    }

    fun isFuxel(uuid: UUID): Boolean {
        if (this.fuxel.isEmpty) return false
        return uuid == this.fuxel.get()
    }

    fun isPlayer(player: Player): Boolean {
        return this.players.contains(player.uniqueId)
    }

    fun isPlayer(uuid: UUID): Boolean {
        return this.players.contains(uuid)
    }

    fun isSpectator(player: Player): Boolean {
        return this.spectators.contains(player.uniqueId)
    }

    fun isSpectator(uuid: UUID): Boolean {
        return this.spectators.contains(uuid)
    }

    // OTHER GETTER
    fun getFormat(player: Player): Component {
        val text: String = when {
            isFuxel(player) -> Colors.FUXEL.prefix + player.name + Colors.FUXEL.suffix
            isSpectator(player) -> Colors.SPECTATOR.prefix + player.name + Colors.PLAYER.suffix
            isPlayer(player) -> Colors.PLAYER.prefix + player.name + Colors.PLAYER.suffix
            else -> Colors.OTHER.prefix + player.name + Colors.OTHER.suffix
        }
        return Component.text(text)
    }

    fun getType(player: Player): Colors {
        return when {
            isFuxel(player) -> Colors.FUXEL
            isSpectator(player) -> Colors.SPECTATOR
            isPlayer(player) -> Colors.PLAYER
            else -> Colors.OTHER
        }
    }

    // MANAGER
    fun playerLoose(player: Player) {
        if (!this.players.remove(player.uniqueId)) return;
        this.spectators.add(player.uniqueId)
        // broadcast
    }

    fun killPlayer(player: UUID) {
        if (!this.players.remove(player)) return;
        this.spectators.add(player);
        framework.getFuxelSagt().server.getPlayer(player)?.gameMode ?: GameMode.SPECTATOR
        // broadcast
    }

    fun killPlayer(player: Player) {
        return killPlayer(player.uniqueId)
    }

    fun revievePlayer(player: UUID) {
        this.spectators.remove(player)
        this.players.add(player)
        // broadcast
    }

    fun revievePlayer(player: Player) {
        return revievePlayer(player.uniqueId)
    }
}