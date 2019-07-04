package com.devbashar.programminglearning.fragments


import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.devbashar.programminglearning.R
import com.devbashar.programminglearning.helperClasses.Constants
import com.devbashar.programminglearning.helperClasses.RequestHandler
import com.devbashar.programminglearning.helperClasses.User
import com.devbashar.programminglearning.helperClasses.UserSharedPreference
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog
import com.github.javiersantos.materialstyleddialogs.enums.Style
import kotlinx.android.synthetic.main.fragment_profile.*
import org.json.JSONObject
import java.util.HashMap
import android.content.Intent
import android.net.Uri
import android.content.ClipData
import android.content.Context




class Profile : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_profile, container, false)


    private lateinit var user: User
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        user = UserSharedPreference.getInstance(this.requireContext()).getCurrentUserDetails()
        profile_image.setActualImageResource(R.drawable.profiledemo)
        profile_name.text = user.username
        profile_email.text = user.email
        profile_gender.text = user.gender
        profile_full_name.text = user.first_name+" "+user.last_name
        profile_work.text= user.prof
        initSocial()
        initBtn()
    }
    private fun getID():String{
       return user.id.toString()
    }

    private fun initSocial() {
        val stringRequest : StringRequest = object : StringRequest(Request.Method.POST, Constants().URL_GET_USER_DET,
                Response.Listener {
                    try {
                        val jsonObject = JSONObject(it)
                        facebook_email.text = jsonObject.getString("facebook_id")
                        twitter_email.text = jsonObject.getString("twitter_id")
                        skype_email.text = jsonObject.getString("skype_id")
                    }catch (e: Exception){
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener {
                    Toast.makeText(this.requireContext(),"Error ${it.message}", Toast.LENGTH_LONG).show()
                }) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["id"] = getID()
                return params
            }
        }
        RequestHandler.getInstance(this.requireContext()).addToRequestQueue(stringRequest)
    }

    private fun setSocial(f_id:String = "NULL",t_id:String="NULL",s_id:String="NULL"){
        var which = ""
        when {
            f_id!="NULL" -> which=  "facebook"
            t_id!="NULL" -> which ="twitter"
            s_id!="NULL" -> which= "skype"
        }
        val stringRequest : StringRequest = object : StringRequest(Request.Method.POST, Constants().URL_ADD_SOCIAL,
                Response.Listener {
                    try {
                        initSocial()
                    }catch (e: Exception){
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener {
                    Toast.makeText(this.requireContext(),"Error ${it.message}", Toast.LENGTH_LONG).show()
                }) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["id"] = getID()
                params["f_id"]=f_id
                params["t_id"]=t_id
                params["s_id"] = s_id
                params["which"] = which
                return params
            }
        }
        RequestHandler.getInstance(this.requireContext()).addToRequestQueue(stringRequest)
    }
    private fun initBtn() {

        soc_facebook.setOnClickListener {
            if (facebook_email.text.toString().equals("null",ignoreCase = true)){
                val v = View.inflate(this.context,R.layout.input_text,null)
                val input = v.findViewById<EditText>(R.id.inputText)
                MaterialStyledDialog.Builder(this.context)
                        .setTitle("Enter your facebook account")
                        .setCustomView(v)
                        .setStyle(Style.HEADER_WITH_TITLE)
                        .setDescription("add your facebook id ")
                        .setPositiveText("add")

                        .onPositive { _, _ ->
                            input.hint = "https://www.facebook.com/your user"
                            if (!input.text.toString().isEmpty())
                            setSocial(f_id = input.text.toString())
                        }
                       .show()

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
                val v = View.inflate(this.context,R.layout.input_text,null)
                val input = v.findViewById<EditText>(R.id.inputText)
                MaterialStyledDialog.Builder(this.context)
                        .setTitle("Enter your twitter account")
                        .setCustomView(v)
                        .setStyle(Style.HEADER_WITH_TITLE)
                        .setDescription("add your twitter id ")
                        .setPositiveText("add")
                        .onPositive { _, _ ->
                            input.hint = "https://twitter.com/username"
                            if (!input.text.toString().isEmpty())
                                setSocial(t_id = input.text.toString())
                        }
                        .show()
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
                val v = View.inflate(this.context,R.layout.input_text,null)
                val input = v.findViewById<EditText>(R.id.inputText)
                MaterialStyledDialog.Builder(this.context)
                        .setTitle("Enter your skype account")
                        .setCustomView(v)
                        .setStyle(Style.HEADER_WITH_TITLE)
                        .setDescription("add your skype email ")
                        .setPositiveText("add")
                        .onPositive { _, _ ->
                            input.hint = "your skype email"
                            if (!input.text.toString().isEmpty())
                                setSocial(s_id = input.text.toString())
                        }
                        .show()
            }else{
                val clipboard = this.requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                val clip = ClipData.newPlainText("skype id", skype_email.text.toString())
                clipboard.primaryClip = clip
                Toast.makeText(this.context,"the skype id/email has been copied to your clipboard", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
