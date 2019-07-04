package com.devbashar.programminglearning.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.devbashar.programminglearning.R
import kotlinx.android.synthetic.main.activity_create_assignment.*
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment
import java.text.SimpleDateFormat
import java.util.*
import android.content.Intent
import com.devbashar.programminglearning.util.FilePath
import android.app.Activity
import android.util.Log
import android.view.*
import android.widget.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.devbashar.programminglearning.helperClasses.Constants
import com.devbashar.programminglearning.helperClasses.RecyclerItemClickListener
import com.devbashar.programminglearning.helperClasses.RequestHandler
import com.devbashar.programminglearning.util.UploadFile
import java.lang.ref.WeakReference






class CreateAssignment : AppCompatActivity() {

    private lateinit var viewAdapter: androidx.recyclerview.widget.RecyclerView.Adapter<*>
    private lateinit var viewManager: androidx.recyclerview.widget.RecyclerView.LayoutManager
    private val PICK_FILE_REQUEST = 1
    private val TAG = MainActivity::class.java.simpleName
    private var selectedFilePath= mutableListOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_assignment)
        initBTNs()
        initList()
    }

    private fun initList(){
        viewManager = androidx.recyclerview.widget.LinearLayoutManager(this)

        viewAdapter = AttachListAdapter(selectedFilePath)
        attachment_list.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter

        }.addOnItemTouchListener(RecyclerItemClickListener(this, attachment_list, object : RecyclerItemClickListener.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                  Toast.makeText(this@CreateAssignment,"Long press to delete",Toast.LENGTH_LONG).show()

            }
            override fun onLongItemClick(view: View?, position: Int) {
                selectedFilePath.removeAt(position)
                viewAdapter.notifyDataSetChanged()
                attachment_list.adapter!!.notifyDataSetChanged()
            }
        })
        )
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.class_work_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            R.id.attachment->{attachFile()}
            R.id.create->{createAssignment()}
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initBTNs(){
        cancel_date.setOnClickListener {
            dueDate.text= resources.getString(R.string.no_due_date)
            cancel_date.visibility = View.GONE
        }
        dueDate.setOnClickListener {
            val dateTimeDialogFragment = SwitchDateTimeDialogFragment.newInstance(
                    "DateTime",
                    "OK",
                    "Cancel",
                    "Clean"
            )
            dateTimeDialogFragment.startAtCalendarView()
            dateTimeDialogFragment.set24HoursMode(true)
            dateTimeDialogFragment.minimumDateTime = Calendar.getInstance().time
            dateTimeDialogFragment.setDefaultDateTime( Calendar.getInstance().time)
            dateTimeDialogFragment.setOnButtonClickListener(object : SwitchDateTimeDialogFragment.OnButtonWithNeutralClickListener {
               override fun onPositiveButtonClick(date: Date) {
                   dueDate.text = SimpleDateFormat("dd MM yyyy, HH:mm", Locale.ENGLISH).format(date).toString()
                   cancel_date.visibility = View.VISIBLE
               }

               override fun onNegativeButtonClick(date: Date) {}

               override fun onNeutralButtonClick(date: Date) {
                   dueDate.text = resources.getString(R.string.no_due_date)
                   cancel_date.visibility = View.GONE
               }
            })
            dateTimeDialogFragment.show(supportFragmentManager, "dialog_time")
        }
    }
    private fun createAssignment() {
        var attachID = ""
        if (!selectedFilePath.isNullOrEmpty()){
            attachID = getIdPattern()
            for (i in 0 until selectedFilePath.size){
                uploadFile(selectedFilePath,i,attachID)
            }
        }
        val stringRequest : StringRequest = object : StringRequest(Request.Method.POST, Constants().URL_CREATE_ASSIGNMENT,
                Response.Listener {
                    try {
                       this@CreateAssignment.finish()

                    }catch (e: Exception){
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener {
                    Toast.makeText(this,"Error ${it.message}", Toast.LENGTH_LONG).show()
                }) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["id"] = this@CreateAssignment.intent.extras!!.getString("id")!!
                params["title"] = title_assignment.text.toString()
                params["date"] = dueDate.text.toString()
                params["desc"] = assignment_desc.text.toString()
                params["attach_id"] = attachID
                params["point"] = points.text.toString()
                params["posted_date"] = SimpleDateFormat("dd MM yyyy, HH:mm", Locale.ENGLISH).format(Calendar.getInstance().time).toString()
                return params
            }
        }
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest)


    }

    private fun getIdPattern():String{
        val r = Random().nextInt(9)+1
        return UUID.randomUUID().toString().subSequence(0,7).toString()+r
    }

    private fun attachFile() {
        showFileChooser()
    }

    private fun showFileChooser() {
        val intent = Intent()
        intent.type = "*/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Choose File to Upload.."), PICK_FILE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_FILE_REQUEST) {
                if (data == null) {
                    return
                }
                val selectedFileUri = data.data
                selectedFilePath.add(FilePath.getPath(this, selectedFileUri!!)!!)
                Log.i(this.TAG, "Selected File Path:$selectedFilePath")

                if (selectedFilePath.isNotEmpty() ) {
                    viewAdapter.notifyDataSetChanged()
                    attachment_list.adapter!!.notifyDataSetChanged()
                } else {
                    Toast.makeText(this, "Cannot upload file to server", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun uploadFile(selectedFilePath: MutableList<String>, id:Int, attachID : String): Int {
       UploadFile( WeakReference(this.applicationContext)).execute(selectedFilePath[id],attachID)
            return 1
    }


}



class AttachListAdapter(private val myDataSet: MutableList<String>,private val hideElement:Boolean = false): androidx.recyclerview.widget.RecyclerView.Adapter<AttachListAdapter.ViewHolder>(){

    class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        var name = view.findViewById(R.id.text) as TextView
        var btn= view.findViewById<ImageView>(R.id.delete)!!

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.attachment_card, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.name.text = myDataSet[position]
        if (hideElement){
            holder.btn.visibility=View.INVISIBLE
        }

    }
    override fun getItemCount() = myDataSet.size

}



