package fr.centralesupelec.db

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        bottom_navigation.setOnNavigationItemSelectedListener(navigationListener)
        openFragment(TodayFragment.newInstance())
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

}
