package fr.centralesupelec.db

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.quietmodem.Quiet.*
import java.io.IOException

object QuietUtils {
    var receiver: FrameReceiver? = null
    var transmitter: FrameTransmitter? = null
    val profile: String = "ultrasonic"

    private fun hasRecordAudioPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission(perm: Int, activity: Activity) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.RECORD_AUDIO),
            perm
        )
    }

    fun showMissingAudioPermissionToast(context: Context) {
        val duration = Toast.LENGTH_SHORT
        val toast = Toast.makeText(context, R.string.missing_audio_permission, duration)
        toast.show()
    }

    fun setupTransmitter(context: Context) {
        val transmitterConfig: FrameTransmitterConfig
        try {
            transmitterConfig = FrameTransmitterConfig(
                context, profile
            )
            transmitter = FrameTransmitter(transmitterConfig)
        } catch (e: IOException) {
            println("could not build configs")
            throw RuntimeException(e)
        } catch (e: ModemException) {
            println("could not set up transmitter")
            throw RuntimeException(e)
        }
    }

    fun checkReceiverPermission(activity: Activity, context: Context, perm: Int) {
        if (hasRecordAudioPermission(context)) {
            setupReceiver(context)
        } else {
            requestPermission(perm, activity)
        }

    }

    fun setupReceiver(context: Context) {
        val receiverConfig: FrameReceiverConfig?
        try {
            receiverConfig = FrameReceiverConfig(
                context,
                profile
            )
            receiver = FrameReceiver(receiverConfig)
        } catch (e: IOException) {
            println("could not build configs")
        }


        try {
            receiver!!.setBlocking(1, 500000)
        } catch (e: ModemException) {
            println("could not set up receiver")
        }
    }
    fun send(payload: String) : Boolean {
        return try {
            transmitter!!.send((if (payload.isEmpty()) "Test" else payload).toByteArray())
            true
        } catch (e: IOException) {
            println("Your message '$payload' might be too long or the transmit queue full")
            false
        }
    }

    fun receive(context: Context, seconds: Long, nano: Long) : String {
        receiver!!.setBlocking(seconds,nano)
        var buf = ByteArray(1024)
        var recvLen: Long = 0
        try {
            recvLen = receiver!!.receive(buf)
        } catch (e: IOException) {
            println("read timed out")
            buf = "".toByteArray()
        }

        return if (buf.size != recvLen.toInt()) {
            buf.toString(charset("UTF-8"))
        } else {
            context.getString(R.string.reception_error)
        }
    }
}