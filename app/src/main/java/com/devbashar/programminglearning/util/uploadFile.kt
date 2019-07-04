package com.devbashar.programminglearning.util

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.devbashar.programminglearning.helperClasses.Constants
import com.devbashar.programminglearning.helperClasses.RequestHandler
import java.io.*
import java.lang.ref.WeakReference
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.HashMap





class UploadFile(val context:WeakReference<Context>): AsyncTask<String, Void, Int>() {

    override fun doInBackground(vararg params: String?): Int {
        var serverResponseCode = 0

        val connection: HttpURLConnection
        val dataOutputStream: DataOutputStream
        val lineEnd = "\r\n"
        val twoHyphens = "--"
        val boundary = "*****"


        var bytesRead: Int
        var bytesAvailable: Int
        var bufferSize: Int
        val buffer: ByteArray
        val maxBufferSize = 1 * 1024 * 1024
        val selectedFile = File(params[0])


        val parts =params[0]!!.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
       val  fileName = parts[parts.size - 1]

        if (!selectedFile.isFile) {

            return 0
        } else {
            try {
                val fileInputStream = FileInputStream(selectedFile)
                val url = URL(Constants().URL_UPLOAD_FILE)
                connection = url.openConnection() as HttpURLConnection
                connection.doInput = true//Allow Inputs
                connection.doOutput = true//Allow Outputs
                connection.useCaches = false//Don't use a cached Copy
                connection.requestMethod = "POST"
                connection.setRequestProperty("Connection", "Keep-Alive")
                connection.setRequestProperty("ENCTYPE", "multipart/form-data")
                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=$boundary")
                connection.setRequestProperty("file", params[0])


                dataOutputStream = DataOutputStream(connection.outputStream)

                dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd)
                dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\""
                        + params[0] + "\"" + lineEnd)

                dataOutputStream.writeBytes(lineEnd)


                bytesAvailable = fileInputStream.available()

                bufferSize = Math.min(bytesAvailable, maxBufferSize)

                buffer = ByteArray(bufferSize)

                bytesRead = fileInputStream.read(buffer, 0, bufferSize)

                while (bytesRead > 0) {
                    dataOutputStream.write(buffer, 0, bufferSize)
                    bytesAvailable = fileInputStream.available()
                    bufferSize = Math.min(bytesAvailable, maxBufferSize)
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize)
                }

                dataOutputStream.writeBytes(lineEnd)
                dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd)

                serverResponseCode = connection.responseCode
                val serverResponseMessage = connection.responseMessage

                Log.i("upload", "Server Response is: $serverResponseMessage: $serverResponseCode")

                if (serverResponseCode==200){
                    val url1 = "http://178.62.87.100/project/v1/uploads/$fileName"
                    createAttach(url1,params[1]!!)
                }

                fileInputStream.close()
                dataOutputStream.flush()
                dataOutputStream.close()


            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: MalformedURLException) {
                e.printStackTrace()

            } catch (e: IOException) {
                e.printStackTrace()
            }

            return serverResponseCode
        }
    }

    override fun onPostExecute(result: Int?) {


    }
    private fun createAttach(url: String, id: String) {
        val stringRequest : StringRequest = object : StringRequest(Request.Method.POST, Constants().URL_CREATE_ATTACHMENT,
                Response.Listener {
                    try {
                        // val jsonObject = JSONObject(it)
                        // val msg = jsonObject.getString("msg")

                    }catch (e: Exception){
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener {

                }) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["id"] = id
                params["url"] = url
                return params
            }
        }
        RequestHandler.getInstance(context.get()!!).addToRequestQueue(stringRequest)
    }
}