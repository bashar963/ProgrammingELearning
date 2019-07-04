package com.devbashar.programminglearning.util

object RandomUtils {
    fun getFileExtension(fileName: String): String {
        val dotIndex = fileName.lastIndexOf(".")
        if (dotIndex == -1)
            return ""
        return fileName.substring(dotIndex + 1, fileName.length)
    }
    fun getFileName(filePath:String):String{
        return  filePath.subSequence(filePath.lastIndexOf('/')+1,filePath.lastIndex+1).toString()
    }
}
