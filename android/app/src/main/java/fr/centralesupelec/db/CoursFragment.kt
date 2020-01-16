package fr.centralesupelec.db


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager




/**
 * A simple [Fragment] subclass.
 */
class CoursFragment : Fragment() {

    var data = arrayOf<CourseModel>()

    companion object {
        fun newInstance(): CoursFragment = CoursFragment()
        lateinit var adapter: CourseAdapter
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView = inflater.inflate(R.layout.fragment_cours, container, false)

        data = arrayOf(
            CourseModel(
                "BE Fondements 6/6",
                "G. Hiet",
                "509",
                "8:30 — 12:00",
                PositionVerification.NONE
            ),
            CourseModel(
                "Voeux du Directeur",
                "R. Soubeyran",
                "Amphi ABL",
                "12:00 — 12:30",
                PositionVerification.ANONYMOUS
            ),
            CourseModel(
                "Mineure n°6",
                "S. Le Gall",
                "309",
                "13:30 — 17:00",
                PositionVerification.IDENTIFIED_CONTINUOUS
            )
        )

        adapter = CourseAdapter(data, context!!)
        val rV = rootView.findViewById<RecyclerView>(R.id.recyclerView)
        rV.adapter = adapter
        val llm = LinearLayoutManager(context)
        llm.orientation = LinearLayoutManager.VERTICAL
        rV.layoutManager = llm

        return rootView
    }

}
