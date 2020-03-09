package fr.centralesupelec.db

import android.Manifest
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import fr.centralesupelec.db.audio.Receiver
import fr.centralesupelec.db.audio.Sender
import fr.centralesupelec.db.skowa.DroidReader
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_schedule.*
import kotlin.random.Random


class MainActivity : AppCompatActivity() {

    lateinit var currentFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        bottom_navigation.setOnNavigationItemSelectedListener(navigationListener)
        currentFragment = ScheduleFragment.newInstance()

        openFragment(currentFragment)

        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 200)
    }

    private val navigationListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_cours -> {
                toolbar.title = getString(R.string.course)
                currentFragment = TodayFragment.newInstance()
                openFragment(currentFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_edt -> {
                toolbar.title = getString(R.string.schedule)
                currentFragment = ScheduleFragment.newInstance()
                openFragment(currentFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_profil -> {
                toolbar.title = getString(R.string.profile)
                currentFragment = ProfileFragment.newInstance()
                openFragment(currentFragment)
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

    fun sendTrue(view: View) {
        Sender.playSound(true)
    }

    fun sendFalse(view: View) {
        Sender.playSound(false)
    }

    fun receive(view: View) {
        when (Receiver.receive(10)) {
            0 -> received.text = "FALSE"
            1 -> received.text = "TRUE"
            else -> received.text = "NONE"
        }
    }

    fun tag(view: View) {
        (currentFragment as ProfileFragment).tag(p_address.editText!!.text.toString())
    }

    fun reader(view: View) {
        val rand = Random(System.currentTimeMillis())
        val r = DroidReader(rand.nextInt(), results)
        //r.init(p_ip.editText!!.text.toString().toInt(), this)
    }

    fun pingPongEmitter(view: View) {
        pingTV.text = ""
        pongTV.text = ""
        val payload = "ping"
        val start = System.currentTimeMillis()
        sendTrue(view)
        println("Emitter emitted Ping")
        pingTV.text = getString(R.string.ping_success_sent)
        pingTV.setTextColor(getColor(R.color.colorAccent))

        val receivedText = Receiver.receive(10)
        println("Emitter got '$receivedText' (should be False)")
        if (receivedText >= 0) {
            val elapsed = System.currentTimeMillis() - start
            val distance = elapsed * 340 / 2000
            timeTV.text = "$distance m"
            pongTV.text = getString(R.string.pong_succes_received)
            pongTV.setTextColor(getColor(R.color.colorAccent))
        } else {
            pongTV.text = getString(R.string.pong_error_received)
            pongTV.setTextColor(getColor(R.color.colorPrimaryDark))
        }
    }

    fun pingPongReceiver(view: View) {
        pingTV.text = ""
        pongTV.text = ""
        val payload = "pong"
        val receivedText = Receiver.receive(10)
        println("Receiver got '$receivedText' (should be True)")
        if (receivedText >= 0) {
            pingTV.text = getString(R.string.ping_success_received)
            pingTV.setTextColor(getColor(R.color.colorAccent))
        } else {
            pingTV.text = getString(R.string.ping_error_received)
            pingTV.setTextColor(getColor(R.color.colorPrimaryDark))
        }
        Sender.playSound(false)
    }
}
