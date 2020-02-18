package fr.centralesupelec.db.skowa

import android.content.Context
import android.util.Log
import fr.centralesupelec.db.QuietUtils
import protocol.Tag
import protocol.Values
import protocol.Values.Companion.easyBitSet
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.Socket
import java.util.*

class DroidTag : Tag() {
    override val name = "DroidTag"
    override val privateKey = sha256(easyBitSet("1110"))
    override val ID = easyBitSet("1010101")

    private lateinit var client: Socket
    private lateinit var writer: ObjectOutputStream
    private lateinit var reader: ObjectInputStream
    private lateinit var context: Context

    fun init(dstAddress: String, dstPort: Int, context: Context) {
        log("Connecting to $dstAddress on port $dstPort")
        client = Socket(dstAddress, dstPort)
        log("Connected")
        writer = ObjectOutputStream(client.getOutputStream())
        reader = ObjectInputStream(client.getInputStream())
        this.context = context
        start()
    }

    override fun genNB() = sha256(easyBitSet("00101011"))

    override fun f_x(private: BitSet, b: BitSet) = sha256(Values.join(arrayOf(b, private)))

    override fun receive1(): Pair<BitSet, BitSet> {
        return reader.readObject() as Pair<BitSet, BitSet>
    }

    override fun send1(N_B: BitSet) {
        writer.writeObject(N_B)
    }

    override fun receive2(): Boolean {
        val received = QuietUtils.receive(context, 1, 0)
        return received == "1"
    }

    override fun send2(r_i: Boolean) {
        QuietUtils.send(if (r_i) "1" else "0")
    }

    override fun send3(value: Pair<BitSet, BitSet>) {
        writer.writeObject(value)
    }

    override fun receive3(): BitSet {
        val b = reader.readObject() as BitSet
        client.close()
        return b
    }

    override fun log(s: String) {
        Log.i(name, s)
    }
}