package com.devbashar.programminglearning.activities


import android.Manifest
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.devbashar.programminglearning.helperClasses.RequestHandler
import com.devbashar.programminglearning.helperClasses.Constants
import kotlinx.android.synthetic.main.activity_compiler.*
import org.json.JSONObject
import xyz.iridiumion.iridiumhighlightingeditor.editor.IridiumHighlightingEditorJ
import xyz.iridiumion.iridiumhighlightingeditor.highlightingdefinitions.ExtList
import xyz.iridiumion.iridiumhighlightingeditor.highlightingdefinitions.HighlightingDefinitionLoader
import java.util.HashMap
import android.util.TypedValue
import android.view.*
import android.widget.EditText
import com.devbashar.programminglearning.R
import com.devbashar.programminglearning.helperClasses.RecyclerItemClickListener
import com.devbashar.programminglearning.util.FileIOUtil
import com.devbashar.programminglearning.util.RandomUtils
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog
import com.github.javiersantos.materialstyleddialogs.enums.Style
import com.github.jksiezni.permissive.Permissive
import com.nbsp.materialfilepicker.MaterialFilePicker
import com.nbsp.materialfilepicker.ui.FilePickerActivity
import kotlinx.android.synthetic.main.outputlayout.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import kotlin.Exception
import java.util.*
import kotlin.concurrent.schedule

class CompilerActivity : AppCompatActivity() {


