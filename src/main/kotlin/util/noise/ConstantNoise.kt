package util.noise;

class ConstantNoise(private val constant: Double) : Noise {

    override fun generate(x: Int, z: Int, bounds: Int, seed: Int): Double {
        return this.constant;
    }

}