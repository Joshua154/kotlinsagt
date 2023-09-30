package util.noise

class DiagonalStripeNoise : Noise {

    override fun generate(x: Int, z: Int, bounds: Int, seed: Int): Double {
        val xNormalized = 1.5 * (x.toDouble() + bounds + seed) / (bounds / 2);
        val zNormalized = 1.5 * (z.toDouble() + bounds + seed) / (bounds / 2);

        val pattern = xNormalized + zNormalized;

        val patternNormalized = ((pattern + 2.0) / 4.0) % 1;
        return patternNormalized * 2.0 - 1.0;
    }

}