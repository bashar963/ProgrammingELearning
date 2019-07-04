package com.devbashar.programminglearning.util


import com.devbashar.programminglearning.helperClasses.Constants
import java.io.*


object FileIOUtil {
    @Throws(IOException::class)
    fun readAllText(filePath: String,fileExt: String): CharSequence? {
        for (i in Constants().acceptedExtList){
            if (i == fileExt){
                try {
                return  File(filePath).readText(charset = Charsets.UTF_8)
                } catch (e: Exception) {
                    throw e
                }
            }
        }
       return null
    }
    @Throws(IOException::class)
    fun writeAllText(filePath: String, contents: String): Boolean {
        val out = BufferedWriter(OutputStreamWriter(FileOutputStream(filePath), "UTF-8"))
        out.write(contents)
        out.close()
        return true
    }
}
