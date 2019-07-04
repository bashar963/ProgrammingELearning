package com.devbashar.programminglearning.fragments


import android.content.Intent
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
import com.devbashar.programminglearning.activities.StudentAnswer
import com.devbashar.programminglearning.helperClasses.Constants
import com.devbashar.programminglearning.helperClasses.RecyclerItemClickListener
import com.devbashar.programminglearning.helperClasses.RequestHandler
import kotlinx.android.synthetic.main.fragment_student_work.*
import org.json.JSONObject
import java.util.HashMap

class StudentWork : Fragment() {

    private lateinit var assignment:Assignment
    private var myDataSet= mutableListOf<UserHanded>()
    private lateinit var viewAdapter: androidx.recyclerview.widget.RecyclerView.Adapter<*>
    private lateinit var viewManager: androidx.recyclerview.widget.RecyclerView.LayoutManager
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_student_work, container, false)

    companion object {
        fun newInstance(): StudentWork = StudentWork()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        assignment = this.activity!!.intent.getParcelableExtra("assignment") as Assignment
        setAssignedNum()
        setHandedNum()
        fillUserHandedList()
    }

    override fun onResume() {
        super.onResume()
        setAssignedNum()
        setHandedNum()
        fillUserHandedList()
    }
    private fun fillUserHandedList() {
        viewManager = androidx.recyclerview.widget.LinearLayoutManager(this.requireContext())
        viewAdapter = StudentHandedAdapter(myDataSet)
        student_list.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }.addOnItemTouchListener(RecyclerItemClickListener(this.requireContext(), student_list, object : RecyclerItemClickListener.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                val i= Intent(this@StudentWork.requireContext(),StudentAnswer::class.java)
                i.putExtra("assign",assignment)
                i.putExtra("std_id",myDataSet[position].id)
                i.putExtra("classID2",this@StudentWork.requireActivity().intent.extras!!.getString("classID1")!!)
                this@StudentWork.startActivity(i)
            }
            override fun onLongItemClick(view: View?, position: Int) {}
        }))
        setHandedNum()
    }

    private fun setHandedNum() {
        val stringRequest : StringRequest = object : StringRequest(Request.Method.POST, Constants().URL_GET_HANDED,
                Response.Listener {
                    try {
                        val jsonObject = JSONObject(it)
                        val arr = jsonObject.getJSONArray("users")
                        handed_num.text = arr.length().toString()
                        myDataSet.clear()
                        var count = 0
                        for (i in 0 until arr.length()){
                            if (arr.getJSONObject(i).getString("student_grade") == "NO"){
                                myDataSet.add(i, UserHanded(arr.getJSONObject(i).getString("student_name"),"HANDED IN",arr.getJSONObject(i).getString("student_id")))
                            }
                            else{
                                count++
                                myDataSet.add(i, UserHanded(arr.getJSONObject(i).getString("student_name"),arr.getJSONObject(i).getString("student_grade"),arr.getJSONObject(i).getString("student_id")))
                            }
                            if (i == arr.length()-1){
                                viewAdapter.notifyDataSetChanged()
                                student_list.adapter!!.notifyDataSetChanged()
                            }
                        }
                        marked_num.text = count.toString()
                    }catch (e: Exception){
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener {
                    Toast.makeText(this.requireContext(),"Error ${it.message}", Toast.LENGTH_LONG).show()
                }) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["class_id"] = this@StudentWork.requireActivity().intent.extras!!.getString("classID1")!!
                params["assign_id"] = assignment.id1
                return params
            }
        }
        RequestHandler.getInstance(this.requireContext()).addToRequestQueue(stringRequest)
    }
    private fun setAssignedNum() {
        val stringRequest : StringRequest = object : StringRequest(Request.Method.POST, Constants().URL_GET_CLASS_USER,
                Response.Listener {
                    try {
                        val jsonObject = JSONObject(it)
                        val arr = jsonObject.getJSONArray("users")
                        var coint = 0
                        for (i in 0 until arr.length()){
                            if (arr.getJSONObject(i).getString("user_type") == "student")
                                coint++
                        }
                        assigned_num.text = coint.toString()

                    }catch (e: Exception){
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener {
                    Toast.makeText(this.requireContext(),"Error ${it.message}", Toast.LENGTH_LONG).show()
                }) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["id"] = this@StudentWork.requireActivity().intent.extras!!.getString("classID1")!!
                return params
            }
        }
        RequestHandler.getInstance(this.requireContext()).addToRequestQueue(stringRequest)
    }

}





class StudentHandedAdapter(private val myDataSet: MutableList<UserHanded>): androidx.recyclerview.widget.RecyclerView.Adapter<StudentHandedAdapter.ViewHolder>(){

    class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        var name = view.findViewById(R.id.std_name) as TextView
        var grade = view.findViewById(R.id.std_grade) as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.student_handed_card, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.name.text = myDataSet[position].name
        holder.grade.text = myDataSet[position].grade

    }
    override fun getItemCount() = myDataSet.size

}
class UserHanded(val name:String,val grade:String,val id:String)