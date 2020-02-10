import java.util.*

class Values {

    companion object {
        val m = 10
        val T = 2
        val C_B = BitSet.valueOf(longArrayOf(1529623414373657642))
        val tMax = 1000

        fun join(bitsets: Array<BitSet>): BitSet {
            var b1 = BitSet()
            for (b in bitsets) {
                // New BitSet of the correct size
                val b2 = BitSet(b.size() + b1.size())

                // b copy
                b2.or(b)

                // b1 copy
                for (i in 0 until b1.size())
                    if (b1[i])
                        b2.set(i + b.size())
                b1 = b2
            }
            return b1
        }
    }

    // Slow phase
    var N_A = BitSet()
    var N_B = BitSet()
    var d = BitSet()
    var R0 = BitSet(m)
    var R1 = BitSet(m)

    // Rapid phase
    var c1 = BitSet(m)
    var c2 = BitSet(m)
    var Dt = Array(m) { 0.toLong() }

    // End phase
    var t_B = 0
    var t_A = 0

    fun computeR(a: BitSet, privateKey: BitSet) {

        // Z computation
        val Z0: BitSet = a.clone() as BitSet
        val Z1: BitSet = a.clone() as BitSet
        Z1.xor(privateKey)

        // R computation
        var j = 0
        for (i in 0 until m) {
            j = d.nextSetBit(j)
            R0[i].or(Z0[j])
            R1[i].or(Z1[j])
        }
    }

}