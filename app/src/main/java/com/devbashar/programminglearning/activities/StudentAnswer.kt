package com.devbashar.programminglearning.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import com.devbashar.programminglearning.util.RandomUtils
import com.krishna.fileloader.FileLoader
import com.krishna.fileloader.listener.FileRequestListener
import com.krishna.fileloader.pojo.FileResponse
import com.krishna.fileloader.request.FileLoadRequest
import kotlinx.android.synthetic.main.activity_student_answer.*
import org.json.JSONObject
import java.io.File

class StudentAnswer : AppCompatActivity() {

    private lateinit var assignment: Assignment
    private lateinit var answer :StudentAnswer1
    private var URLs= mutableListOf<String>()

    private var myDataSet= mutableListOf<String>()
    private lateinit var viewAdapter: androidx.recyclerview.widget.RecyclerView.Adapter<*>
    private lateinit var viewManager: androidx.recyclerview.widget.RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_answer)
        assignment = this.intent.getParcelableExtra("assign") as Assignment
        initButton()
        getStudentAnswer()
    }

    private fun initButton() {
        submit_mark.setOnClickListener {
            if (!std_mark.text.toString().isEmpty()){
                setStudentMark(std_mark.text.toString())
            }else{
                std_mark.error="Please Add mark"
            }

        }
    }

    private fun setStudentMark(mark: String) {
        val stringRequest : StringRequest = object : StringRequest(Request.Method.POST, Constants().URL_SET_STUDENT_MARK,
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
                params["std_id"] = this@StudentAnswer.intent.getStringExtra("std_id")
                params["class_id"] = this@StudentAnswer.intent!!.getStringExtra("classID2")!!
                params["assignment_id"] = assignment.id1
                params["mark"] = mark
                return params
            }
        }
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest)
    }

    private fun getStudentAnswer() {
        val stringRequest : StringRequest = object : StringRequest(Request.Method.POST, Constants().URL_GET_ANSWER,
                Response.Listener {
                    try {
                        val jsonObject = JSONObject(it)
                        val arr = jsonObject.getJSONArray("answers")
                        answer = StudentAnswer1(arr.getJSONObject(0).getString("student_name"),
                                arr.getJSONObject(0).getString("student_grade"),
                                arr.getJSONObject(0).getString("attach_id"),
                                arr.getJSONObject(0).getString("answer_text"),
                                arr.getJSONObject(0).getString("answer_date"))
                        initTexts()
                        fillAttachList()
                    }catch (e: Exception){
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener {
                    Toast.makeText(this,"Error ${it.message}", Toast.LENGTH_LONG).show()
                }) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["std_id"] = this@StudentAnswer.intent.getStringExtra("std_id")
                params["class_id"] = this@StudentAnswer.intent!!.getStringExtra("classID2")!!
                params["assign_id"] = assignment.id1
                return params
            }
        }
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest)
    }

    private fun fillAttachList() {
        viewManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        viewAdapter = AttachListAdapter(myDataSet,true)
        fillAssignAttachment()
        std_attach_list.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter

        }.addOnItemTouchListener(RecyclerItemClickListener(this, std_attach_list, object : RecyclerItemClickListener.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                FileLoader.with(this@StudentAnswer)
                        .load(URLs[position], false)
                        .fromDirectory("el_Downloads", FileLoader.DIR_EXTERNAL_PUBLIC)
                        .asFile(object : FileRequestListener<File> {
                            override fun onLoad(request: FileLoadRequest, response: FileResponse<File>) {
                                val loadedFile = response.body as File
                                var isAccepted=false
                                for (i in Constants().acceptedExtList){
                                    if (i == RandomUtils.getFileExtension(RandomUtils.getFileName(loadedFile.absolutePath))){
                                        isAccepted=true
                                        val intent = Intent(this@StudentAnswer,CompilerActivity::class.java)
                                        intent.putExtra("openFile",true)
                                        intent.putExtra("path", loadedFile.absolutePath)
                                        this@StudentAnswer.startActivity(intent)
                                    }
                                }
                                if (!isAccepted){
                                    val uri = FileProvider.getUriForFile(this@StudentAnswer,this@StudentAnswer.packageName+".provider",loadedFile)
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
                                        this@StudentAnswer.startActivity(Intent.createChooser(shareIntent,"choose app"))
                                    }catch (e:Exception){
                                        Toast.makeText(this@StudentAnswer,e.message,Toast.LENGTH_LONG).show()
                                    }

                                }
                            }

                            override fun onError(request: FileLoadRequest, t: Throwable) {}
                        })
            }

            override fun onLongItemClick(view: View?, position: Int) {}
        }))
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
                                std_attach_list.adapter!!.notifyDataSetChanged()
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
                val params = java.util.HashMap<String, String>()
                params["id"] = answer.attach_id
                return params
            }
        }
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest)
    }

    private fun initTexts() {
        std_name.text=answer.name
        announce_title.text = assignment.title
        assign_point.text= assignment.points
        std_text.text=answer.answer_text
        if (answer.grade == "NO"){
            std_mark.setText("")
            std_mark.hint = "ADD MARK"
        }else{
            std_mark.setText(answer.grade)
        }
      answer_date.text=answer.answer_date

    }
}
class StudentAnswer1(val name:String,val grade:String,val attach_id:String,val answer_text:String,val answer_date:String)
