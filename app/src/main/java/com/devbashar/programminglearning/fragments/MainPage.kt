package com.devbashar.programminglearning.fragments


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.devbashar.programminglearning.R
import com.devbashar.programminglearning.activities.ClassRoom
import com.devbashar.programminglearning.helperClasses.Constants
import com.devbashar.programminglearning.helperClasses.RecyclerItemClickListener
import com.devbashar.programminglearning.helperClasses.RequestHandler
import com.devbashar.programminglearning.helperClasses.UserSharedPreference
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog
import com.github.javiersantos.materialstyleddialogs.enums.Style
import kotlinx.android.synthetic.main.fragment_main_page.*
import org.json.JSONObject
import java.lang.Exception
import java.util.*


class MainPage : androidx.fragment.app.Fragment() {

    private lateinit var viewAdapter: androidx.recyclerview.widget.RecyclerView.Adapter<*>
    private lateinit var viewManager: androidx.recyclerview.widget.RecyclerView.LayoutManager
    private val myDataSet= mutableListOf<Class>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        initBtn()



    }


    private fun joinClass(classCode:String){

        val currentUserId= UserSharedPreference.getInstance(this.requireContext()).getCurrentUserDetails().id
        val currentUserType=UserSharedPreference.getInstance(this.requireContext()).getCurrentUserDetails().prof
        val currentUserName=UserSharedPreference.getInstance(this.requireContext()).getFullName()

        val stringRequest : StringRequest = object : StringRequest(Request.Method.POST, Constants().URL_JOIN_CLASS,
                Response.Listener {
                    try {
                        val jsonObject = JSONObject(it)
                        val err = jsonObject.getString("msg")
                        when (err) {
                            "class joined successfully" -> {
                               fillUserClasses()
                            }
                            "error while joining class"->{
                                Log.e("classes","ERR:join class")
                            }
                            "user already joined the class"->{
                                showExceptionDialog("You've already joined the class")
                            }
                            "class not found"->{
                                showExceptionDialog("Sorry couldn't find the class you've entered")
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
                params["class_id"] = classCode
                params["user_type"] = currentUserType!!
                params["user_id"] = currentUserId.toString()
                params["user_name"]= currentUserName
                return params
            }
        }
        RequestHandler.getInstance(this.requireContext()).addToRequestQueue(stringRequest)

    }
    private fun createClass(classID:String,className:String,section:String,room:String,subject:String){
        val stringRequest : StringRequest = object : StringRequest(Request.Method.POST, Constants().URL_CREATE_CLASS,
                Response.Listener {
                    try {
                        val jsonObject = JSONObject(it)
                        val err = jsonObject.getString("msg")
                        when (err) {
                            "class created successfully" -> {
                                joinClass(classID)
                            }
                            "error while creating class"->{
                                Log.e("classes","ERR:crete class")
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
                params["id"]=classID
                params["class_name"]=className
                params["class_section"]=section
                params["class_room"]=room
                params["class_subject"]=subject
                return params
            }
        }
        RequestHandler.getInstance(this.requireContext()).addToRequestQueue(stringRequest)

    }
    private fun createClass(className:String,section:String,room:String,subject:String){
        checkClassID(getClassIdPattern(),className,section,room,subject)

    }

    private fun showExceptionDialog(msg:String){
        MaterialStyledDialog.Builder(this.requireContext())
                .setTitle("Error")
                .setStyle(Style.HEADER_WITH_TITLE)
                .setDescription(msg)
                .show()
    }
    private fun checkClassID(classCode: String,className:String,section:String,room:String,subject:String){

        val stringRequest : StringRequest = object : StringRequest(Request.Method.POST, Constants().URL_CHECK_CLASS,
                Response.Listener {
                    try {
                        val jsonObject = JSONObject(it)
                        val err = jsonObject.getString("msg")
                        when (err) {
                            "class is exist" -> {
                                createClass(className,section,room,subject)
                            }
                            "class is not exist"->{
                               createClass(classCode,className,section,room,subject)
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
                params["id"]=classCode
                return params
            }
        }
        RequestHandler.getInstance(this.requireContext()).addToRequestQueue(stringRequest)

    }
    private fun getClassIdPattern():String{
        val r = Random().nextInt(9)+1
        return UUID.randomUUID().toString().subSequence(0,7).toString()+r
    }
    private fun fillUserClasses(){
        getUserClassCodes()
    }
    private fun getUserClassCodes(){
        val stringRequest : StringRequest = object : StringRequest(Request.Method.POST, Constants().URL_USER_CLASSES,
                Response.Listener {
                    try {
                        val jsonObject = JSONObject(it)
                        val arr = jsonObject.getJSONArray("classes")
                        val list = ArrayList<String>()
                        for (i in 0 until arr.length()) {
                            list.add(arr.getJSONObject(i).getString("class_id"))
                        }
                        if (list.size>0){
                            fillClassIntoList(list)
                        }else{
                            progressBar2.visibility = View.GONE
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
                params["id"]= UserSharedPreference.getInstance(this@MainPage.requireContext()).getCurrentUserDetails().id.toString()
                return params
            }
        }
        RequestHandler.getInstance(this.requireContext()).addToRequestQueue(stringRequest)
    }

    private fun fillClassIntoList(list:ArrayList<String>){
        myDataSet.clear()
        val idList= mutableSetOf<String>()
        for (i in 0 until list.size){
            val stringRequest : StringRequest = object : StringRequest(Request.Method.POST, Constants().URL_CLASS_DET,
                    Response.Listener {
                        try {
                            val jsonObject = JSONObject(it)
                            idList.add(jsonObject.getString("id"))
                            myDataSet.add(Class(jsonObject.getString("class_name"),jsonObject.getString("class_section"),jsonObject.getString("class_subject"),list[i]))
                            if (i==list.size-1){
                                viewAdapter.notifyDataSetChanged()
                                classes_list.adapter!!.notifyDataSetChanged()
                                progressBar2.visibility = View.GONE
                                UserSharedPreference.getInstance(this@MainPage.requireContext()).saveCurrentClasses(idList)
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
                    params["id"]=list[i]
                    return params
                }
            }
            RequestHandler.getInstance(this.requireContext()).addToRequestQueue(stringRequest)
        }
    }
    private fun initBtn(){
        add_or_search_class.setOnClickListener {
           val prof= UserSharedPreference.getInstance(this.requireContext()).getProfession()
            when(prof){
                "teacher"->{
                    val view = View.inflate(this.requireContext(),R.layout.create_class_dialog,null)
                    val className =view.findViewById<EditText>(R.id.class_name)
                    val section =view.findViewById<EditText>(R.id.class_section)
                    val room =view.findViewById<EditText>(R.id.class_room)
                    val subject =view.findViewById<EditText>(R.id.class_subject)
                    MaterialStyledDialog.Builder(this.requireContext())
                            .setTitle("Create Class")
                            .setStyle(Style.HEADER_WITH_TITLE)
                            .setCustomView(view)
                            .setPositiveText("CREATE")
                            .autoDismiss(false)
                            .onPositive { dialog, _ ->
                                if (!className.text.isEmpty()){
                                    createClass(className.text.toString(),section.text.toString(),room.text.toString(),subject.text.toString())
                                    dialog.dismiss()
                                }else{
                                    className.error="Cannot be empty"
                                }

                            }
                            .setNegativeText("Cancel")
                            .onNegative { dialog, _ ->
                                dialog.dismiss()
                            }
                            .show()
                }
                "student"->{
                    val view = View.inflate(this.requireContext(),R.layout.join_class_dialog,null)
                    val classCode =view.findViewById<EditText>(R.id.input_class)
                    MaterialStyledDialog.Builder(this.requireContext())
                            .setTitle("Join Class")
                            .setStyle(Style.HEADER_WITH_TITLE)
                            .setDescription("Ask your teacher for the class code, then enter it here.")
                            .setCustomView(view)
                            .setPositiveText("JOIN")
                            .autoDismiss(false)
                            .onPositive { dialog, _ ->
                                if (!classCode.text.toString().isEmpty()){
                                    joinClass(classCode.text.toString())
                                    dialog.dismiss()
                                }else{
                                    classCode.error="Enter a class code"
                                }

                            }
                            .setNegativeText("Cancel")
                            .onNegative { dialog, _ ->
                                dialog.dismiss()
                            }
                            .show()

                }
            }
        }
    }
    private fun initList(){
        viewManager = androidx.recyclerview.widget.LinearLayoutManager(this.requireContext())
        fillUserClasses()
        viewAdapter = ClassListAdapter(myDataSet)
        classes_list.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter

        }
        classes_list.addOnItemTouchListener(RecyclerItemClickListener(this.requireContext(), classes_list, object : RecyclerItemClickListener.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                val i = Intent(this@MainPage.requireContext(),ClassRoom::class.java).putExtra("classID",myDataSet[position].classID)
                i.putExtra("className",myDataSet[position].name)

                this@MainPage.startActivity(i)
            }
            override fun onLongItemClick(view: View?, position: Int) {}
        })
        )
    }


}












class ClassListAdapter (private val myDataSet: MutableList<Class>): androidx.recyclerview.widget.RecyclerView.Adapter<ClassListAdapter.ViewHolder>(){


    class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        var className = view.findViewById(R.id.class_name) as TextView
        var classSection = view.findViewById(R.id.section) as TextView
        var stdNum = view.findViewById(R.id.std_enrolled) as TextView
        var classId = view.findViewById<TextView>(R.id.class_id)!!

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.class_card, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.className.text = myDataSet[position].name
        holder.classSection.text = myDataSet[position].section
        holder.stdNum.text = myDataSet[position].subject
        holder.classId.text = myDataSet[position].classID
    }
    override fun getItemCount() = myDataSet.size

}

class Class(val name:String,val section:String,val subject:String,val classID: String)
