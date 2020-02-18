package examples

import java.security.MessageDigest
import java.util.*

fun main() {

    val reader = T1()
    val tag = T2()

    reader.start()
    Thread.sleep(100)
    tag.start()

}

fun sha256(bs: BitSet): BitSet {
    val crypt = MessageDigest.getInstance("SHA-256")
    crypt.reset()
    return BitSet.valueOf(crypt.digest(bs.toByteArray()))
}