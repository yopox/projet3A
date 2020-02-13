package examples

import protocol.Values
import java.security.MessageDigest
import java.util.*

fun main(args: Array<String>) {

    val reader = T1()
    val tag = T2()

    reader.start()
    tag.start()

}

fun sha256(bs: BitSet): BitSet {
    val crypt = MessageDigest.getInstance("SHA-256")
    crypt.reset()
    return BitSet.valueOf(crypt.digest(bs.toByteArray()))
}