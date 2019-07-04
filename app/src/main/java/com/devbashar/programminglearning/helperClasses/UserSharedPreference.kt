package com.devbashar.programminglearning.helperClasses

import android.annotation.SuppressLint
import android.content.Context


class UserSharedPreference constructor(val context: Context) {

    private val SHARED_PREF_NAME = "user_details"
    private val KEY_ID="USER_ID"
    private val KEY_USERNAME="USER_NAME"
    private val KEY_FIRST_NAME="USER_FIRST_NAME"
    private val KEY_LAST_NAME="USER_LAST_NAME"
    private val KEY_EMAIL="USER_EMAIL"
    private val KEY_PROF="USER_PROF"
    private val KEY_GENDER="USER_GENDER"
    private val KEY_CURRENT_USER_CLASSES="class_code"
    private val KEY_GROUP_ID = "groupID"
    private val currentOpenFilePath = "currentOpenFilePath"
    private val emptyList = mutableSetOf<String>()

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: UserSharedPreference? = null
        fun getInstance(context: Context) =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: UserSharedPreference(context).also {
                        INSTANCE = it
                    }
                }
    }

    fun setGroupID(id:String){
        val shared = context.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE)
        val editor = shared.edit()
        editor.putString(KEY_GROUP_ID,id)
        editor.apply()

    }
    fun getGroupID():String?{
        return context.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE).getString(KEY_GROUP_ID,"")
    }

    fun saveCurrentClasses(list :MutableSet<String>):Boolean{
        val shared = context.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE)
        val editor = shared.edit()
        editor.putStringSet(KEY_CURRENT_USER_CLASSES,list)
        editor.apply()
        return true
    }
    fun getCurrentClasses():MutableSet<String>?{
        return context.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE).getStringSet(KEY_CURRENT_USER_CLASSES,emptyList)
    }
    fun getProfession():String{
        return context.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE).getString(KEY_PROF,"student")!!
    }
    fun getFullName():String{
        return getCurrentUserDetails().first_name+" "+getCurrentUserDetails().last_name
    }

    fun userLogin(id:Int,username:String,email:String,first_name:String,last_name:String,prof:String,gender:String):Boolean{
        val shared = context.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE)
        val editor = shared.edit()
        editor.putInt(KEY_ID,id)
        editor.putString(KEY_USERNAME,username)
        editor.putString(KEY_EMAIL,email)
        editor.putString(KEY_FIRST_NAME,first_name)
        editor.putString(KEY_LAST_NAME,last_name)
        editor.putString(KEY_PROF,prof)
        editor.putString(KEY_GENDER,gender)

        editor.apply()
        return true
    }
    fun getCurrentUserDetails():User{
        val shared = context.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE)

        return User(shared.getInt(KEY_ID,0),
                shared.getString(KEY_USERNAME,""),
                shared.getString(KEY_EMAIL,""),
                shared.getString(KEY_FIRST_NAME,""),
                shared.getString(KEY_LAST_NAME,""),
                shared.getString(KEY_PROF,""),
                shared.getString(KEY_GENDER,"")
                )
    }
    fun isLoggedIn():Boolean{
        val shared = context.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE)
        if (shared.getString(KEY_USERNAME,null) != null ){
            return true
        }
        return false
    }
    fun logOut():Boolean{
        val shared = context.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE)
        val editor = shared.edit()
        editor.clear()
        editor.apply()
        return true
    }

}