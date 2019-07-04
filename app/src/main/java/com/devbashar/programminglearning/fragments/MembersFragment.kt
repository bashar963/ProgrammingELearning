package com.devbashar.programminglearning.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest

import com.devbashar.programminglearning.R
import com.devbashar.programminglearning.helperClasses.Constants
import com.devbashar.programminglearning.helperClasses.RequestHandler
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog
import com.github.javiersantos.materialstyleddialogs.enums.Style
import kotlinx.android.synthetic.main.fragment_members.*
import org.json.JSONObject
import java.util.HashMap
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import com.devbashar.programminglearning.activities.ProfileActivity
import com.devbashar.programminglearning.helperClasses.RecyclerItemClickListener


class MembersFragment : Fragment() {

    private lateinit var classID:String
    private lateinit var viewAdapter: androidx.recyclerview.widget.RecyclerView.Adapter<*>
    private lateinit var viewManager: androidx.recyclerview.widget.RecyclerView.LayoutManager
    private var std_names = mutableListOf<UserHanded>()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_members, container, false)
    companion object { fun newInstance(): MembersFragment = MembersFragment() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        classID=this.requireActivity().intent.getStringExtra("classID")
        fillData()
        initAddUser()
        teacher_name.setOnClickListener {
            val i = Intent(this@MembersFragment.context,ProfileActivity::class.java)
            i.putExtra("userID",teacher_id.text.toString())
            startActivity(i)
        }
        teacher.setOnClickListener {
            val i = Intent(this@MembersFragment.context,ProfileActivity::class.java)
            i.putExtra("userID",teacher_id.text.toString())
            startActivity(i)
        }

    }

    private fun initAddUser() {
        add_users.setOnClickListener {
            MaterialStyledDialog.Builder(this.requireContext())
                    .setTitle("Add Users")
                    .setStyle(Style.HEADER_WITH_TITLE)
                    .setDescription("Share this code $classID with your friends \nto join the class ")
                    .setPositiveText("Copy")
                    .setNegativeText("Cancel")
                    .onPositive{_, _ ->
                        val clipboard = this@MembersFragment.requireContext().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?
                        val clip = ClipData.newPlainText("class code", classID)
                        clipboard!!.primaryClip = clip
                        Toast.makeText(this@MembersFragment.requireContext(),"class code copied",Toast.LENGTH_SHORT).show()
                    }
                    .show()
        }
    }

    private fun fillData() {
        val stringRequest : StringRequest = object : StringRequest(Request.Method.POST, Constants().URL_GET_CLASS_USER,
                Response.Listener {
                    try {
                        val jsonObject = JSONObject(it)
                        val arr = jsonObject.getJSONArray("users")
                        std_names.clear()
                        var c=0
                        for (i in 0 until arr.length()){
                            if (arr.getJSONObject(i).getString("user_type") == "teacher"){
                                teacher_name.text = arr.getJSONObject(i).getString("user_name")
                                teacher_id.text = arr.getJSONObject(i).getString("user_id")
                            }else{
                                std_names.add(c,UserHanded(arr.getJSONObject(i).getString("user_name"),"",arr.getJSONObject(i).getString("user_id")))
                                c++
                            }
                            if (i == arr.length()-1){
                                fillList()
                            }
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
                params["id"] = classID
                return params
            }
        }
        RequestHandler.getInstance(this.requireContext()).addToRequestQueue(stringRequest)
    }

    private fun fillList() {
        viewManager = androidx.recyclerview.widget.LinearLayoutManager(this.requireContext())
        viewAdapter = StudentHandedAdapter(std_names)
        std_list.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }.addOnItemTouchListener(RecyclerItemClickListener(this.requireContext(), std_list, object : RecyclerItemClickListener.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
               val i = Intent(this@MembersFragment.context,ProfileActivity::class.java)
                i.putExtra("userID",std_names[position].id)
                startActivity(i)
            }
            override fun onLongItemClick(view: View?, position: Int) {}
        })
        )
    }

}
