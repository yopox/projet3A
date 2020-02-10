package fr.centralesupelec.db

import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_schedule.*
import kotlin.system.measureNanoTime


class MainActivity : AppCompatActivity() {

    private val MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        bottom_navigation.setOnNavigationItemSelectedListener(navigationListener)
        openFragment(ScheduleFragment.newInstance())
    }

    override fun onStart() {
        super.onStart()
        println("Creating Network Interfaces")
        QuietUtils.checkReceiverPermission(
            this, applicationContext,
            MY_PERMISSIONS_REQUEST_RECORD_AUDIO
        )
        QuietUtils.setupTransmitter(this)
    }

    override fun onStop() {
        super.onStop()
        QuietUtils.receiver!!.close()
        QuietUtils.transmitter!!.close()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_RECORD_AUDIO -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    QuietUtils.setupReceiver(this)
                } else {
                    QuietUtils.showMissingAudioPermissionToast(this)
                    finish()
                }
            }
        }
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

    fun send(view: View) {
        val payload = til.editText!!.text.toString()
        QuietUtils.send(payload)
    }

    fun receive(view: View) {
        received.text = QuietUtils.receive(this, 5, 0)
    }

    fun pingPongEmitter(view: View) {
        pingTV.text = ""
        pongTV.text = ""
        val payload = "ping"
        val start = System.currentTimeMillis()
        if (QuietUtils.send(payload)) {
            println("Emitter emitted Ping")
            pingTV.text = getString(R.string.ping_success_sent)
            pingTV.setTextColor(getColor(R.color.colorAccent))
        } else {
            pingTV.text = getString(R.string.send_error)
            pingTV.setTextColor(getColor(R.color.colorPrimaryDark))
        }
        val receivedText = QuietUtils.receive(this, 5, 0)
        println("Emitter got '$receivedText' (should be pong)")
        if ("pong" in receivedText) {
            val elapsed = System.currentTimeMillis() - start
            val distance = elapsed * 340/2000
            timeTV.text = "$distance m"
            pongTV.text = getString(R.string.pong_succes_received)
            pongTV.setTextColor(getColor(R.color.colorAccent))
        } else {
//            pongTV.text = getString(R.string.pong_error_received_retry)
//            pongTV.setTextColor(getColor(R.color.colorSecondary))
            pongTV.text = getString(R.string.pong_error_received)
            pongTV.setTextColor(getColor(R.color.colorPrimaryDark))
        }
    }

    fun pingPongReceiver(view: View) {
        pingTV.text = ""
        pongTV.text = ""
        val payload = "pong"
        val receivedText = QuietUtils.receive(this, 5, 0)
        println("Receiver got '$receivedText' (should be ping)")
        if ("ping" in receivedText) {
            pingTV.text = getString(R.string.ping_success_received)
            pingTV.setTextColor(getColor(R.color.colorAccent))
        } else {
            pingTV.text = getString(R.string.ping_error_received)
            pingTV.setTextColor(getColor(R.color.colorPrimaryDark))
        }
        if (QuietUtils.send(payload)) {
            println("Receiver sent pong")
            pongTV.text = getString(R.string.pong_success_sent)
            pongTV.setTextColor(getColor(R.color.colorAccent))
        } else {
            pongTV.text = getString(R.string.send_error)
            pongTV.setTextColor(getColor(R.color.colorPrimaryDark))
        }
    }
}
