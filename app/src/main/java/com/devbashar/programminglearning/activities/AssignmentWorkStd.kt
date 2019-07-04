package com.devbashar.programminglearning.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.FileProvider
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.devbashar.programminglearning.R
import com.devbashar.programminglearning.fragments.Assignment
import com.devbashar.programminglearning.helperClasses.Constants
import com.devbashar.programminglearning.helperClasses.RecyclerItemClickListener
import com.devbashar.programminglearning.helperClasses.RequestHandler
import com.devbashar.programminglearning.helperClasses.UserSharedPreference
import com.devbashar.programminglearning.util.FilePath
import com.devbashar.programminglearning.util.RandomUtils
import com.devbashar.programminglearning.util.UploadFile
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog
import com.github.javiersantos.materialstyleddialogs.enums.Style
import com.krishna.fileloader.FileLoader
import com.krishna.fileloader.listener.FileRequestListener
import com.krishna.fileloader.pojo.FileResponse
import com.krishna.fileloader.request.FileLoadRequest
import kotlinx.android.synthetic.main.activity_assignment_work.*
import org.json.JSONObject
import java.io.File
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.*


class AssignmentWorkStd : AppCompatActivity() {

    private val PICK_FILE_REQUEST = 1
    private var selectedFilePath= mutableListOf<String>()
    private var URLs= mutableListOf<String>()
    private var myDataSet= mutableListOf<String>()
    private lateinit var viewAdapter: androidx.recyclerview.widget.RecyclerView.Adapter<*>
    private lateinit var viewManager: androidx.recyclerview.widget.RecyclerView.LayoutManager

    private lateinit var viewAdapter2: androidx.recyclerview.widget.RecyclerView.Adapter<*>
    private lateinit var viewManager2: androidx.recyclerview.widget.RecyclerView.LayoutManager

