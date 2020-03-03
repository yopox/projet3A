package fr.centralesupelec.db.audio

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.AudioTrack.MODE_STATIC
import kotlin.experimental.and
import kotlin.math.sin
import com.karlotoy.perfectune.instance.PerfectTune




object Sender {

    private var perfectTune = PerfectTune()


    /**
    private const val duration = 1
    private const val sampleRate = 44100
    private const val numSamples = (duration * sampleRate).toInt()
    private val trueSamples = DoubleArray(numSamples)
    private val falseSamples = DoubleArray(numSamples)

    private val trueSound = ByteArray(2 * numSamples)
    private val falseSound = ByteArray(2 * numSamples)

    init {
        for (i in 0 until numSamples) {
            trueSamples[i] = sin(2.0 * Math.PI * i / (sampleRate / Receiver.FREQ_TRUE))
            falseSamples[i] = sin(2.0 * Math.PI * i / (sampleRate / Receiver.FREQ_FALSE))
        }

        var idx = 0
        for (dVal in trueSamples) {
            val value = (dVal * 32767).toShort()
            trueSound[idx++] = (value and 0x00ff).toByte()
            trueSound[idx++] = ((value and 0xff00.toShort()).toInt()).ushr(16).toByte()
        }

        idx = 0
        for (dVal in falseSamples) {
            val value = (dVal * 32767).toShort()
            falseSound[idx++] = (value and 0x00ff).toByte()
            falseSound[idx++] = ((value and 0xff00.toShort()).toInt().shr(16)).toByte()
        }
    }

    fun playSound(value: Boolean) {
        val audioTrack = AudioTrack(
            AudioManager.STREAM_MUSIC,
            sampleRate, AudioFormat.CHANNEL_CONFIGURATION_MONO,
            AudioFormat.ENCODING_PCM_16BIT, numSamples,
            MODE_STATIC
        )
        if (value) {
            audioTrack.write(trueSound, 0, trueSound.size)
        } else {
            audioTrack.write(falseSound, 0, falseSound.size)
        }
        audioTrack.setVolume(3f)
        audioTrack.play()
    }
    **/

    fun playSound(value: Boolean) {
        if (value) {
            perfectTune.tuneFreq = Receiver.FREQ_TRUE.toDouble()
        } else {
            perfectTune.tuneFreq = Receiver.FREQ_FALSE.toDouble()
        }
        perfectTune.playTune()
        Thread.sleep(500)
        perfectTune.stopTune()
    }

}