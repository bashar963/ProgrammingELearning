package com.devbashar.programminglearning.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.devbashar.programminglearning.R
import com.devbashar.programminglearning.helperClasses.Constants
import com.devbashar.programminglearning.helperClasses.RequestHandler
import com.devbashar.programminglearning.helperClasses.UserSharedPreference
import kotlinx.android.synthetic.main.fragment_announcement.*
import org.json.JSONObject


class Announcement : Fragment() {

    private lateinit var viewAdapter: androidx.recyclerview.widget.RecyclerView.Adapter<*>
    private lateinit var viewManager: androidx.recyclerview.widget.RecyclerView.LayoutManager
    private val myDataSet= mutableListOf<Announce>()
    private var userClasses= mutableSetOf<String>()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_announcement, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userClasses= UserSharedPreference.getInstance(this.requireContext()).getCurrentClasses()!!
        initList()
    }

    private fun initList() {
        fillAnnounceList()
        viewManager = androidx.recyclerview.widget.LinearLayoutManager(this.requireContext())
        viewAdapter = AnnounceAdapter(myDataSet)
        announcment_list.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    private fun fillAnnounceList() {
        for (i in 0 until userClasses.size){
            val stringRequest : StringRequest = object : StringRequest(Request.Method.POST, Constants().URL_GET_ANNOUNCE,
                    Response.Listener {
                        try {
                            val jsonObject = JSONObject(it)
                            val arr = jsonObject.getJSONArray("announcements")

                            for (c in 0 until arr.length()){
                                val a = Announce(arr.getJSONObject(c).getString("announce_title"),arr.getJSONObject(c).getString("announce_desc"),arr.getJSONObject(c).getString("announce_date"),arr.getJSONObject(c).getString("class_name"))
                                myDataSet.add(a)
                            }
                            if (i == userClasses.size-1){
                                progressBar4.visibility = View.GONE
                                if (myDataSet.size==0){
                                    warn_text.visibility = View.VISIBLE
                                }
                                announcment_list.adapter!!.notifyDataSetChanged()
                                viewAdapter.notifyDataSetChanged()
                            }

                        }catch (e: Exception){
                            e.printStackTrace()
                        }
                    }, Response.ErrorListener {
                        Toast.makeText(this.requireContext(),"Error ${it.message}", Toast.LENGTH_LONG).show()
                    }) {
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["class_id"] = userClasses.elementAt(i)
                    return params
                }
            }
            RequestHandler.getInstance(this.requireContext()).addToRequestQueue(stringRequest)
        }
    }

}









class AnnounceAdapter(private val myDataSet: MutableList<Announce>): androidx.recyclerview.widget.RecyclerView.Adapter<AnnounceAdapter.ViewHolder>(){

    class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        var announceTitle = view.findViewById(R.id.announce_title) as TextView
        var announceDesc = view.findViewById(R.id.announce_desc) as TextView
        var announceDate = view.findViewById(R.id.announce_date) as TextView
        var className = view.findViewById(R.id.class_name) as TextView

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.announce_card, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.announceTitle.text = myDataSet[position].aTitle
        holder.announceDesc.text = myDataSet[position].aDesc
        holder.announceDate.text = myDataSet[position].aDate
        holder.className.text = myDataSet[position].className

    }
    override fun getItemCount() = myDataSet.size

}
data class Announce(val aTitle:String,val aDesc:String,val aDate:String,val className:String)