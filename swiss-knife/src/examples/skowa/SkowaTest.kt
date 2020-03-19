package examples.skowa

import examples.skowa.audio.Receiver
import examples.skowa.audio.Sender
import kotlin.random.Random

class SkowaTest {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            // Use [receiveTest] to test frequency recognition
            //receiveTest()

            // SKoWA receiver
            Reader((0..9999999).random()).start()
        }

        fun receiveTest() {
            repeat(10) {
                println(Receiver.receive(10))
                Thread.sleep(800)
            }
        }
    }


}

