package examples.skowa.audio

import java.io.ByteArrayOutputStream
import java.lang.Integer.min
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.sound.sampled.*
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.log10
import kotlin.math.pow

object Receiver {

    const val FREQ_TRUE = 19000
    const val FREQ_FALSE = 18000
    private const val WIDTH = 300
    private const val frequency = 44100f
    private const val samplesNb = 256
    private const val T = 1.0 * samplesNb / frequency
    private val format = AudioFormat(frequency, 16, 1, true, true)
    var microphone: TargetDataLine

    init {
        microphone = AudioSystem.getTargetDataLine(format)
        val info = DataLine.Info(TargetDataLine::class.java, format)
        microphone = AudioSystem.getLine(info) as TargetDataLine
        microphone.open(format)
    }

    /**
     * Tries to receive a bit [rounds] times.
     * Returns 0/1 if a bit was received, -1 else.
     */
    fun receive(rounds: Int): Int {

        repeat(rounds) {
            val received = round()
            if (received != -1) return received
        }

        return -1
    }

    private fun round(): Int {


        try {
            val out = ByteArrayOutputStream()
            var numBytesRead: Int
            val bufferSize = microphone.bufferSize / 5
            val data = ByteArray(bufferSize)
            microphone.start()
            var bytesRead = 0
            try {
                while (bytesRead < samplesNb * 2) {
                    numBytesRead = microphone.read(data, 0, bufferSize)
                    bytesRead += numBytesRead
                    out.write(data, 0, min(numBytesRead, samplesNb * 2 - out.size()))
                }
                microphone.flush()
                microphone.stop()

                val array = out.toByteArray()
                val audioData = ByteBuffer.wrap(out.toByteArray())
                audioData.order(ByteOrder.BIG_ENDIAN)

                val bits = 16
                val max = 2.0.pow(bits - 1.0)
                val samples = DoubleArray(array.size * 8 / bits)
                for (i in samples.indices) samples[i] = audioData.short / max

                // Result analysis
                return analyse(samples)

            } catch (e: Exception) {
                e.printStackTrace()
            }

        } catch (e: Exception) {
        }

        return -1
    }

    private fun analyse(data: DoubleArray): Int {

        // Windowing
        for (i in data.indices) data[i] *= 0.5 * (1 - cos(2 * Math.PI * i / data.size))

        val f = FFT.fft(data.map { Complex(it, 0.0) }.toTypedArray())
        val maxDB = f.map { 10 * log10(it.re.pow(2) + it.im.pow(2)) }.withIndex().maxBy { it.value }

        maxDB?.let {
            val freq = it.index.div(T)
            if (abs(freq - FREQ_FALSE) < WIDTH) return 0
            if (abs(freq - FREQ_TRUE) < WIDTH) return 1
        }

        return -1
    }

}