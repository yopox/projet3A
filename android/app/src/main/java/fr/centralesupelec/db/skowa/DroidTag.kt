package fr.centralesupelec.db.skowa

import android.content.Context
import android.util.Log
import fr.centralesupelec.db.audio.Receiver
import fr.centralesupelec.db.audio.Sender
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

    fun init(dstAddress: String) {
        log("Connecting to $dstAddress on port 9999")
        client = Socket(dstAddress, 9999)
        log("Connected")
        writer = ObjectOutputStream(client.getOutputStream())
        reader = ObjectInputStream(client.getInputStream())
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
        return Receiver.receive(5) == 1
        //return reader.readObject() as Boolean
    }

    override fun send2(r_i: Boolean) {
        Sender.playSound(r_i)
        //writer.writeObject(r_i)
    }

    override fun sync2() {
        reader.readObject()
        Log.i(name, "READ")
        writer.writeObject(1)
        Log.i(name, "WRITE")
    }

    override fun send3(value: Pair<BitSet, BitSet>) {
        writer.writeObject(value)
    }

    override fun receive3(): BitSet {
        val b = reader.readObject() as BitSet
        return b
    }

    override fun log(s: String) {
        Log.i(name, s)
    }
}