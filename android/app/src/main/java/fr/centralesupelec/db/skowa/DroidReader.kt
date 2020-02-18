package fr.centralesupelec.db.skowa

import android.content.Context
import android.util.Log
import android.widget.TextView
import fr.centralesupelec.db.QuietUtils
import protocol.Reader
import protocol.Values
import protocol.Values.Companion.easyBitSet
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import java.util.*

class DroidReader(seed: Int, val textView: TextView) : Reader(seed) {
    override val name = "DroidReader"
    override val hashSize = 256

    private lateinit var server: Socket
    private lateinit var writer: ObjectOutputStream
    private lateinit var reader: ObjectInputStream
    private lateinit var context: Context
    private val db = arrayOf(
        easyBitSet("0001001") to sha256(easyBitSet("1101")),
        easyBitSet("1010101") to sha256(easyBitSet("1110"))
    )

    fun init(port: Int, context: Context) {
        log("Init")
        val s = ServerSocket(port, 0, InetAddress.getByName("172.17.3.14"))
        server = s.accept()
        log("Socket accepted")
        reader = ObjectInputStream(server.getInputStream())
        writer = ObjectOutputStream(server.getOutputStream())
        this.context = context
        start()
    }

    override fun genNA() = sha256(easyBitSet("11010100"))

    override fun dbSearch(tB: BitSet, cpI: BitSet, nA: BitSet, nB: BitSet): Pair<BitSet, BitSet>? {
        for ((id, private) in db)
            if (f_x(private, Values.join(arrayOf(cpI, id, nA, nB))) == tB)
                return id to private
        return null
    }

    override fun f_x(private: BitSet, b: BitSet): BitSet = sha256(Values.join(arrayOf(b, private)))

    override fun tagNotFound() = Unit
    override fun accept(ID: BitSet) {
        textView.text = "Tag accepted\n\nID : ${Values.bitSetToStr(ID).takeLast(8)}\nR0 : ${Values.bitSetToStr(this.values.R0).takeLast(Values.m)}\nR1 : ${Values.bitSetToStr(this.values.R1).takeLast(Values.m)}\nC1 : ${Values.bitSetToStr(this.values.c1).takeLast(Values.m)}\nC2 : ${Values.bitSetToStr(this.values.c2).takeLast(Values.m)}"
    }
    override fun reject(ID: BitSet) {
        textView.text = "Tag rejected\n\nID : ${Values.bitSetToStr(ID).takeLast(8)}\nR0 : ${Values.bitSetToStr(this.values.R0).takeLast(Values.m)}\nR1 : ${Values.bitSetToStr(this.values.R1).takeLast(Values.m)}\nC1 : ${Values.bitSetToStr(this.values.c1).takeLast(Values.m)}\nC2 : ${Values.bitSetToStr(this.values.c2).takeLast(Values.m)}"
    }

    override fun send1(value: Pair<BitSet, BitSet>) {
        writer.writeObject(value)
    }

    override fun receive1(): BitSet {
        return reader.readObject() as BitSet
    }

    override fun send2(value: Boolean) {
        QuietUtils.send(if (value) "1" else "0")
    }

    override fun receive2(): Boolean {
        val received = QuietUtils.receive(context, 1, 0)
        log("Received $received")
        return received == "1"
    }

    override fun receive3(): Pair<BitSet, BitSet> {
        return reader.readObject() as Pair<BitSet, BitSet>
    }

    override fun send3(value: BitSet) {
        writer.writeObject(value)
        server.close()
    }

    override fun log(s: String) {
        Log.i(name, s)
    }

}