    private lateinit var viewAdapter: androidx.recyclerview.widget.RecyclerView.Adapter<*>
    private lateinit var viewManager: androidx.recyclerview.widget.RecyclerView.LayoutManager
    private val myDataSet = Array(28){"->"}
    private val tag = "Editor"
    private var currentOpenFilePath: String? = null
    private var languageID = "c"
    private var requestId = ""
    private var stdInput=""
    private var isInput=false
    private var currentFontSize = 12f
    private val apiKey = "guest"
    private var imm: InputMethodManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compiler)
        title = "Compiler"
        initBtnItems()
        activity_main.panelHeight=0
        initQuickList()
        initEditor()
        fontBtnInit()
        initButtons()
        fileReceived()
        if (intent.getBooleanExtra("openFile",false)){
            currentOpenFilePath= intent.getStringExtra("path")
            val selectedFileExt = RandomUtils.getFileExtension(currentOpenFilePath!!)
            try {
                var fileContent = FileIOUtil.readAllText(currentOpenFilePath!!,selectedFileExt)
                if (fileContent.isNullOrBlank()){
                    showExceptionDialog(noException = true)
                }else{

                    fileContent = fileContent.replace("\t".toRegex(),"    ")
                    editor.setText(fileContent)

                    addExtToSpinner(selectedFileExt)
                    val definitionLoader = HighlightingDefinitionLoader()
                    val highlightingDefinition = definitionLoader.selectDefinitionFromFileExtension(selectedFileExt)
                    editor.loadHighlightingDefinition(highlightingDefinition)
                    Toast.makeText(this, "File loaded", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                showExceptionDialog(e)
            }
        }
    }

    private fun fileReceived() {
        if (intent.action == null){
            return
        }
        if (intent.action!!.compareTo(Intent.ACTION_VIEW) == 0) {
            val scheme = intent.scheme
            val resolver = contentResolver

            when {
                scheme!!.compareTo(ContentResolver.SCHEME_CONTENT) == 0 -> {
                    val uri = intent.data
                    val name = getContentName(resolver, uri!!)

                    val input = resolver.openInputStream(uri)
                    val importFilePath = Environment.getExternalStorageDirectory().path+"/"+name
                    inputStreamToFile(input, importFilePath)
                    currentOpenFilePath= importFilePath
                    val selectedFileExt = RandomUtils.getFileExtension(currentOpenFilePath!!)
                    try {
                        var fileContent = FileIOUtil.readAllText(currentOpenFilePath!!,selectedFileExt)
                        if (fileContent.isNullOrBlank()){
                            showExceptionDialog(noException = true)
                        }else{

                            fileContent = fileContent.replace("\t".toRegex(),"    ")
                            editor.setText(fileContent)

                            addExtToSpinner(selectedFileExt)
                            val definitionLoader = HighlightingDefinitionLoader()
                            val highlightingDefinition = definitionLoader.selectDefinitionFromFileExtension(selectedFileExt)
                            editor.loadHighlightingDefinition(highlightingDefinition)
                            Toast.makeText(this, "File loaded", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        showExceptionDialog(e)
                    }
                }
                scheme.compareTo(ContentResolver.SCHEME_FILE) == 0 -> {
                    val uri = intent.data
                    val name = uri!!.lastPathSegment

                    val input = resolver.openInputStream(uri)
                    val importFilePath = Environment.getExternalStorageDirectory().path+"/"+name
                    inputStreamToFile(input, importFilePath)
                    currentOpenFilePath= importFilePath
                    val selectedFileExt = RandomUtils.getFileExtension(currentOpenFilePath!!)
                    try {
                        var fileContent = FileIOUtil.readAllText(currentOpenFilePath!!,selectedFileExt)
                        if (fileContent.isNullOrBlank()){
                            showExceptionDialog(noException = true)
                        }else{

                            fileContent = fileContent.replace("\t".toRegex(),"    ")
                            editor.setText(fileContent)

                            addExtToSpinner(selectedFileExt)
                            val definitionLoader = HighlightingDefinitionLoader()
                            val highlightingDefinition = definitionLoader.selectDefinitionFromFileExtension(selectedFileExt)
                            editor.loadHighlightingDefinition(highlightingDefinition)
                            Toast.makeText(this, "File loaded", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        showExceptionDialog(e)
                    }
                }

            }
        }
    }


    private fun getContentName(resolver:ContentResolver, uri: Uri):String?{
        val cursor = resolver.query(uri, null, null, null, null)!!
        cursor.moveToFirst()
        val nameIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
        val txt:String?
        txt = if (nameIndex >= 0) {
            cursor.getString(nameIndex)
        } else {
            null
        }
        cursor.close()
        return txt
    }

    private fun inputStreamToFile(inp: InputStream?, file:String) {
        try {
            val out = FileOutputStream(File(file))

            var size: Int
            val buffer = ByteArray(1024)
            do {
                size = inp!!.read(buffer)
                out.write(buffer, 0, size)
            }while (size != -1)


            out.close()
        } catch (e:Exception) {

        }
    }

        // menu functions
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
       menuInflater.inflate(R.menu.compiler_menu,menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId){
            R.id.compiler_save_file ->{
                saveOpenFile()
            }
            R.id.compiler_open_file ->{
                openFile()
            }
            R.id.compiler_save_file_as->{
                showSaveFileAsDialog()
            }
            R.id.compiler_close_file->{
                closeCurrentFile()
            }
            R.id.open_output_layout->{activity_main.panelHeight=250}
            R.id.compiler_copy->{
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                val clip = android.content.ClipData.newPlainText("code", editor.text.toString())
                clipboard.primaryClip = clip
                Toast.makeText(this,"Code has been copied into clipBoard",Toast.LENGTH_SHORT).show()
            }
            R.id.compiler_paste->{
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                val clipData = clipboard.primaryClip
                if (clipData != null)
                editor.append(clipData.getItemAt(0).text.toString())

            }
            R.id.compiler_share->{
                shareButtonClicked()
            }

        }
        return super.onOptionsItemSelected(item)
    }

    //end of menu functions

    //Run code functions
    private fun runCode(inputStdin:String){
        runningBar.visibility= View.VISIBLE
        val stringRequest : StringRequest = object : StringRequest(
            Request.Method.POST, Constants().URL_COMPILER_API_CREATE,
            Response.Listener {
                try {
                    val jsonObject = JSONObject(it)
                    requestId = jsonObject.getString("id")
                    if(jsonObject.has("error")){
                        Toast.makeText(this,jsonObject.getString("error").toString(),Toast.LENGTH_LONG).show()
                    }else{
                        getStatus(requestId)

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
                params["source_code"] = editor.text.toString()
                params["api_key"]= apiKey
                params["language"] = languageID
                if (isInput){
                    params["input"]= inputStdin
                }
                return params
            }
        }
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest)

    }

    private fun getStatus(id:String) {
        val stringRequest : StringRequest = object : StringRequest(
                Request.Method.GET, Constants().URL_COMPILER_API_STATUS+"?api_key=guest&id=$id",
                Response.Listener {
                    try {
                        val jsonObject = JSONObject(it)
                        if (jsonObject.getString("status") == "running"){
                            Timer().schedule(500){  getStatus(id) }
                        }else{
                            showOutPutDialog(id)
                        }

                    }catch (e: Exception){
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener {
                    Toast.makeText(this,"Error ${it.message}", Toast.LENGTH_LONG).show()
                }) {
        }
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest)
    }

    //end of Run code functions

    //Open File functions
    private fun openFile() {
        Permissive.Request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .whenPermissionsGranted { browseForFile() }
                .whenPermissionsRefused {
                    if (!Permissive.checkPermission(this@CompilerActivity,Manifest.permission.READ_EXTERNAL_STORAGE)){
                        MaterialStyledDialog.Builder(this@CompilerActivity)
                                .setTitle("Permission not granted")
                                .setStyle(Style.HEADER_WITH_TITLE)
                                .setDescription("the compiler needs your permission to load and save files. Please grant this permission.")
                                .setPositiveText("Got it")
                                .show()
                    }
                }
                .execute(this@CompilerActivity)
    }
    private fun browseForFile() {
        MaterialFilePicker()
                .withActivity(this)
                .withRequestCode(1)
                .start()
    }

    //end of Open File functions

    //Close file fun
    private fun closeCurrentFile(){
        if (!currentOpenFilePath.isNullOrEmpty()){
            saveOpenFile(false,false)
            currentOpenFilePath=null
            editor.setText("")
        }

    }
    // end of close file fun

    //show Dialogs functions
    private fun showOutPutDialog(id:String){
        val stringRequest : StringRequest = object : StringRequest(
                Request.Method.GET, Constants().URL_COMPILER_API_DETAILS+"?api_key=guest&id=$id",
                Response.Listener {
                    try {
                        val jsonObject = JSONObject(it)
                        val stderr = jsonObject.getString("stderr")
                        val buildStderr = jsonObject.getString("build_stderr")
                        val output = jsonObject.getString("stdout")
                        runningBar.visibility= View.GONE
                        activity_main.panelHeight=250
                        if ((stderr == "null"||stderr=="") && (buildStderr == "null"||buildStderr=="")){
                            outPut.text = output
                        }else{
                            if (stderr == "null"){
                                outPut.text = buildStderr
                            }else{
                                outPut.text = stderr
                            }
                        }


                    }catch (e: Exception){
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener {
                    Toast.makeText(this,"Error ${it.message}", Toast.LENGTH_LONG).show()
                }) {
        }
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest)

    }
    private fun showSaveFileAsDialog(){
        val  customView = View.inflate(this,R.layout.dialog_save_file_path, null)
        val saveDirectoryInput: EditText?
        if (customView != null) {
            saveDirectoryInput = customView.findViewById(R.id.sfad_dir_path)
            saveDirectoryInput!!.setText(Environment.getExternalStorageDirectory().path)
        }
        MaterialStyledDialog.Builder(this)
                .setTitle("Save File As")
                .setStyle(Style.HEADER_WITH_TITLE)
                .setCustomView(customView)
                .setPositiveText("Save As")
                .setNegativeText("Cancel")
                .onPositive { dialog, _ ->
                    val view = dialog.customView ?: return@onPositive
                    val saveDirectoryInput1 = (view.findViewById(R.id.sfad_dir_path) as EditText).text.toString()
                    val saveFileName = (view.findViewById(R.id.sfad_file_name) as EditText).text.toString()
                    currentOpenFilePath = File(saveDirectoryInput1, saveFileName).absolutePath
                    saveOpenFile()

                }
                .show()
    }
    private fun showExceptionDialog(e: Exception = Exception(),noException :Boolean = false) {
        if (noException){
            MaterialStyledDialog.Builder(this)
                    .setTitle("Oops!")
                    .setStyle(Style.HEADER_WITH_TITLE)
                    .setDescription(String.format("An unexpected error occurred: File extension not supported"))
                    .setPositiveText("Got it")
                    .show()
        }else{
            MaterialStyledDialog.Builder(this)
                    .setTitle("Oops!")
                    .setStyle(Style.HEADER_WITH_TITLE)
                    .setDescription(String.format("An unexpected error occurred: %s", if (e.message == null)
                        e.toString()
                    else
                        e.message))
                    .setPositiveText("Got it")
                    .show()
        }

    }
    private fun showInputDialog() {
        val promptsView = View.inflate(this, R.layout.compiler_run_dialog, null)
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setView(promptsView)
        val userInput = promptsView.findViewById(R.id.stdin_input) as EditText

        alertDialogBuilder.setTitle("Inputs")
                .setCancelable(false)
                .setPositiveButton("OK") { dialog, _ ->
                    stdInput=userInput.text.toString()
                    isInput=true
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    input.isChecked=false
                    dialog.dismiss()
                }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    //end of show Dialogs functions

    //save Files function
    private fun saveOpenFile() {
        saveOpenFile(true, true)
    }
    private fun saveOpenFile(showErrorIfAccident: Boolean, showToast: Boolean) {
        if (currentOpenFilePath.isNullOrEmpty() && showErrorIfAccident) {
            showSaveFileAsDialog()
            return
        }
        Permissive.Request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .whenPermissionsGranted {

                    val textToSave = editor.text.toString()
                    try {
                        if (currentOpenFilePath!=null)
                        FileIOUtil.writeAllText(currentOpenFilePath!!, textToSave)
                        if (showToast)
                            Toast.makeText(this@CompilerActivity, "File saved", Toast.LENGTH_SHORT).show()
                    } catch (e: IOException) {
                        showExceptionDialog(e)
                    }
                }
                .whenPermissionsRefused {
                    if (!Permissive.checkPermission(this@CompilerActivity,Manifest.permission.READ_EXTERNAL_STORAGE)){
                        MaterialStyledDialog.Builder(this@CompilerActivity)
                                .setTitle("Permission not granted")
                                .setStyle(Style.HEADER_WITH_TITLE)
                                .setDescription("Compiler needs your permission to load and save files. Please grant this permission in settings.")
                                .setPositiveText("Got it")
                                .show()
                    }
                }
                .execute(this@CompilerActivity)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            val selectedFilePath = data!!.getStringExtra(FilePickerActivity.RESULT_FILE_PATH)
            val selectedFileExt = RandomUtils.getFileExtension(selectedFilePath)
            //Load file into editor
            try {
                var fileContent = FileIOUtil.readAllText(selectedFilePath,selectedFileExt)
                if (fileContent.isNullOrBlank()){
                    showExceptionDialog(noException = true)
                }else{

                    fileContent = fileContent.replace("\t".toRegex(),"    ")
                    editor.setText(fileContent)

                    addExtToSpinner(selectedFileExt)
                    val definitionLoader = HighlightingDefinitionLoader()
                    val highlightingDefinition = definitionLoader.selectDefinitionFromFileExtension(selectedFileExt)
                    editor.loadHighlightingDefinition(highlightingDefinition)
                    currentOpenFilePath = selectedFilePath
                    Toast.makeText(this, "File loaded", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                showExceptionDialog(e)
            }

        }
    }

    //end of save Files function

    //Helper functions
    private fun languageIDSelection(position: Int){
        when(position){
            0->languageID = "c"
            1->languageID = "objective-c"
            2->languageID = "swift"
            3->languageID = "cpp"
            4->languageID = "csharp"
            5-> languageID = "kotlin"
            6-> languageID="python"
            7-> languageID="python3"
            8-> languageID="php"
            9-> languageID="java"
            10-> languageID="javascript"
            11-> languageID="mysql"
            12-> languageID="plain"
        }

        editor.loadHighlightingDefinition(HighlightingDefinitionLoader().selectDefinitionFromFileExtension(ExtList.Java.ext))
        editor.updateHighlighting()

    }
    private fun addExtToSpinner(ext:String){
        when(ext){
            "java"->{
                languagesSpinner.setSelection(9)
                languageID="java"
            }
            "js"->{
                languagesSpinner.setSelection(10)
                languageID="javascript"
            }
            "mysql"->{
                languagesSpinner.setSelection(11)
                languageID="mysql"
            }
            "txt"->{
                languagesSpinner.setSelection(12)
                languageID="plain"
            }
            "php"->{
                languagesSpinner.setSelection(8)
                languageID="php"}
            "kt"->{
                languagesSpinner.setSelection(5)
                languageID="kotlin"
            }
            "py"->{
                languagesSpinner.setSelection(7)
                languageID="python3"
            }
            "cpp"->{
                languagesSpinner.setSelection(3)
                languageID="cpp"
            }
            "c"->{
                languagesSpinner.setSelection(0)
                languageID="c"
            }
            "cs"->{
                languagesSpinner.setSelection(4)
                languageID="csharp"
            }
            "m"->{
                languagesSpinner.setSelection(1)
                languageID="objective-c"
            }

        }
    }
    private fun quickButtonsInit(position: Int){
        if (myDataSet[position]=="->"){
            val start = Math.max(editor.selectionStart, 0)
            val end = Math.max(editor.selectionEnd, 0)
            editor.text.replace(Math.min(start, end), Math.max(start, end), editor.insertTab().toString().removeRange(0,11), 0,  editor.insertTab().toString().removeRange(0,11).length)

        }else{
            val start = Math.max(editor.selectionStart, 0)
            val end = Math.max(editor.selectionEnd, 0)
            editor.text.replace(Math.min(start, end), Math.max(start, end), myDataSet[position], 0, myDataSet[position].length)
        }
    }
    //end of Helper functions

    // init functions
    private fun initBtnItems() {
        myDataSet[1]=";"
        myDataSet[2]="("
        myDataSet[3]=")"
        myDataSet[4]="{"
        myDataSet[5]="}"
        myDataSet[6]="["
        myDataSet[7]="]"
        myDataSet[8]="<"
        myDataSet[9]=">"
        myDataSet[10]="\""
        myDataSet[11]="/"
        myDataSet[12]="\\"
        myDataSet[13]="="
        myDataSet[14]="'"
        myDataSet[15]="|"
        myDataSet[16]="&"
        myDataSet[17]="."
        myDataSet[18]="!"
        myDataSet[19]="#"
        myDataSet[20]="$"
        myDataSet[21]=":"
        myDataSet[22]="*"
        myDataSet[23]="-"
        myDataSet[24]="+"
        myDataSet[25]="^"
        myDataSet[26]="@"
        myDataSet[27]="?"
    }
    private fun fontBtnInit(){
        clearAll.setOnClickListener {
            editor.setText("")
        }
        textDec.setOnClickListener {
            if (currentFontSize>12f){
                currentFontSize--
                editor.setTextSize(TypedValue.COMPLEX_UNIT_SP,currentFontSize-1)
            }
        }
        textInc.setOnClickListener {
            if (currentFontSize<30f){
                currentFontSize++
                editor.setTextSize(TypedValue.COMPLEX_UNIT_SP,currentFontSize+1)
            }
        }
    }
    private fun initQuickList(){
        viewManager = androidx.recyclerview.widget.LinearLayoutManager(this, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false)
        viewAdapter = MenuCompilerAdapter(myDataSet)
        quickBtnList.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter

        }
        quickBtnList.addOnItemTouchListener(
                RecyclerItemClickListener(this, quickBtnList, object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) { quickButtonsInit(position) }
                    override fun onLongItemClick(view: View?, position: Int) {}
                })
        )
    }
    private fun initEditor(){
        imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val definition = HighlightingDefinitionLoader().selectDefinitionFromFileExtension(ExtList.Java.ext)
        editor.loadHighlightingDefinition(definition)
        try{
            editor.setOnTextChangedListener( object : IridiumHighlightingEditorJ.OnTextChangedListener{
                override fun onTextChanged(text: String) {

                }
            }  )
        } catch (e:ClassCastException) {
            Log.e(tag,e.message)
            throw ClassCastException(this.toString() + " must implement " + "ShaderEditor.OnTextChangedListener")
        }
    }
    private fun initButtons(){
        input.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked){
                isInput=false
            }else{
                showInputDialog()
            }
        }
        languagesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                languageIDSelection(position)
            }
        }

        run_code.setOnClickListener { runCode(stdInput) }
        close_output.setOnClickListener { activity_main.panelHeight=0 }
    }
    private fun shareButtonClicked(){
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        val shareBody = editor.text.toString()
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Code")
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
        startActivity(Intent.createChooser(sharingIntent, "Share via"))
    }
    //end of init functions

    // End of class


}

class MenuCompilerAdapter (private val myDataSet: Array<String>): androidx.recyclerview.widget.RecyclerView.Adapter<MenuCompilerAdapter.ViewHolder>(){


    class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        var btn: Button = view.findViewById(R.id.btn) as Button

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val btn = LayoutInflater.from(parent.context).inflate(R.layout.adapter_menu_btn, parent, false)
        return ViewHolder(btn)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.btn.text = myDataSet[position]
    }
    override fun getItemCount() = myDataSet.size

}
