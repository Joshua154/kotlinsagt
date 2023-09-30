package util.noise

class StripeNoise : Noise {
    override fun generate(x: Int, z: Int, bounds: Int, seed: Int): Double {
        return (((x.toDouble() * 2 + 200 + seed + bounds) / bounds) % 1) * 2.0 - 1.0;
    }

}