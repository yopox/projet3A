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

        bottom_navigation.selectedItemId = R.id.navigation_profil

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
    }

}
