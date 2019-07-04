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
import com.devbashar.programminglearning.helperClasses.RecyclerItemClickListener
import com.devbashar.programminglearning.helperClasses.RequestHandler
import com.devbashar.programminglearning.helperClasses.UserSharedPreference
import kotlinx.android.synthetic.main.fragment_discussion.*
import org.json.JSONObject
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*


class DiscussionFragment : Fragment() {


    private lateinit var viewAdapter: androidx.recyclerview.widget.RecyclerView.Adapter<*>
    private lateinit var viewManager: androidx.recyclerview.widget.RecyclerView.LayoutManager
    private val myDataSet= mutableListOf<Post>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_discussion, container, false)

    companion object {
        fun newInstance(): DiscussionFragment = DiscussionFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        initButton()
    }
    private fun createPost(text: String,name: String,date: String){
        val stringRequest : StringRequest = object : StringRequest(Request.Method.POST, Constants().URL_SET_POST,
                Response.Listener {
                    try {
                        val jsonObject = JSONObject(it)
                        val msg = jsonObject.getString("msg")
                        if (msg=="post created successfully"){
                            fillPosts()
                        }

                    }catch (e: Exception){
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener {
                    Toast.makeText(this.context,"Error ${it.message}", Toast.LENGTH_LONG).show()
                }) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["id"]= this@DiscussionFragment.requireActivity().intent.extras!!.getString("classID")!!
                params["name"] = name
                params["date"] = date
                params["text"] = text
                return params
            }
        }
        RequestHandler.getInstance(this.requireContext()).addToRequestQueue(stringRequest)
    }
    private fun fillPosts(){

        val stringRequest : StringRequest = object : StringRequest(Request.Method.POST, Constants().URL_GET_POST,
                Response.Listener {
                    try {
                        val jsonObject = JSONObject(it)
                        val arr = jsonObject.getJSONArray("posts")
                        myDataSet.clear()
                        for (i in 0 until arr.length()){
                            myDataSet.add(i, Post(arr.getJSONObject(i).getString("post_name"),arr.getJSONObject(i).getString("post_date"),arr.getJSONObject(i).getString("post_text")))
                            if (i == arr.length()-1) {
                                viewAdapter.notifyDataSetChanged()
                                posts_list.adapter!!.notifyDataSetChanged()
                            }
                        }

                    }catch (e: Exception){
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener {
                    Toast.makeText(this.context,"Error ${it.message}", Toast.LENGTH_LONG).show()
                }) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["id"]= this@DiscussionFragment.requireActivity().intent.extras!!.getString("classID")!!
                return params
            }
        }
        RequestHandler.getInstance(this.requireContext()).addToRequestQueue(stringRequest)
    }
    private fun initButton(){
        send_button.setOnClickListener {
            val df = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.ENGLISH)
            val date = df.format(Calendar.getInstance().time).toString()
            createPost(message_text.text.toString(),UserSharedPreference.getInstance(this.requireContext()).getFullName(),date)
            message_text.setText("")
        }
    }
    private fun initList(){
        viewManager = androidx.recyclerview.widget.LinearLayoutManager(this.requireContext())
        fillPosts()
        viewAdapter = PostListAdapter(myDataSet)
        posts_list.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter

        }.addOnItemTouchListener(RecyclerItemClickListener(this.requireContext(), posts_list, object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {

                    }
                    override fun onLongItemClick(view: View?, position: Int) {}
                })
                )
    }

}







class PostListAdapter(private val myDataSet: MutableList<Post>): androidx.recyclerview.widget.RecyclerView.Adapter<PostListAdapter.ViewHolder>(){

    class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        var postName = view.findViewById(R.id.post_name) as TextView
        var postDate = view.findViewById(R.id.post_date) as TextView
        var postText = view.findViewById(R.id.post_text) as TextView

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.post_card, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.postName.text = myDataSet[position].name
        holder.postDate.text = myDataSet[position].date
        holder.postText.text = myDataSet[position].text
    }
    override fun getItemCount() = myDataSet.size

}

class Post(val name:String,val date:String,val text:String)
