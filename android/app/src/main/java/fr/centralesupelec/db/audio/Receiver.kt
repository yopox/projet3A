package fr.centralesupelec.db.audio

import android.media.AudioFormat.CHANNEL_IN_MONO
import android.media.AudioFormat.ENCODING_PCM_16BIT
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.*

object Receiver {

    private val mainBuffer = ByteArrayOutputStream()
    private const val audioEncoding = ENCODING_PCM_16BIT
    private const val audioConfig = CHANNEL_IN_MONO
    private const val frequency = 44100
    private const val samplesNb = 512
    private const val T = 1.0 * samplesNb / frequency

    private var bufferSize = AudioRecord.getMinBufferSize(frequency, audioConfig, audioEncoding)
    private lateinit var recorder: AudioRecord

    const val FREQ_TRUE = 440
    const val FREQ_FALSE = 330
    private const val WIDTH = 30

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

    /**
     * Actual recording.
     */
    private fun round(): Int {
        recorder = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            frequency,
            audioConfig,
            audioEncoding,
            bufferSize
        )

        // Start recording
        try {
            recorder.startRecording()
        } catch (e: IOException) {
            Log.e("RECEIVER", "startRecording() failed")
            return -1
        }

        mainBuffer.reset()
        val readBuffer = ByteArray(bufferSize)

        // Record samples
        while (mainBuffer.size() < samplesNb * 2) {
            val bytesRead = recorder.read(readBuffer, 0, bufferSize)
            mainBuffer.write(readBuffer, 0, min(bytesRead, samplesNb * 2 - mainBuffer.size()))
        }

        recorder.stop()
        recorder.release()

        val array = mainBuffer.toByteArray()
        val bb = ByteBuffer.wrap(array)
        bb.order(ByteOrder.LITTLE_ENDIAN)

        val bits = 16
        val max = 2.0.pow(bits - 1.0)
        val samples = DoubleArray(array.size * 8 / bits)
        for (i in samples.indices) samples[i] = bb.short / max

        // Result analysis
        return analyse(samples)
    }

    /**
     * Runs FFT on the recording.
     */
    private fun analyse(data: DoubleArray): Int {
        val signal = data.clone()

        // Mean filter
        for (i in signal.indices) if (i > 0)
            signal[i] = (signal[i-1] + signal[i]) / 2.0

        // Windowing
        for (i in signal.indices) signal[i] *= 0.5 * (1 - cos(2 * Math.PI * i / signal.size))

        val f = FFT.fft(signal.map { Complex(it, 0.0) }.toTypedArray())
        val maxDB = f.copyOfRange(0, samplesNb / 4).map { 10 * log10(it.re.pow(2) + it.im.pow(2)) }.withIndex().maxBy { it.value }

        // text.text = "${maxFreq?.index?.div(T)} Hz — ${maxFreq?.value} dB"
        // println("${maxFreq?.index?.div(T)} Hz — ${maxFreq?.value} dB")

        maxDB?.let {
            if (it.value >= 2) {
                val freq = it.index.div(T)
                if (abs(freq - FREQ_FALSE) < WIDTH) return 0
                if (abs(freq - FREQ_TRUE) < WIDTH) return 1
            }
        }

        return -1
    }

}