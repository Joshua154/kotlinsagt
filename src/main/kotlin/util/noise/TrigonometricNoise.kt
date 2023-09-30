package util.noise;

import kotlin.math.cos;
import kotlin.math.sin;

class TrigonometricNoise : Noise {

    override fun generate(x: Int, z: Int, bounds: Int, seed: Int): Double {
        val xNormalized = (x + seed).toDouble() / (bounds / 4);
        val zNormalized = (z + seed).toDouble() / (bounds / 4);

        val pattern = sin(xNormalized) + cos(zNormalized);

        val patternNormalized = (pattern + 2.0) / 4.0;
        return patternNormalized * 2.0 - 1.0;
    }

}