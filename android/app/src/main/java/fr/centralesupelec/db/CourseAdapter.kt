package fr.centralesupelec.db

import android.content.Context
import android.content.res.Resources
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.widget.ImageView


class CourseAdapter(private val dataset: Array<CourseModel>, private val context: Context) :
    RecyclerView.Adapter<CourseAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.course_item, parent, false)
        return Holder(view)
    }

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.course_name)
        val teacher: TextView = view.findViewById(R.id.course_teacher)
        val infos: TextView = view.findViewById(R.id.course_infos)
        var icon: ImageView = view.findViewById(R.id.course_icon)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.name.text = dataset[position].name
        holder.teacher.text = dataset[position].teacher
        holder.infos.text = "${dataset[position].room}, ${dataset[position].time}"
        if (dataset[position].verification == PositionVerification.NONE) holder.icon.setImageDrawable(
            context.getDrawable(R.drawable.ic_location_off_black_24dp)
        )
    }

    override fun getItemCount() = dataset.size
}