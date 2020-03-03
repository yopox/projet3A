package examples.localhost

import kotlin.random.Random

class T1 : Thread() {
    override fun run() {
        val reader = LHReader(Random(System.currentTimeMillis()).nextInt())
        reader.start()
    }
}