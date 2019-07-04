package com.devbashar.programminglearning.fragments


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest

import com.devbashar.programminglearning.R
import com.devbashar.programminglearning.activities.AttachListAdapter
import com.devbashar.programminglearning.activities.CompilerActivity
import com.devbashar.programminglearning.helperClasses.Constants
import com.devbashar.programminglearning.helperClasses.RecyclerItemClickListener
import com.devbashar.programminglearning.helperClasses.RequestHandler
import com.devbashar.programminglearning.util.RandomUtils
import kotlinx.android.synthetic.main.fragment_instructions.*
import org.json.JSONObject
import java.util.HashMap
import com.krishna.fileloader.request.FileLoadRequest
import com.krishna.fileloader.pojo.FileResponse
import com.krishna.fileloader.listener.FileRequestListener
import com.krishna.fileloader.FileLoader
import java.io.File
import java.net.URI


class Instructions : Fragment() {

    private lateinit var assignment:Assignment
    private var myDataSet= mutableListOf<String>()
    private var URLs= mutableListOf<String>()
    private lateinit var viewAdapter: androidx.recyclerview.widget.RecyclerView.Adapter<*>
    private lateinit var viewManager: androidx.recyclerview.widget.RecyclerView.LayoutManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_instructions, container, false)
    companion object { fun newInstance(): Instructions = Instructions() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        assignment = this.activity!!.intent.getParcelableExtra("assignment") as Assignment
        initAssignment()
    }
    private  fun  initAssignment(){
        dueDate.text=assignment.dueDate1
        title.text = assignment.title
        descr.text=assignment.desc
        initAttachList()
    }
    private fun initAttachList(){
        viewManager = androidx.recyclerview.widget.LinearLayoutManager(this.requireContext())
        viewAdapter = AttachListAdapter(myDataSet,true)
        fillAssignAttachment()
        attachment_list.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter

        }.addOnItemTouchListener(RecyclerItemClickListener(this.requireContext(), attachment_list, object : RecyclerItemClickListener.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                FileLoader.with(this@Instructions.requireContext())
                        .load(URLs[position], false)
                        .fromDirectory("el_Downloads", FileLoader.DIR_EXTERNAL_PUBLIC)
                        .asFile(object : FileRequestListener<File> {
                            override fun onLoad(request: FileLoadRequest, response: FileResponse<File>) {
                                val loadedFile = response.body as File
                                var isAccepted=false
                                for (i in Constants().acceptedExtList){
                                    if (i == RandomUtils.getFileExtension(RandomUtils.getFileName(loadedFile.absolutePath))){
                                        isAccepted=true
                                        val intent = Intent(this@Instructions.requireContext(),CompilerActivity::class.java)
                                        intent.putExtra("openFile",true)
                                        intent.putExtra("path", loadedFile.absolutePath)
                                        this@Instructions.requireActivity().startActivity(intent)
                                    }
                                }
                                if (!isAccepted){
                                    val uri = FileProvider.getUriForFile(this@Instructions.requireContext(),this@Instructions.requireActivity().packageName+".provider",loadedFile)
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
                                        this@Instructions.startActivity(Intent.createChooser(shareIntent,"choose app"))
                                    }catch (e:Exception){
                                        Toast.makeText(this@Instructions.requireContext(),e.message,Toast.LENGTH_LONG).show()
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
                                attachment_list.adapter!!.notifyDataSetChanged()
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
                params["id"] = assignment.attach_id
                return params
            }
        }
        RequestHandler.getInstance(this.requireContext()).addToRequestQueue(stringRequest)
    }
}
