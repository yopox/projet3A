package skowa

import fr.yopox.swiss_knife.Reader
import fr.yopox.swiss_knife.Values
import fr.yopox.swiss_knife.Values.Companion.easyBitSet
import skowa.audio.Receiver
import skowa.audio.Sender
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.ServerSocket
import java.net.Socket
import java.util.*

/**
 * SKoWA (Swiss-Knife over Wifi & Audio) Reader.
 */
class SkowaReader(seed: Int) : Reader(seed) {
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

    override fun genNA() = sha256(easyBitSet(IntArray(16).map { (0..1).random() }.joinToString()))

    override fun dbSearch(tB: BitSet, cpI: BitSet, nA: BitSet, nB: BitSet): Pair<BitSet, BitSet>? {
        for ((id, private) in db)
            if (f_x(private, Values.join(cpI, id, nA, nB)) == tB)
                return id to private
        return null
    }

    override fun f_x(private: BitSet, b: BitSet) = sha256(Values.join(sha256(Values.join(b, private)), private))

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
        Sender.play(value)
    }

    override fun receive2(): Boolean {
        val bit = Receiver.receive(2)
        return bit == 1
    }

    override fun receive3(): Pair<BitSet, BitSet> {
        return reader.readObject() as Pair<BitSet, BitSet>
    }

    override fun send3(value: BitSet) {
        writer.writeObject(value)
    }
}