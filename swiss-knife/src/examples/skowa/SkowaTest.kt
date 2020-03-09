package examples.skowa

import examples.skowa.audio.Receiver
import examples.skowa.audio.Sender
import kotlin.random.Random

class SkowaTest {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Reader((0..9999999).random()).start()
        }

        fun receiveTest() {
            repeat(10) {
                println(Receiver.receive(10))
                Thread.sleep(200)
            }
        }
    }


}

