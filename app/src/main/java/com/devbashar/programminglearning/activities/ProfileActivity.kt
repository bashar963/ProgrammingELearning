package com.devbashar.programminglearning.activities

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.devbashar.programminglearning.R
import com.devbashar.programminglearning.helperClasses.Constants
import com.devbashar.programminglearning.helperClasses.RequestHandler
import kotlinx.android.synthetic.main.activity_profile.*
import org.json.JSONObject
import java.util.HashMap

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        initSocial()
        initBtn()
    }


    private fun getID():String{
        return intent.getStringExtra("userID")
    }

    private fun initSocial() {
        val stringRequest : StringRequest = @SuppressLint("SetTextI18n")
        object : StringRequest(Request.Method.POST, Constants().URL_GET_USER_DET,
                Response.Listener {
                    try {
                        val jsonObject = JSONObject(it)
                        facebook_email.text = jsonObject.getString("facebook_id")
                        twitter_email.text = jsonObject.getString("twitter_id")
                        skype_email.text = jsonObject.getString("skype_id")
                        profile_image.setImageURI("http://178.62.87.100/project/profiledemo.png")
                        profile_name.text = jsonObject.getString("username")
                        profile_email.text = jsonObject.getString("email")
                        profile_gender.text = jsonObject.getString("gender")
                        profile_full_name.text = jsonObject.getString("first_name")+" "+jsonObject.getString("last_name")
                        profile_work.text= jsonObject.getString("profession")
                    }catch (e: Exception){
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener {
                    Toast.makeText(this,"Error ${it.message}", Toast.LENGTH_LONG).show()
                }) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["id"] = getID()
                return params
            }
        }
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest)
    }

    private fun initBtn() {

        soc_facebook.setOnClickListener {
            if (facebook_email.text.toString().equals("null",ignoreCase = true)){
              Toast.makeText(this,"no facebook account",Toast.LENGTH_SHORT).show()

            }else{
                if (twitter_email.text.toString().contains("/")){
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(facebook_email.text.toString()))
                    startActivity(browserIntent)
                }else{
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.facebook.com/${twitter_email.text}"))
                    startActivity(browserIntent)
                }
            }
        }
        soc_twitter.setOnClickListener {
            if (twitter_email.text.toString().equals("null",ignoreCase = true)){
                Toast.makeText(this,"no twitter account",Toast.LENGTH_SHORT).show()
            }else{
                if (twitter_email.text.toString().contains("/")){
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(twitter_email.text.toString()))
                    startActivity(browserIntent)
                }else{
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.twitter.com/${twitter_email.text}"))
                    startActivity(browserIntent)
                }

            }
        }
        soc_skype.setOnClickListener {
            if (skype_email.text .toString().equals("null",ignoreCase = true)){
                Toast.makeText(this,"no skype account",Toast.LENGTH_SHORT).show()
            }else{
                val clipboard = this.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                val clip = ClipData.newPlainText("skype id", skype_email.text.toString())
                clipboard.primaryClip = clip
                Toast.makeText(this,"the skype id/email has been copied to your clipboard", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
