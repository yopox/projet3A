package fr.centralesupelec.db.skowa

import java.security.MessageDigest
import java.util.*

fun sha256(value: BitSet): BitSet {
    val crypt = MessageDigest.getInstance("SHA-256")
    crypt.reset()
    return BitSet.valueOf(crypt.digest(value.toByteArray()))
}