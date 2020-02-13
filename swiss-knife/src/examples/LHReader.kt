package examples

import protocol.Reader
import protocol.Values
import protocol.Values.Companion.easyBitSet
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.ServerSocket
import java.net.Socket
import java.util.*

class LHReader(seed: Int) : Reader(seed) {
    override val name = "[Reader]"
    override fun hashSize(): Int = 256

    private val db = arrayOf(
            easyBitSet("001") to sha256(easyBitSet("1101")),
            easyBitSet("111") to sha256(easyBitSet("1010"))
    )
    private val client: Socket
    private val server: Socket
    private val writer: ObjectOutputStream
    private val reader: ObjectInputStream

    init {
        val s = ServerSocket(9991)
        server = s.accept()
        reader = ObjectInputStream(server.getInputStream())
        client = Socket("localhost", 9990)
        writer = ObjectOutputStream(client.getOutputStream())
    }

    override fun dbSearch(tB: BitSet, cpI: BitSet, nA: BitSet, nB: BitSet): Pair<BitSet, BitSet>? {
        for ((id, private) in db)
            if (f_x(private, Values.join(arrayOf(cpI, id, nA, nB))) == tB)
                return id to private
        return null
    }

    override fun tagNotFound() {

    }

    override fun genNA(): BitSet = sha256(easyBitSet("11010001"))

    override fun f_x(private: BitSet, b: BitSet) = sha256(Values.join(arrayOf(b, private)))

    override fun accept(ID: BitSet) {
    }

    override fun reject(ID: BitSet) {
    }

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

        client.close()
        server.close()
    }
}