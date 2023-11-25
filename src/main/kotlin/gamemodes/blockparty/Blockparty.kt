package gamemodes.blockparty

import framework.Framework
import framework.configuration.Configurable
import framework.gamemode.GameMode
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import util.item.ItemBuilder
import util.noise.Noise
import util.noise.TrigonometricNoise
import java.util.concurrent.ThreadLocalRandom

class Blockparty(private val framework: Framework) : GameMode(framework) {
    // Description
    override val name: String = "blockparty";
    override val displayName: String = "Blockparty";
    override val displayItem: ItemStack = ItemBuilder(Material.ORANGE_CONCRETE).build();
    override val worldName: String = "blockparty";
    override val description: String = "todo";
    override val minPlayers: Int = 2;
    override val maxPlayers: Int = Int.MAX_VALUE;
    override val hasPreBuiltWorld: Boolean = false;

    // Modus
    override val roundTime: Int = 60 * 5 // 5 minutes

    // var remainingTime for countdown
    override val hasPoints: Boolean = false

    // default survivorRate = 1.0
    override val hasTeams: Boolean = false

    // default teamQuantity = 2
    override val isFinale: Boolean = false

    // In-game Vars
    @Configurable(Material.IRON_BARS, "Größe der Map")
    private val bounds: Int = 50;

    @Configurable(Material.GREEN_STAINED_GLASS_PANE, "Sekunden zum Suchen der Richtigen Farbe")
    private val secondsToSearch: Int = 4;

    @Configurable(Material.RED_STAINED_GLASS_PANE, "Sekunden zum Ausruhen zwischen den Suchphasen")
    private val secondsPause: Int = 3;

    private lateinit var task: BukkitTask;
    private lateinit var bossBar: BossBar;
    private var secondsRemaining: Int = 0;
    private var currentColor: Color? = null;

    enum class Color(val displayName: String, val textColor: TextColor, val block: Material) {
        BLACK("Schwarz", NamedTextColor.BLACK, Material.BLACK_CONCRETE),
        BLUE("Blau", NamedTextColor.BLUE, Material.BLUE_CONCRETE),
        BROWN("Braun", TextColor.color(102, 76, 51), Material.BROWN_CONCRETE),
        CYAN("Cyan", NamedTextColor.DARK_AQUA, Material.CYAN_CONCRETE),
        GRAY("Grau", NamedTextColor.DARK_GRAY, Material.GRAY_CONCRETE),
        GREEN("Grün", NamedTextColor.DARK_GREEN, Material.GREEN_CONCRETE),
        LIGHT_BLUE("Hellblau", NamedTextColor.AQUA, Material.LIGHT_BLUE_CONCRETE),
        LIGHT_GRAY("Hellgrau", NamedTextColor.GRAY, Material.LIGHT_GRAY_CONCRETE),
        LIME("Hellgrün", NamedTextColor.GREEN, Material.LIME_CONCRETE),
        MAGENTA("Magenta", TextColor.color(178, 76, 216), Material.MAGENTA_CONCRETE),
        ORANGE("Orange", TextColor.color(216, 127, 51), Material.ORANGE_CONCRETE),
        PINK("Rosa", TextColor.color(242, 127, 165), Material.PINK_CONCRETE),
        PURPLE("Lila", TextColor.color(127, 63, 178), Material.PURPLE_CONCRETE),
        RED("Rot", NamedTextColor.DARK_RED, Material.RED_CONCRETE),
        WHITE("Weiß", NamedTextColor.WHITE, Material.WHITE_CONCRETE);
    }

    override fun getWorldCreator(): WorldCreator {
        return super.getWorldCreator()
            .generateStructures(false)
            .type(WorldType.FLAT)
            .generatorSettings("{\"biome\": \"minecraft:the_void\"," + "\"layers\": []}")
    }

