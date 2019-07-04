package com.devbashar.programminglearning.fragments



import android.content.Intent
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.view.Gravity
import androidx.fragment.app.Fragment
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
import com.devbashar.programminglearning.activities.AssignmentWorkStd
import com.devbashar.programminglearning.activities.AssignmentWorkTeacher
import com.devbashar.programminglearning.activities.CreateAssignment
import com.devbashar.programminglearning.helperClasses.*
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog
import com.github.javiersantos.materialstyleddialogs.enums.Style
import kotlinx.android.synthetic.main.fragment_assignment.*
import kotlinx.android.synthetic.main.fragment_discussion.*
import org.json.JSONObject
import java.lang.Exception
import com.orhanobut.dialogplus.DialogPlus
import java.text.SimpleDateFormat
import java.util.*


class ClassWorkFragment : Fragment() {

    private lateinit var viewAdapter: androidx.recyclerview.widget.RecyclerView.Adapter<*>
    private lateinit var viewManager: androidx.recyclerview.widget.RecyclerView.LayoutManager
    private var myDataSet= mutableListOf<Assignment>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_assignment, container, false)
    }

    companion object {
        fun newInstance(): ClassWorkFragment = ClassWorkFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBtn()
        initList()
    }

    private fun initBtn(){
        if (UserSharedPreference.getInstance(this.requireContext()).getProfession() == "student")
            add_class_work.visibility = View.GONE

        add_class_work.setOnClickListener {
            val adapter = SimpleAdapter(this.requireContext(), 2)
            val dialog = DialogPlus.newDialog(this.context)
                    .setAdapter(adapter)
                    .setOnItemClickListener { dialog, _, _, position ->
                        if (position == 0) {

                            this.requireActivity().startActivity(Intent(this.requireContext(), CreateAssignment::class.java).putExtra("id", this.requireActivity().intent.extras!!.getString("classID")!!))

                        } else if (position == 1) {
                            val classId =this.requireActivity().intent.extras!!.getString("classID")!!
                            val className =this.requireActivity().intent.extras!!.getString("className")!!
                            val df = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.ENGLISH)
                            val annDate = df.format(Calendar.getInstance().time).toString()

                            val view = View.inflate(this.requireContext(),R.layout.create_announce,null)
                            val annTitle =view.findViewById<EditText>(R.id.anntitle)
                            val annDesc =view.findViewById<EditText>(R.id.anndesc)
                            MaterialStyledDialog.Builder(this.requireContext())
                                    .setTitle("Create Announcement")
                                    .setStyle(Style.HEADER_WITH_TITLE)
                                    .setCustomView(view)
                                    .setPositiveText("CREATE")
                                    .setNegativeText("Cancel")
                                    .autoDismiss(false)
                                    .onPositive { d, _ ->
                                        if (!annTitle.text.isEmpty() && !annDesc.text.isEmpty()){
                                            createAnnounce(classId,className,annDate,annTitle.text.toString(),annDesc.text.toString())
                                        }
                                        else{
                                            annDesc.error = "Please fill in"
                                            annTitle.error = "Please fill in"
                                        }
                                        d.dismiss()
                                    }
                                    .show()
                        }

                        dialog.dismiss()
                    }
                    .setPadding(5,5,5,5)
                    .setExpanded(false,300)
                    .setGravity(Gravity.BOTTOM)
                    .setCancelable(true)
                    .create()
            dialog.show()
        }
    }

    private fun createAnnounce(classId: String, className: String, annDate: String, annTitle: String, annDesc: String) {
        val stringRequest : StringRequest = object : StringRequest(Request.Method.POST, Constants().URL_CREATE_ANNOUNC,
                Response.Listener {
                    try {



                    }catch (e: Exception){
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener {
                    Toast.makeText(this.context,"Error ${it.message}", Toast.LENGTH_LONG).show()
                }) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["class_id"]= classId
                params["class_name"]=className
                params["announce_title"]=annTitle
                params["announce_desc"]=annDesc
                params["announce_date"]=annDate
                return params
            }
        }
        RequestHandler.getInstance(this.requireContext()).addToRequestQueue(stringRequest)
    }

    private fun initList(){
        viewManager = androidx.recyclerview.widget.LinearLayoutManager(this.requireContext())
        fillAssignList()
        viewAdapter = AssignmentAdapter(myDataSet)
        assignment_list.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter

        }.addOnItemTouchListener(RecyclerItemClickListener(this.requireContext(), assignment_list, object : RecyclerItemClickListener.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                val userType = UserSharedPreference.getInstance(this@ClassWorkFragment.requireContext()).getCurrentUserDetails().prof
                if (userType == "student"){
                    val intent= Intent(this@ClassWorkFragment.requireContext(),AssignmentWorkStd::class.java)
                    intent.putExtra("assignment",myDataSet[position])
                    intent.putExtra("classID1",this@ClassWorkFragment.requireActivity().intent.extras!!.getString("classID")!!)
                    this@ClassWorkFragment.startActivity(intent)
                }else{
                    val intent= Intent(this@ClassWorkFragment.requireContext(), AssignmentWorkTeacher::class.java)
                    intent.putExtra("assignment",myDataSet[position])
                    intent.putExtra("classID1",this@ClassWorkFragment.requireActivity().intent.extras!!.getString("classID")!!)
                    this@ClassWorkFragment.startActivity(intent)
                }
            }
            override fun onLongItemClick(view: View?, position: Int) {}
        })


        )
    }

    override fun onResume() {
        super.onResume()
        fillAssignList()
    }
    private fun fillAssignList() {
        val stringRequest : StringRequest = object : StringRequest(Request.Method.POST, Constants().URL_GET_ASSIGNMENT,
                Response.Listener {
                    try {
                        val jsonObject = JSONObject(it)
                        val arr = jsonObject.getJSONArray("assignments")
                        myDataSet.clear()
                        for (i in 0 until arr.length()){
                            val assign = Assignment(arr.getJSONObject(i).getString("id")
                                    ,arr.getJSONObject(i).getString("assignment_id")
                                    ,arr.getJSONObject(i).getString("assignment_title")
                                    ,arr.getJSONObject(i).getString("assignment_desc")
                                    ,arr.getJSONObject(i).getString("assignment_points")
                                    ,arr.getJSONObject(i).getString("assignment_date")
                                    ,arr.getJSONObject(i).getString("attachment_id")
                                    ,arr.getJSONObject(i).getString("posted_date"))
                            myDataSet.add(i,assign)
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
                params["id"]= this@ClassWorkFragment.requireActivity().intent.extras!!.getString("classID")!!
                return params
            }
        }
        RequestHandler.getInstance(this.requireContext()).addToRequestQueue(stringRequest)
    }

}










class AssignmentAdapter(private val myDataSet: MutableList<Assignment>): androidx.recyclerview.widget.RecyclerView.Adapter<AssignmentAdapter.ViewHolder>(){

    class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        var name = view.findViewById(R.id.announce_title) as TextView
        var date = view.findViewById(R.id.assign_date) as TextView


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.assignment_card, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.name.text = myDataSet[position].title
        holder.date.text = myDataSet[position].date

    }
    override fun getItemCount() = myDataSet.size

}
class Assignment(val id1:String,val assign_id:String,val title:String,val desc:String,val points:String,val dueDate1:String,val attach_id:String, val date:String):Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id1)
        parcel.writeString(assign_id)
        parcel.writeString(title)
        parcel.writeString(desc)
        parcel.writeString(points)
        parcel.writeString(dueDate1)
        parcel.writeString(attach_id)
        parcel.writeString(date)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Assignment> {
        override fun createFromParcel(parcel: Parcel): Assignment {
            return Assignment(parcel)
        }

        override fun newArray(size: Int): Array<Assignment?> {
            return arrayOfNulls(size)
        }
    }
}
