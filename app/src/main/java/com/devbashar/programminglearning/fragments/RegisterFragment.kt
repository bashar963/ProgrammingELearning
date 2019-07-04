package com.devbashar.programminglearning.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.devbashar.programminglearning.R
import com.devbashar.programminglearning.helperClasses.RequestHandler
import com.devbashar.programminglearning.helperClasses.Constants
import kotlinx.android.synthetic.main.fragment_register.*
import org.json.JSONObject
import java.lang.Exception
import java.util.HashMap
import java.util.regex.Matcher
import java.util.regex.Pattern

class RegisterFragment : androidx.fragment.app.Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        register.setOnClickListener {
           if (register()){

           }
            progressBar.visibility = View.INVISIBLE
        }
    }


    private fun register():Boolean{
        progressBar.visibility = View.VISIBLE
        val email = email.text.toString()
        val username = username.text.toString()
        val pass = password.text.toString()
        val repass = rePassword.text.toString()
        val firstName=first_name.text.toString()
        val lastName= last_name.text.toString()
        val gender = if (genderGroup.checkedRadioButtonId == R.id.male){
            "male"
        }else{
            "female"
        }
        val prof = if (professionGroup.checkedRadioButtonId == R.id.student){
            "student"
        }else
        {
            "teacher"
        }

        checkValidate(username,pass,repass)
            if (email.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || gender.isEmpty()){
                Toast.makeText(this.context,"Please fill all fields",Toast.LENGTH_LONG).show()
                return false
            }
         val stringRequest : StringRequest = object : StringRequest(Request.Method.POST, Constants().URL_REGISTER,
                 Response.Listener {
                     try {
                         val jsonObject = JSONObject(it)
                        val err = jsonObject.getString("msg")
                         when (err) {
                             "user registered successfully" -> Toast.makeText(this.context,"registered successfully now go and Login",Toast.LENGTH_LONG).show()
                             "error while registering user" -> {
                                 Toast.makeText(this.context,"Sorry we are having problem now to registering you, try later",Toast.LENGTH_LONG).show()
                             }
                             "the user is already exist" ->{
                                 Toast.makeText(this.context,"it seems that you already have account with same username or e-mail",Toast.LENGTH_LONG).show()
                             }
                             else -> {
                                 Toast.makeText(this.context,"Sorry!! we are having technical error and we are trying to solve it",Toast.LENGTH_LONG).show()
                             }
                         }
                     }catch (e:Exception){
                         e.printStackTrace()
                     }
                 },
                 Response.ErrorListener {
                     Toast.makeText(this.context,"Error ${it.message}",Toast.LENGTH_LONG).show()
                 }) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                    params["email"] = email
                    params["username"]= username
                    params["password"]=pass
                    params["first_name"]=firstName
                    params["last_name"]=lastName
                    params["prof"]=prof
                    params["gender"]=gender
                return params
            }
        }
        RequestHandler.getInstance(this.requireContext()).addToRequestQueue(stringRequest)

        return true
    }


    private fun checkValidate(username:String, password:String, rePass:String) : Boolean {

        if (!username.contains("[a-z]".toRegex())&&username.length < 3){
            this.username.error = "must contains latin characters and numbers and at least 3 characters"
            return false
        }
        if (password == rePass){
            if (password.length<8 && !isValidPassword(password)){
                this.password.error = "Invalid password must be greater than 8 characters and allowed spacial characters are @$#%^&+=!"
                return false
            }
        }else{
            rePassword.error = "the passwords are not same"
        }

        return true
    }

    private fun isValidPassword(password: String): Boolean {

        val pattern: Pattern
        val matcher: Matcher
        val passPattern = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$"
        pattern = Pattern.compile(passPattern)
        matcher = pattern.matcher(password)
        return matcher.matches()

    }


}