    override fun setupPlayer(player: Player) {
        this.sendStartupMessage(player);
        player.inventory.clear();
        player.gameMode = org.bukkit.GameMode.ADVENTURE;
        player.inventory.setItem(4, ItemBuilder(Material.FEATHER).setName(ChatColor.GOLD.toString() + "Boost").build())
        player.health = player.maxHealth;
        player.fireTicks = 0;
        player.saturation = 20F;
        player.addPotionEffect(PotionEffect(PotionEffectType.SATURATION, 1, 100, false, false, false))
        player.teleport(Location(Bukkit.getWorld(this.worldName), 0.0, 65.0, 0.0));
        player.showBossBar(this.bossBar);
    }

    override fun prepare() {
        this.load();
        this.registerEventListener();
        this.generatePattern(TrigonometricNoise(), 0);
        val world: World? = Bukkit.getWorld(this.worldName);
        world!!.setSpawnLocation(Location(Bukkit.getWorld(this.worldName), 0.0, 65.0, 0.0));
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setGameRule(GameRule.DO_ENTITY_DROPS, false);
        world.setGameRule(GameRule.DO_FIRE_TICK, false);
        world.setGameRule(GameRule.DO_TRADER_SPAWNING, false);
        world.setGameRule(GameRule.DO_PATROL_SPAWNING, false);
        world.setGameRule(GameRule.DO_WARDEN_SPAWNING, false);
        world.setGameRule(GameRule.DO_TILE_DROPS, false);
        world.setGameRule(GameRule.DO_MOB_LOOT, false);
        world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        this.updateBossbar();
    }

    private fun generatePattern(noise: Noise, seed: Int) {
        val world: World? = Bukkit.getWorld(this.worldName);
        for (x in -this.bounds..this.bounds) {
            for (z in -this.bounds..this.bounds) {
                world?.setType(x, 64, z, Noise.map(noise, x, z, seed, this.bounds));
            }
        }
    }

    private fun removeAnyInvalidBlock() {
        val world: World? = Bukkit.getWorld(this.worldName);
        for (x in -this.bounds..this.bounds) {
            for (z in -this.bounds..this.bounds) {
                if (world?.getType(x, 64, z) != this.currentColor?.block)
                    world?.setType(x, 64, z, Material.AIR);
            }
        }
    }

    override fun applyConfiguration() {
        this.generatePattern(TrigonometricNoise(), 0);
    }

    override fun unregisterEventListener() {
        PlayerInteractEvent.getHandlerList().unregister(this);
        EntityDamageEvent.getHandlerList().unregister(this);
        FoodLevelChangeEvent.getHandlerList().unregister(this);
        InventoryClickEvent.getHandlerList().unregister(this);
        PlayerSwapHandItemsEvent.getHandlerList().unregister(this);
        PlayerDropItemEvent.getHandlerList().unregister(this);
    }

    private fun updateHotbar(player: Player) {
        for (slot in 0..8) {
            if (slot == 4) continue;
            player.inventory.setItem(
                slot,
                ItemBuilder(this.currentColor!!.block).setName(
                    Component.text(this.currentColor!!.displayName).color(this.currentColor!!.textColor)
                        .decoration(TextDecoration.BOLD, true).decoration(TextDecoration.ITALIC, false)
                ).build()
            );
        }
    }

