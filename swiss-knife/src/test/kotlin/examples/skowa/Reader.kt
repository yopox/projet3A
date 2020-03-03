package examples.skowa

import examples.localhost.sha256
import protocol.Reader
import protocol.Values
import protocol.Values.Companion.easyBitSet
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.ServerSocket
import java.net.Socket
import java.util.*

/**
 * SKoWA (Swiss-Knife over Wifi & Audio) Reader.
 */
class Reader(seed: Int) : Reader(seed) {
    override val name = "[Reader]"
    override val hashSize = 256

    private val db = arrayOf(
            easyBitSet("001") to sha256(easyBitSet("1101")),
            easyBitSet("111") to sha256(easyBitSet("1010")),
            easyBitSet("1010101") to sha256(easyBitSet("1110"))
    )
    private val server: Socket
    private val writer: ObjectOutputStream
    private val reader: ObjectInputStream

    init {
        val s = ServerSocket(9999)
        server = s.accept()
        writer = ObjectOutputStream(server.getOutputStream())
        reader = ObjectInputStream(server.getInputStream())
    }

    override fun sync2() {
        writer.writeObject(1)
        reader.readObject()
    }

    override fun genNA() = sha256(easyBitSet("100010100111"))

    override fun dbSearch(tB: BitSet, cpI: BitSet, nA: BitSet, nB: BitSet): Pair<BitSet, BitSet>? {
        for ((id, private) in db)
            if (f_x(private, Values.join(arrayOf(cpI, id, nA, nB))) == tB)
                return id to private
        return null
    }

    override fun f_x(private: BitSet, b: BitSet) = sha256(Values.join(arrayOf(b, private)))

    override fun tagNotFound() {}
    override fun accept(ID: BitSet) {}
    override fun reject(ID: BitSet) {}

    override fun send1(value: Pair<BitSet, BitSet>) {
        writer.writeObject(value)
    }

    override fun receive1(): BitSet {
        return reader.readObject() as BitSet
    }

    override fun send2(value: Boolean) {
        writer.writeObject(value)
    }

    override fun receive2(): Boolean {
        return reader.readObject() as Boolean
    }

    override fun receive3(): Pair<BitSet, BitSet> {
        return reader.readObject() as Pair<BitSet, BitSet>
    }

    override fun send3(value: BitSet) {
        writer.writeObject(value)
    }
}