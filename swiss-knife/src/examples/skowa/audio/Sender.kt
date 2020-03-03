package examples.skowa.audio

import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.LineUnavailableException
import kotlin.math.sin


object Sender {
    private const val SAMPLE_RATE = 16 * 1024
    private val TRUE = createSinWaveBuffer(440.0, 500)
    private val FALSE = createSinWaveBuffer(330.0, 500)

    fun play(value: Boolean) {
        val af = AudioFormat(SAMPLE_RATE.toFloat(), 8, 1, true, true)
        try {
            val line = AudioSystem.getSourceDataLine(af)
            line.open(af, SAMPLE_RATE)
            line.start()
            val toneBuffer = if (value) TRUE else FALSE
            //println(toneBuffer.toString() + " " + toneBuffer.size)
            line.write(toneBuffer, 0, toneBuffer.size)
            line.drain()
            line.close()
        } catch (e: LineUnavailableException) {
            //println(e.localizedMessage)
        }
    }

    private fun createSinWaveBuffer(freq: Double, ms: Int): ByteArray {
        val output = ByteArray((ms * SAMPLE_RATE / 1000))
        val period = SAMPLE_RATE.toDouble() / freq
        for (i in output.indices) {
            val angle = 2.0 * Math.PI * i / period
            output[i] = (sin(angle) * 127f).toByte()
        }
        return output
    }
}