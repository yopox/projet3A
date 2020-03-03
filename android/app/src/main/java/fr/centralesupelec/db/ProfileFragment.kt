package fr.centralesupelec.db


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.UiThread
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import fr.centralesupelec.db.skowa.DroidTag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment() {

    companion object {
        fun newInstance(): ProfileFragment = ProfileFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    @UiThread
    fun tag(address: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            setupTag(address)
        }
    }

    private suspend fun setupTag(address: String) {
        withContext(Dispatchers.IO) {
            val t = DroidTag()
            t.init(address)
        }
    }


}
