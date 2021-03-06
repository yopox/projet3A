package skowa

import skowa.audio.Receiver
import java.security.MessageDigest
import java.util.*

class SkowaTest {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            // Object init
            Receiver.receive(1)

            // Use [receiveTest] to test frequency recognition
            //receiveTest()

            // SKoWA receiver
            SkowaReader((0..9999999).random()).start()
        }

        fun receiveTest() {
            repeat(10) {
                println(Receiver.receive(10))
                Thread.sleep(800)
            }
        }
    }

}

fun sha256(bs: BitSet): BitSet {
    val crypt = MessageDigest.getInstance("SHA-256")
    crypt.reset()
    return BitSet.valueOf(crypt.digest(bs.toByteArray()))
}
