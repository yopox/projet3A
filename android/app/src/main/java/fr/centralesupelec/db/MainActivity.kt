package fr.centralesupelec.db

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.quietmodem.Quiet.*
import java.io.IOException

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_schedule.*
import org.quietmodem.Quiet.ModemException
import org.quietmodem.Quiet.NetworkInterfaceConfig
import org.quietmodem.Quiet.FrameReceiverConfig
import org.quietmodem.Quiet.FrameTransmitterConfig
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import java.util.*
import org.quietmodem.Quiet.LoopbackNetworkInterface
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import org.quietmodem.Quiet.FrameTransmitter
import org.quietmodem.Quiet.FrameReceiver
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T












class MainActivity : AppCompatActivity() {
    //private var intf: NetworkInterface? = null
    private var receiver: FrameReceiver? = null
    private var transmitter: FrameTransmitter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        bottom_navigation.setOnNavigationItemSelectedListener(navigationListener)
        openFragment(TodayFragment.newInstance())
        println("Creating Network Interface")
        createNetworkInterface()
    }

    private val navigationListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_cours -> {
                toolbar.title = getString(R.string.course)
                val coursFragment = TodayFragment.newInstance()
                openFragment(coursFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_edt -> {
                toolbar.title = getString(R.string.schedule)
                val edtFragment = ScheduleFragment.newInstance()
                openFragment(edtFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_profil -> {
                toolbar.title = getString(R.string.profile)
                val profilFragment = ProfileFragment.newInstance()
                openFragment(profilFragment)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    fun createNetworkInterface() {
//        var receiverConfig: FrameReceiverConfig? = null
//        var transmitterConfig: FrameTransmitterConfig? = null
//
//        try {
//            transmitterConfig = FrameTransmitterConfig(
//                this,
//                "audible-7k-channel-0"
//            )
//            receiverConfig = FrameReceiverConfig(
//                this,
//                "audible-7k-channel-0"
//            )
//        } catch (e: IOException) {
//            println("could not build configs")
//
//        }
//
//
//        var conf: NetworkInterfaceConfig? = null
//        try {
//            conf = NetworkInterfaceConfig(
//                receiverConfig,
//                transmitterConfig,
//                InetAddress.getByName("192.168.0.3"),
//                InetAddress.getByName("255.255.255.0"),
//                InetAddress.getByName("192.168.0.1")
//            )
//            println(conf.localAddress)
//
//        } catch (e: IOException) {
//            println("could not build network config")
//        }
//
//
//        try {
//            intf = NetworkInterface(conf)
//        } catch (e: ModemException) {
//            println("network interface threw exception")
//        }
        var receiverConfig: FrameReceiverConfig? = null
        var transmitterConfig: FrameTransmitterConfig? = null

        try {
            transmitterConfig = FrameTransmitterConfig(
                this,
                "audible-7k-channel-0"
            )
            receiverConfig = FrameReceiverConfig(
                this,
                "audible-7k-channel-0"
            )
        } catch (e: IOException) {
            println("could not build configs")
        }


        try {
            receiver = FrameReceiver(receiverConfig)
            transmitter = FrameTransmitter(transmitterConfig)
        } catch (e: ModemException) {
            println("could not set up receiver/transmitter")
        }


    }

    fun send(view: View) {
        val payload = "Hello, World!"
        try {
            transmitter!!.send(payload.toByteArray())
        } catch (e: IOException) {
            println("our message might be too long or the transmit queue full")
        }

//        var s: Socket? = null
//        try {
//            s = Socket()
//            s.bind(InetSocketAddress("192.168.0.3", 0))
//        } catch (e: IOException) {
//            println("new socket failed")
//        }
//
//        try {
//            s!!.connect(InetSocketAddress("192.168.0.3", 5678))
//        } catch (e: IOException) {
//            println(e.message)
//            println("connect failed")
//        }
//
//
//        val send = "Hello, World!".toByteArray()
//        val os = s?.outputStream
//
//        try {
//            os?.write(send)
//        } catch (e: IOException) {
//            println("exception sending on Socket")
//        }
//
//        try {
//            s?.close()
//        } catch (e: IOException) {
//            println("close failed")
//        }

    }

    fun receive(view: View) {
        receiver!!.setBlocking(5, 0);
        var buf = ByteArray(1024)
        var recvLen: Long = 0
        try {
            recvLen = receiver!!.receive(buf)
        } catch (e: IOException) {
            println("read timed out")
            buf = "".toByteArray()
        }

        if (buf.isNotEmpty()) {
            received.text = buf.toString()
        } else {
            received.text = "Error"
        }

//        val listener = Thread(Runnable {
//            var s: ServerSocket? = null
//            try {
//                s = ServerSocket(5678,1,InetAddress.getByName("192.168.0.3"))
//                println(s.inetAddress)
//            } catch (e: IOException) {
//                println("exception creating ServerSocket")
//            }
//            val recv = ByteArray(800)
//
//            var peer: Socket? = null
//            try {
//                peer = s?.accept()
//            } catch (e: IOException) {
//                println("accept failed")
//            }
//
//
//            val `is` = peer!!.inputStream
//
//            var recvLen = 0
//            try {
//                recvLen = `is`.read(recv)
//            } catch (e: IOException) {
//                println("read failed")
//            }

//
//            try {
//                peer.close()
//                s?.close()
//                println("server socket closed !")
//            } catch (e: IOException) {
//                println("close failed")
//            }
//
//            println(recv.toString())
//
//            if (recvLen == recv.size) {
//                received.text = recv.toString()
//            }
//        })
//        listener.start()
    }

}