    private fun updateBossbar() {
        if (this.isRunning) {
            if (this.secondsRemaining > 0) {
                this.bossBar.name(
                    /*Component.text("✔✔✔ ").color(NamedTextColor.GREEN)
                        .append(*/Component.text(this.currentColor!!.displayName)
                        .color(this.currentColor!!.textColor)/*)*/.decoration(TextDecoration.BOLD, true)
                    /*.append(Component.text(" ✔✔✔")).color(NamedTextColor.GREEN)*/
                );
                this.bossBar.progress(this.secondsRemaining.toFloat() / this.secondsToSearch.toFloat());
                this.bossBar.color(BossBar.Color.GREEN);
            } else {
                this.bossBar.name(
                    Component.text("\uD83D\uDFAD\uD83D\uDFAD\uD83D\uDFAD ").color(NamedTextColor.RED)
                        .append(Component.text(this.currentColor!!.displayName).color(this.currentColor!!.textColor))
                        .decoration(TextDecoration.BOLD, true)
                        .append(Component.text(" \uD83D\uDFAD\uD83D\uDFAD\uD83D\uDFAD")).color(NamedTextColor.RED)
                );
                this.bossBar.progress((this.secondsPause.toFloat() + this.secondsRemaining) / this.secondsPause.toFloat());
                this.bossBar.color(BossBar.Color.RED);
            }
        } else {
            if (!this::bossBar.isInitialized) {
                this.bossBar = BossBar.bossBar(
                    MiniMessage.miniMessage().deserialize("<rainbow>Blockparty</rainbow>")
                        .decoration(TextDecoration.BOLD, true), 1F, BossBar.Color.PURPLE, BossBar.Overlay.PROGRESS
                );
            } else {
                this.bossBar.name(
                    MiniMessage.miniMessage().deserialize("<rainbow>Blockparty</rainbow>")
                        .decoration(TextDecoration.BOLD, true)
                );
                this.bossBar.progress(1F);
                this.bossBar.color(BossBar.Color.PURPLE);
            }
        }
    }

    override fun start() {
        this.getPlayers().forEach { it.teleport(Location(Bukkit.getWorld(this.worldName), 0.0, 65.0, 0.0)); };
        this.isRunning = true;

        this.task = object : BukkitRunnable() {
            var i: Int = 0;
            override fun run() {
                when (this.i) {
                    0 -> {
                        currentColor = Color.values().random();
                        generatePattern(Noise.random(), ThreadLocalRandom.current().nextInt(0, 20));
                        getPlayers().forEach { player -> updateHotbar(player) }
                        secondsRemaining = secondsToSearch;
                    }

                    in 1..<secondsToSearch -> {
                        secondsRemaining--;
                    }

                    in secondsToSearch..<secondsToSearch + secondsPause -> {
                        removeAnyInvalidBlock();
                        secondsRemaining--;
                    }

                    else -> {
                        secondsRemaining--;
                        this.i = -1;
                    }
                }
                this.i++;
                updateBossbar();
            }
        }.runTaskTimer(this.framework.getFuxelSagt(), 0, 20);
    }

    override fun stop() {
        if (this::task.isInitialized) {
            this.task.cancel();
        }
        this.getPlayers().forEach { player -> player.inventory.clear() }
        this.isRunning = false;
    }

    override fun cleanup() {
        this.getPlayers().forEach { player -> player.hideBossBar(this.bossBar) }
    }

    @EventHandler
    fun onDamage(event: EntityDamageEvent) {
        event.isCancelled = true;
    }

    @EventHandler
    fun onHungerLoose(event: FoodLevelChangeEvent) {
        event.setFoodLevel(20);
    }

    @EventHandler
    fun onPlayerDropItem(event: PlayerDropItemEvent) {
        event.isCancelled = true;
    }

    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        if (!this.isPlayer(event.player) || !this.isRunning) return;

        val blockLocation = event.to.block.location;
        if (blockLocation.y < 60) {
            event.player.gameMode = org.bukkit.GameMode.SPECTATOR;
            this.addToDead(event.player);
            this.playerLoose(event.player);
            this.checkGameScore();
            return;
        }
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        event.setCancelled(true);
    }

    @EventHandler
    fun onPlayerSwapHandItems(event: PlayerSwapHandItemsEvent) {
        event.setCancelled(true);
    }

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        if (event.hand != EquipmentSlot.HAND || (event.action != Action.RIGHT_CLICK_AIR && event.action != Action.RIGHT_CLICK_BLOCK) || event.item?.type != Material.FEATHER || !this.isRunning) return;
        val player: Player = event.player;
        player.velocity = player.velocity.setY(1.0.coerceAtLeast(player.velocity.y + 1));
        event.item?.amount = 0;
    }


}