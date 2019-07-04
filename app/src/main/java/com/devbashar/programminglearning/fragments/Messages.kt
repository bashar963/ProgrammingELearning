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
import io.chatcamp.sdk.GroupChannel
import kotlinx.android.synthetic.main.fragment_messages.*
import org.json.JSONObject
import java.util.*
import com.devbashar.programminglearning.activities.ConversationActivity
import android.content.Intent
import io.chatcamp.sdk.ChatCamp


class Messages : Fragment() {

    private lateinit var viewAdapter: androidx.recyclerview.widget.RecyclerView.Adapter<*>
    private lateinit var viewManager: androidx.recyclerview.widget.RecyclerView.LayoutManager
    private val myDataSet= mutableListOf<Contact>()
    private var userClasses= mutableSetOf<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_messages, container, false)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userClasses= UserSharedPreference.getInstance(this.requireContext()).getCurrentClasses()!!
        initContactList()

        ChatCamp.updateUserDisplayName(UserSharedPreference.getInstance(this.requireContext()).getFullName(),ChatCamp.UserUpdateListener { user, chatCampException ->
        })
        ChatCamp.updateUserAvatarUrl("http://178.62.87.100/project/profiledemo.png",ChatCamp.UserUpdateListener { user, chatCampException ->
        })

    }

    private fun initContactList() {
        fillContactList()
        viewManager = androidx.recyclerview.widget.LinearLayoutManager(this.requireContext())
        viewAdapter = ContactAdapter(myDataSet)
        contact_list.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }.addOnItemTouchListener(RecyclerItemClickListener(this.requireContext(), contact_list, object : RecyclerItemClickListener.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                val chatBetween = mutableListOf<String>()
                val CHANNEL_NAME = if (UserSharedPreference.getInstance(this@Messages.requireContext()).getProfession()=="teacher"){
                    UserSharedPreference.getInstance(this@Messages.requireContext()).getFullName()+"and"+myDataSet[position].fullName
                }else{
                    myDataSet[position].fullName+"and"+UserSharedPreference.getInstance(this@Messages.requireContext()).getFullName()
                }
                chatBetween.add(UserSharedPreference.getInstance(this@Messages.requireContext()).getCurrentUserDetails().id.toString())
                chatBetween.add(myDataSet[position].id)

                GroupChannel.create(CHANNEL_NAME,chatBetween.toTypedArray(),true) { baseChannel, _ ->
                    val intent = Intent(this@Messages.requireActivity(), ConversationActivity::class.java)
                    intent.putExtra("channelType", "group")
                    intent.putExtra("channelId", baseChannel.id)
                    startActivity(intent)
                }
            }
            override fun onLongItemClick(view: View?, position: Int) {}
        })
        )
    }

    private fun fillContactList() {
        if (UserSharedPreference.getInstance(this.requireContext()).getCurrentUserDetails().prof  == "student"){
            fillAsStudent()
        }else{
            fillAsTeacher()
        }
    }


    private fun fillAsStudent() {
        val usersID = mutableListOf<String>()
        for (i in 0 until  userClasses.size){
            val stringRequest : StringRequest = object : StringRequest(Request.Method.POST, Constants().URL_GET_CLASS_USER,
                    Response.Listener {
                        try {
                            val jsonObject = JSONObject(it)
                            val arr = jsonObject.getJSONArray("users")
                            for (c in 0 until arr.length()){
                                if (arr.getJSONObject(c).getString("user_type") == "teacher"){
                                    usersID.add(arr.getJSONObject(c).getString("user_id"))
                                }


                            }
                            if (i == userClasses.size-1){
                                fillOntoList(usersID)
                            }

                        }catch (e: Exception){
                            e.printStackTrace()
                        }
                    },
                    Response.ErrorListener {
                        Toast.makeText(this.requireContext(),"Error ${it.message}", Toast.LENGTH_LONG).show()
                    }) {
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["id"] = userClasses.elementAt(i)
                    return params
                }
            }
            RequestHandler.getInstance(this.requireContext()).addToRequestQueue(stringRequest)
        }
    }

    private fun fillOntoList(id: MutableList<String>) {
        val filteredID = id.distinct()
        for (i in 0 until filteredID.size){
            val stringRequest : StringRequest = object : StringRequest(Request.Method.POST, Constants().URL_GET_USER_DET,
                    Response.Listener {
                        try {
                            val jsonObject = JSONObject(it)
                            val fullName = jsonObject.getString("first_name")+" "+jsonObject.getString("last_name")
                            myDataSet.add(Contact(jsonObject.getString("username"),jsonObject.getString("profession"),fullName,jsonObject.getString("id")))
                            if (i == filteredID.size-1){
                                viewAdapter.notifyDataSetChanged()
                                contact_list.adapter!!.notifyDataSetChanged()
                                progressBar3.visibility=View.GONE
                            }
                        }catch (e: Exception){
                            e.printStackTrace()
                        }
                    },
                    Response.ErrorListener {
                        Toast.makeText(this.requireContext(),"Error ${it.message}", Toast.LENGTH_LONG).show()
                    }) {
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["id"] = filteredID[i]
                    return params
                }
            }
            RequestHandler.getInstance(this.requireContext()).addToRequestQueue(stringRequest)
        }

    }

    private fun fillAsTeacher() {
        val usersID = mutableListOf<String>()
        for (i in 0 until  userClasses.size){
            val stringRequest : StringRequest = object : StringRequest(Request.Method.POST, Constants().URL_GET_CLASS_USER,
                    Response.Listener {
                        try {
                            val jsonObject = JSONObject(it)
                            val arr = jsonObject.getJSONArray("users")
                            for (c in 0 until arr.length()){
                                if (arr.getJSONObject(c).getString("user_type") == "student"){
                                    usersID.add(arr.getJSONObject(c).getString("user_id"))
                                }


                            }
                            if (i == userClasses.size-1){
                                fillOntoList(usersID)
                            }

                        }catch (e: Exception){
                            e.printStackTrace()
                        }
                    },
                    Response.ErrorListener {
                        Toast.makeText(this.requireContext(),"Error ${it.message}", Toast.LENGTH_LONG).show()
                    }) {
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["id"] = userClasses.elementAt(i)
                    return params
                }
            }
            RequestHandler.getInstance(this.requireContext()).addToRequestQueue(stringRequest)
        }
    }
}



class ContactAdapter(private val myDataSet: MutableList<Contact>): androidx.recyclerview.widget.RecyclerView.Adapter<ContactAdapter.ViewHolder>(){

    class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        var contactName = view.findViewById(R.id.contact_name) as TextView
        var contactProf = view.findViewById(R.id.profession) as TextView
        var contactFName = view.findViewById(R.id.fullName) as TextView

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.contact_card, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.contactName.text = myDataSet[position].name
        holder.contactProf.text = myDataSet[position].profession
        holder.contactFName.text = myDataSet[position].fullName

    }
    override fun getItemCount() = myDataSet.size

}
data class Contact(val name:String,val profession:String,val fullName:String,val id:String)


