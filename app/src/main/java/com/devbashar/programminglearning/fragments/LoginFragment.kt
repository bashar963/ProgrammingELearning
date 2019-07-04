package com.devbashar.programminglearning.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.devbashar.programminglearning.R
import com.devbashar.programminglearning.activities.CompilerActivity
import com.devbashar.programminglearning.activities.MainPageActivity
import com.devbashar.programminglearning.helperClasses.RequestHandler
import com.devbashar.programminglearning.helperClasses.UserSharedPreference
import com.devbashar.programminglearning.helperClasses.Constants
import kotlinx.android.synthetic.main.fragment_login.*
import org.json.JSONObject
import java.lang.Exception
import java.util.HashMap


class LoginFragment : androidx.fragment.app.Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        if (UserSharedPreference.getInstance(this.requireContext()).isLoggedIn()){
            val intent = Intent(this.context, MainPageActivity::class.java)
            activity!!.finish()
            startActivity(intent)
        }
        return inflater.inflate(R.layout.fragment_login, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        signIn.setOnClickListener {
            login()
        }
        compiler_login.setOnClickListener {
            val intent = Intent(this.context, CompilerActivity::class.java)
            startActivity(intent)
        }
        register.setOnClickListener{
            it.findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    private fun login(){
        val username = userName.text.toString()
        val pass = password.text.toString()


        val stringRequest : StringRequest = object : StringRequest(Method.POST, Constants().URL_LOGIN,
                Response.Listener {
                    try {
                        val jsonObject = JSONObject(it)
                        when (jsonObject.getString("msg")) {
                            "Login successfully" -> {
                                 UserSharedPreference.getInstance(this.requireContext()).userLogin(
                                        jsonObject.getInt("id"),
                                        jsonObject.getString("username"),
                                        jsonObject.getString("email"),
                                        jsonObject.getString("first_name"),
                                        jsonObject.getString("last_name"),
                                        jsonObject.getString("profession"),
                                        jsonObject.getString("gender"))

                                activity!!.finish()
                                val intent= Intent(this.context, MainPageActivity::class.java)
                                startActivity(intent)
                            }
                            "wrong username or password" -> {
                                Toast.makeText(this.context,"Sorry username or password is incorrect", Toast.LENGTH_LONG).show()
                            }
                            else -> {
                                Toast.makeText(this.context,"Sorry!! we are having technical error and we are trying to solve it", Toast.LENGTH_LONG).show()
                            }
                        }
                    }catch (e: Exception){
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener {
                    Toast.makeText(this.context,"Error ${it.message}", Toast.LENGTH_LONG).show()
                }) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["username"]= username
                params["password"]=pass
                return params
            }
        }
        RequestHandler.getInstance(this.requireContext()).addToRequestQueue(stringRequest)

    }

}
