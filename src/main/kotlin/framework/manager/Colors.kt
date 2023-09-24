package framework.manager

import net.kyori.adventure.text.format.NamedTextColor

enum class Colors(val prefix: String, val suffix: String, val namedColor: NamedTextColor) {
    PLAYER("<gradient:#00cc00:#00cc99>", "</gradient>", NamedTextColor.GREEN),
    SPECTATOR("<gradient:#999999:#cccccc>", "</gradient>", NamedTextColor.GRAY),
    FUXEL("<gradient:#ffb84e:#ff724e>", "</gradient>", NamedTextColor.GOLD),
    FUXELSAGT("<gradient:#ffb84e:#ff724e>", "</gradient>", NamedTextColor.GOLD),
    OTHER("<dark_gray>", "</dark_gray>", NamedTextColor.DARK_GRAY)
}