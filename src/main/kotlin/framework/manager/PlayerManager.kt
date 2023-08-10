package framework.manager

import framework.Framework
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import java.util.Optional
import java.util.UUID

class PlayerManager(framework: Framework) {
    private var players = ArrayList<UUID>()
    private var spectators = ArrayList<UUID>()
    private var fuxel: Optional<UUID> = Optional.empty()

    fun setFuxel(uuid: UUID) {
        this.fuxel = Optional.of(uuid)
    }

    fun addPlayer(uuid: UUID) {
        this.players.add(uuid)
    }

    fun addPlayer(player: Player) {
        this.players.add(player.uniqueId)
    }

    fun isPlayer(uuid: UUID): Boolean {
        return this.players.contains(uuid)
    }

    fun isPlayer(player: Player): Boolean {
        return this.players.contains(player.uniqueId)
    }

    fun addSpectator(player: UUID) {
        this.spectators.add(player)
    }

    fun addSpectator(player: Player) {
        this.spectators.add(player.uniqueId)
    }

    fun isSpectator(uuid: UUID): Boolean {
        return this.spectators.contains(uuid)
    }

    fun isSpectator(player: Player): Boolean {
        return this.spectators.contains(player.uniqueId)
    }

    fun setSpectator(uuid: UUID): Boolean {
        this.players.remove(uuid)
        return this.spectators.add(uuid)
    }

    fun isFuxel(uuid: UUID): Boolean {
        if (this.fuxel.isEmpty) return false
        return uuid == this.fuxel.get()
    }

    fun isFuxel(player: Player): Boolean {
        if (this.fuxel.isEmpty) return false
        return player.uniqueId == this.fuxel.get()
    }

    fun getFormat(player: Player): Component {
        val text: String = when {
            isFuxel(player) -> Colors.FUXEL.prefix+player.name+Colors.FUXEL.suffix
            isSpectator(player) -> Colors.SPECTATOR.prefix+player.name+Colors.PLAYER.suffix
            isPlayer(player) -> Colors.PLAYER.prefix+player.name+Colors.PLAYER.suffix
            else -> Colors.OTHER.prefix+player.name+Colors.OTHER.suffix
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

    // Worker-Methods

    fun killPlayer(player: UUID) {
        if (!this.players.remove(player)) return;
        this.spectators.add(player);
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