package fr.centralesupelec.db.audio

import com.karlotoy.perfectune.instance.PerfectTune

object Sender {

    private var perfectTune = PerfectTune()

    fun playSound(value: Boolean) {
        if (value) {
            perfectTune.tuneFreq = Receiver.FREQ_TRUE.toDouble()
        } else {
            perfectTune.tuneFreq = Receiver.FREQ_FALSE.toDouble()
        }
        perfectTune.playTune()
        Thread.sleep(300)
        perfectTune.stopTune()
    }

}