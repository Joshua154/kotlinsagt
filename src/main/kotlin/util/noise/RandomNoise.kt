package util.noise;

import java.util.concurrent.ThreadLocalRandom;

class RandomNoise : Noise {

    override fun generate(x: Int, z: Int, bounds: Int, seed: Int): Double {
        return ThreadLocalRandom.current().nextDouble(-1.0, 1.0);
    }

}