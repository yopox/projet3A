import java.util.*
import kotlin.random.Random

abstract class Server(seed: Int) {

    private val random = Random(seed)
    private var values = Values()

    fun start() {
        values = Values()
        slowPhase()
    }

    private fun slowPhase() {
        // N_A computation
        values.N_A = BitSet.valueOf(longArrayOf(random.nextLong()))

        // m random positions
        val s: Set<Int> = setOf()
        while (s.size < Values.m)
            s.plus(random.nextInt(256))
        s.forEach { values.d.set(it) }

        // Send N_A and d
        send1(Pair(values.N_A, values.d))

        // Receive N_B
        values.N_B = receive1()

        rapidPhase()
    }

    private inline fun <R> measure(block: () -> R): Pair<R, Long> {
        val start = System.currentTimeMillis()
        val result = block()
        return result to (System.currentTimeMillis() - start)
    }

    private fun rapidPhase() {
        for (i in 0 until Values.m) {
            // Send cI
            val cI = random.nextFloat() >= 0.5
            if (cI) values.c1.set(i)
            send2(cI)

            // Receive r'I
            val (rpI, dt) = measure { receive2() }
            if (rpI) values.c2.set(i)
            values.Dt[i] = dt
        }

        endPhase()
    }

    private fun endPhase() {
        // Receive tB & c'I
        val (tB, cpI) = receive3()

        // Check ID
        val (ID, privateKey) = dbSearch(tB, cpI, values.N_A, values.N_B)

        // R computation
        val a = f_x(privateKey, Values.join(arrayOf(Values.C_B, values.N_B)))
        values.computeR(a, privateKey)

        // Errors computation
        var errors = 0

        for (i in 0 until Values.m) {
            // First class errors : Wrong bit received
            if (cpI[i] != values.c1[i])
                errors += 1
            else {
                // Second class errors : Wrong response
                when (values.c1[i]) {
                    true -> if (cpI[i] != values.R1[i]) errors += 1
                    false -> if (cpI[i] != values.R0[i]) errors += 1
                }

                // Third class errors : Response too late
                if (values.Dt[i] >= Values.tMax)
                    errors += 1
            }
        }

        // Accept or reject
        if (errors < Values.T) {
            // Tag accepted
            accept(ID)
        } else {
            // Tag rejected
            reject(ID)
        }

        // Send tA
        val tA = f_x(privateKey, values.N_B)
        send3(tA)

    }

    abstract fun dbSearch(tB: BitSet, cpI: BitSet, nA: BitSet, nB: BitSet): Pair<BitSet, BitSet>
    abstract fun f_x(private: BitSet, b: BitSet): BitSet

    abstract fun accept(ID: BitSet)
    abstract fun reject(ID: BitSet)

    abstract fun send1(value: Pair<BitSet, BitSet>)
    abstract fun receive1(): BitSet

    abstract fun send2(value: Boolean)
    abstract fun receive2(): Boolean

    abstract fun receive3(): Pair<BitSet, BitSet>
    abstract fun send3(value: BitSet)

}