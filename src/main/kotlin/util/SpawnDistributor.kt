package util

import org.bukkit.Location
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class SpawnDistributor {
    fun circleDistributor(center: Location, anzahlPunkte: Int, anzahlRadien: Int, abstand: Double): List<Location> {
        val locations = mutableListOf<Location>()

        for (r in 0 until anzahlRadien) {
            val radius = r * abstand
            val numLocationsOnCircle = anzahlPunkte * (r + 1)

            for (i in 0 until numLocationsOnCircle) {
                val angle = 2.0 * PI * (i.toDouble() / numLocationsOnCircle)
                val x = radius * cos(angle)
                val z = radius * sin(angle)
                locations.add(center.clone().add(x, 0.0, z))
            }
        }
        return locations
    }
}