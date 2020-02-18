package examples

import protocol.Tag
import protocol.Values
import protocol.Values.Companion.easyBitSet
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.ServerSocket
import java.net.Socket
import java.util.*

class LHTag(seed: Int) : Tag() {
    override val name = "[Tag]"
    override val privateKey = sha256(easyBitSet("1010"))
    override val ID = easyBitSet("111")

    private val client = Socket("localhost", 9991)
    private val server: Socket
    private val writer: ObjectOutputStream
    private val reader: ObjectInputStream

    init {
        writer = ObjectOutputStream(client.getOutputStream())
        val s = ServerSocket(9990)
        server = s.accept()
        reader = ObjectInputStream(server.getInputStream())
    }

    override fun genNB(): BitSet = sha256(easyBitSet("11011001"))

    override fun f_x(private: BitSet, b: BitSet): BitSet = sha256(Values.join(arrayOf(b, private)))

    override fun receive1(): Pair<BitSet, BitSet> {
        return reader.readObject() as Pair<BitSet, BitSet>
    }

    override fun send1(N_B: BitSet) {
        writer.writeObject(N_B)
    }

    override fun receive2(): Boolean {
        return reader.readObject() as Boolean
    }

    override fun send2(r_i: Boolean) {
        writer.writeObject(r_i)
    }

    override fun send3(value: Pair<BitSet, BitSet>) {
        writer.writeObject(value)
    }

    override fun receive3(): BitSet {
        val b = reader.readObject() as BitSet
        client.close()
        server.close()
        return b
    }
}