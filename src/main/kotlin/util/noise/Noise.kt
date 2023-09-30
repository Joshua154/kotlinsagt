package util.noise;

import org.bukkit.Material

interface Noise {

    fun generate(x: Int, z: Int, bounds: Int, seed: Int): Double;

    companion object {

        fun random(): Noise {
            return listOf(
                DiagonalStripeNoise(),
                RandomNoise(),
                StripeNoise(),
                TrigonometricNoise()
            ).random();
        }

        fun map(noiseFunction: Noise, x: Int, z: Int, seed: Int, bounds: Int): Material {
            val noiseValue = noiseFunction.generate(x, z, bounds, seed);
            return when {
                noiseValue < -0.875 -> Material.BLACK_CONCRETE
                noiseValue < -0.75 -> Material.BLUE_CONCRETE
                noiseValue < -0.625 -> Material.BROWN_CONCRETE
                noiseValue < -0.5 -> Material.CYAN_CONCRETE
                noiseValue < -0.375 -> Material.GRAY_CONCRETE
                noiseValue < -0.25 -> Material.GREEN_CONCRETE
                noiseValue < -0.125 -> Material.LIGHT_BLUE_CONCRETE
                noiseValue < 0.0 -> Material.LIGHT_GRAY_CONCRETE
                noiseValue < 0.125 -> Material.LIME_CONCRETE
                noiseValue < 0.25 -> Material.MAGENTA_CONCRETE
                noiseValue < 0.375 -> Material.ORANGE_CONCRETE
                noiseValue < 0.5 -> Material.PINK_CONCRETE
                noiseValue < 0.625 -> Material.PURPLE_CONCRETE
                noiseValue < 0.75 -> Material.RED_CONCRETE
                noiseValue < 0.875 -> Material.WHITE_CONCRETE
                else -> Material.YELLOW_CONCRETE
            }
        }
    }

}