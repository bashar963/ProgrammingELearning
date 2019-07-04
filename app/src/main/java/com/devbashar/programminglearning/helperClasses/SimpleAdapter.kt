package com.devbashar.programminglearning.helperClasses

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.devbashar.programminglearning.R

class SimpleAdapter(
        context: Context,
        private val count: Int
) : BaseAdapter() {

    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)

    override fun getCount(): Int {
        return count
    }

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val viewHolder: ViewHolder
        var view: View? = convertView

        if (view == null) {
            view=layoutInflater.inflate(R.layout.simple_list, parent, false)!!
            viewHolder = ViewHolder(view.findViewById(R.id.text_view), view.findViewById(R.id.image_view))
            view.tag = viewHolder
        } else {
            viewHolder = view.tag as ViewHolder
        }

        when (position) {
            0 -> {
                viewHolder.textView.text = "Assignment"
                viewHolder.imageView.setImageResource(R.drawable.ic_assignment)
            }
            1 -> {
                viewHolder.textView.text = "Announcement"
                viewHolder.imageView.setImageResource(R.drawable.ic_announcemen)
            }
            else -> {
                viewHolder.textView.text = "Assignment"
                viewHolder.imageView.setImageResource(R.drawable.ic_assignment)
            }
        }

        return view
    }

    data class ViewHolder(val textView: TextView, val imageView: ImageView)
}