    private lateinit var assignment:Assignment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assignment_work)
        assignment = intent.getParcelableExtra("assignment") as Assignment
        initAssignDet()
        initAttachButton()
        initSubmit()
        checkIfStudentAnswer()
    }

    private fun checkIfStudentAnswer() {
        val stringRequest : StringRequest = object : StringRequest(Request.Method.POST, Constants().URL_GET_ANSWER,
                Response.Listener {
                    try {

                        val jsonObject = JSONObject(it)
                        val arr = jsonObject.getJSONArray("answers")
                        val idd = arr.getJSONObject(0).getString("student_id")
                        if (idd==UserSharedPreference.getInstance(this@AssignmentWorkStd).getCurrentUserDetails().id.toString()){
                            description.visibility = View.GONE
                            assign_attachment_list.visibility = View.GONE
                            textView10.visibility = View.GONE
                            student_attach_list.visibility = View.GONE
                            add_attach.visibility = View.GONE
                            std_answer.visibility =View.GONE
                            submet.visibility = View.GONE
                            message_assign.visibility = View.VISIBLE
                            if (arr.getJSONObject(0).getString("student_grade") != "NO")
                            student_mark.text = arr.getJSONObject(0).getString("student_grade")
                        }

                    }catch (e: Exception){
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener {
                    Toast.makeText(this,"Error ${it.message}", Toast.LENGTH_LONG).show()
                }) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["std_id"] = UserSharedPreference.getInstance(this@AssignmentWorkStd).getCurrentUserDetails().id.toString()
                params["assign_id"] = assignment.id1
                params["class_id"] = this@AssignmentWorkStd.intent.extras!!.getString("classID1")!!
                return params
            }
        }
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest)
    }

    private fun getIdPattern():String{
        val r = Random().nextInt(9)+1
        return UUID.randomUUID().toString().subSequence(0,7).toString()+r
    }
    private fun uploadFile(selectedFilePath: MutableList<String>, id:Int, attachID : String): Int {
        UploadFile( WeakReference(this.applicationContext)).execute(selectedFilePath[id],attachID)
        return 1
    }
    private fun submitAnswer(){
        var attachID = ""
        if (!selectedFilePath.isEmpty()){
            attachID = getIdPattern()
            for (i in 0 until selectedFilePath.size){
                uploadFile(selectedFilePath,i,attachID)
            }
        }
        val stringRequest : StringRequest = object : StringRequest(Request.Method.POST, Constants().URL_SET_ANSWER,
                Response.Listener {
                    try {

                    }catch (e: Exception){
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener {
                    Toast.makeText(this,"Error ${it.message}", Toast.LENGTH_LONG).show()
                }) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["std_id"] = UserSharedPreference.getInstance(this@AssignmentWorkStd.applicationContext).getCurrentUserDetails().id.toString()
                params["assignment_id"] = assignment.id1
                params["class_id"] = this@AssignmentWorkStd.intent.extras!!.getString("classID1")!!
                params["attach_id"] =attachID
                params["answer_date"] = SimpleDateFormat("dd MM yyyy, HH:mm", Locale.ENGLISH).format(Calendar.getInstance().time).toString()
                params["answer_text"] = std_answer.text.toString()
                params["std_grade"] = "NO"
                params["std_name"] = UserSharedPreference.getInstance(this@AssignmentWorkStd).getFullName()
                return params
            }
        }
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest)
    }
    private fun initSubmit(){
        submet.setOnClickListener {
            MaterialStyledDialog.Builder(this)
                    .setTitle("Are you sure")
                    .setDescription("You Can't Edit back if you submit the answer")
                    .setStyle(Style.HEADER_WITH_TITLE)
                    .setPositiveText("Yes")
                    .setNegativeText("No")
                    .onPositive{ dialog, _ ->
                        submitAnswer()
                        this.finish()
                        dialog.dismiss()
                    }
                    .show()

        }
    }
    private fun initAttachButton(){
        viewManager2 = androidx.recyclerview.widget.LinearLayoutManager(this)
        viewAdapter2 = AttachListAdapter(selectedFilePath)
        student_attach_list.apply {
            setHasFixedSize(true)
            layoutManager = viewManager2
            adapter = viewAdapter2

        }.addOnItemTouchListener(RecyclerItemClickListener(this, student_attach_list, object : RecyclerItemClickListener.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                Toast.makeText(this@AssignmentWorkStd,"Long press to delete",Toast.LENGTH_LONG).show()

            }
            override fun onLongItemClick(view: View?, position: Int) {
                selectedFilePath.removeAt(position)
                viewAdapter2.notifyDataSetChanged()
                student_attach_list.adapter!!.notifyDataSetChanged()
            }
        }))
        add_attach.setOnClickListener {
            attachFile()
        }
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
                Log.i("j", "Selected File Path:$selectedFilePath")

                if (selectedFilePath.isNotEmpty() ) {
                    viewAdapter.notifyDataSetChanged()
                    student_attach_list.adapter!!.notifyDataSetChanged()
                } else {
                    Toast.makeText(this, "Cannot upload file to server", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    @SuppressLint("SetTextI18n")
    private fun initAssignDet(){
        viewManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        viewAdapter = AttachListAdapter(myDataSet,true)
        assign_attachment_list.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter

        }.addOnItemTouchListener(RecyclerItemClickListener(this, assign_attachment_list, object : RecyclerItemClickListener.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                FileLoader.with(this@AssignmentWorkStd)
                        .load(URLs[position], false)
                        .fromDirectory("el_Downloads", FileLoader.DIR_EXTERNAL_PUBLIC)
                        .asFile(object : FileRequestListener<File> {
                            override fun onLoad(request: FileLoadRequest, response: FileResponse<File>) {
                                val loadedFile = response.body as File
                                var isAccepted=false
                                for (i in Constants().acceptedExtList){
                                    if (i == RandomUtils.getFileExtension(RandomUtils.getFileName(loadedFile.absolutePath))){
                                        isAccepted=true
                                        val intent = Intent(this@AssignmentWorkStd,CompilerActivity::class.java)
                                        intent.putExtra("openFile",true)
                                        intent.putExtra("path", loadedFile.absolutePath)
                                        this@AssignmentWorkStd.startActivity(intent)
                                    }
                                }
                                if (!isAccepted){
                                    val uri = FileProvider.getUriForFile(this@AssignmentWorkStd,this@AssignmentWorkStd.packageName+".provider",loadedFile)
                                    val shareIntent = Intent(Intent.ACTION_VIEW)
                                    when {
                                        loadedFile.toString().contains(".doc") || loadedFile.toString().contains(".docx") -> // Word document
                                            shareIntent.setDataAndType(uri, "application/msword")
                                        loadedFile.toString().contains(".pdf") -> // PDF file
                                            shareIntent.setDataAndType(uri, "application/pdf")
                                        loadedFile.toString().contains(".ppt") || loadedFile.toString().contains(".pptx") -> // Powerpoint file
                                            shareIntent.setDataAndType(uri, "application/vnd.ms-powerpoint")
                                        loadedFile.toString().contains(".xls") || loadedFile.toString().contains(".xlsx") -> // Excel file
                                            shareIntent.setDataAndType(uri, "application/vnd.ms-excel")
                                        loadedFile.toString().contains(".zip") || loadedFile.toString().contains(".rar") -> // WAV audio file
                                            shareIntent.setDataAndType(uri, "application/x-wav")
                                        loadedFile.toString().contains(".rtf") -> // RTF file
                                            shareIntent.setDataAndType(uri, "application/rtf")
                                        loadedFile.toString().contains(".wav") || loadedFile.toString().contains(".mp3") -> // WAV audio file
                                            shareIntent.setDataAndType(uri, "audio/x-wav")
                                        loadedFile.toString().contains(".gif") -> // GIF file
                                            shareIntent.setDataAndType(uri, "image/gif")
                                        loadedFile.toString().contains(".jpg") || loadedFile.toString().contains(".jpeg") || loadedFile.toString().contains(".png") -> // JPG file
                                            shareIntent.setDataAndType(uri, "image/jpeg")
                                        loadedFile.toString().contains(".txt") -> // Text file
                                            shareIntent.setDataAndType(uri, "text/plain")
                                        loadedFile.toString().contains(".3gp") || loadedFile.toString().contains(".mpg") || loadedFile.toString().contains(".mpeg") || loadedFile.toString().contains(".mpe") || loadedFile.toString().contains(".mp4") || loadedFile.toString().contains(".avi") -> // Video files
                                            shareIntent.setDataAndType(uri, "video/*")
                                        else -> shareIntent.setDataAndType(uri, "*/*")
                                    }
                                    shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    try {
                                        this@AssignmentWorkStd.startActivity(Intent.createChooser(shareIntent,"choose app"))
                                    }catch (e:Exception){
                                        Toast.makeText(this@AssignmentWorkStd,e.message,Toast.LENGTH_LONG).show()
                                    }
                                }


                            }

                            override fun onError(request: FileLoadRequest, t: Throwable) {}
                        })
            }
            override fun onLongItemClick(view: View?, position: Int) {}
        }))

        fillAssignAttachment()
        point.text = assignment.points
        dueDate.text = "Due ${assignment.dueDate1}"
        announce_title.text=assignment.title
        description.text=assignment.desc

    }

    private fun fillAssignAttachment() {
        val stringRequest : StringRequest = object : StringRequest(Request.Method.POST, Constants().URL_GET_ATTACHMENT,
                Response.Listener {
                    try {
                        val jsonObject = JSONObject(it)
                        val arr = jsonObject.getJSONArray("attachments")
                        myDataSet.clear()
                        for (i in 0 until arr.length()){
                            val fileName = RandomUtils.getFileName(arr.getJSONObject(i).getString("attach_url"))
                            myDataSet.add(i,fileName )
                            URLs.add(i, arr.getJSONObject(i).getString("attach_url"))
                            if (i == arr.length()-1) {
                                viewAdapter.notifyDataSetChanged()
                               assign_attachment_list.adapter!!.notifyDataSetChanged()
                            }
                        }

                    }catch (e: Exception){
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener {
                    Toast.makeText(this,"Error ${it.message}", Toast.LENGTH_LONG).show()
                }) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["id"] = assignment.attach_id
                return params
            }
        }
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest)
    }
}

