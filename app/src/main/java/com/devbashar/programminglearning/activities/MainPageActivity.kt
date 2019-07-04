package com.devbashar.programminglearning.activities


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import co.zsmb.materialdrawerkt.builders.accountHeader
import com.devbashar.programminglearning.R
import co.zsmb.materialdrawerkt.builders.drawer
import co.zsmb.materialdrawerkt.builders.footer
import co.zsmb.materialdrawerkt.draweritems.badgeable.primaryItem
import co.zsmb.materialdrawerkt.draweritems.badgeable.secondaryItem
import co.zsmb.materialdrawerkt.draweritems.profile.profile
import com.devbashar.programminglearning.fragments.*
import com.devbashar.programminglearning.helperClasses.Constants
import com.devbashar.programminglearning.helperClasses.User
import com.devbashar.programminglearning.helperClasses.UserSharedPreference
import com.facebook.drawee.backends.pipeline.Fresco
import com.google.firebase.iid.FirebaseInstanceId
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import kotlinx.android.synthetic.main.activity_main_page.*

import com.devbashar.programminglearning.util.ChatFireBaseMessagingService.Companion.sendNotification

import io.chatcamp.sdk.*


class MainPageActivity : AppCompatActivity() {

    private var user: User?=null
    private var fragment:Fragment?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme2)
        setContentView(R.layout.activity_main_page)
        Fresco.initialize(this)
        setSupportActionBar(toolbar)
        user = UserSharedPreference.getInstance(this).getCurrentUserDetails()
        setupChatcamp()
        ChatCamp.init(this.applicationContext, Constants().APP_ID)
        ChatCamp.connect(UserSharedPreference.getInstance(this.applicationContext).getCurrentUserDetails().id.toString()) { _,e ->
            if (e != null) {
                if (FirebaseInstanceId.getInstance().getToken("","") != null) {
                    ChatCamp.updateUserPushToken(FirebaseInstanceId.getInstance().token){_,_->}
                }
            }
        }

        initFragment()
        initDrawer(savedInstanceState)

    }

    private fun setupChatcamp() {
        ChatCamp.addChannelListener("NOTIFICATION", object : ChatCamp.ChannelListener() {
           override fun onGroupChannelMessageReceived(groupChannel: GroupChannel, message: Message) {
                if (UserSharedPreference.getInstance(this@MainPageActivity.applicationContext).getGroupID() != groupChannel.id && !message.user.id.equals(ChatCamp.getCurrentUser().id,ignoreCase = true)) {
                    sendNotification(this@MainPageActivity.applicationContext, groupChannel.id,
                            BaseChannel.ChannelType.GROUP.name, message, "chatcamp")
                }
            }
        })
        }


    private fun initDrawer(savedInstanceState: Bundle?){
        drawer {
            toolbar=this@MainPageActivity.toolbar
            hasStableIds = true
            savedInstance = savedInstanceState

            accountHeader {
                background = R.drawable.header
                savedInstance = savedInstanceState
                translucentStatusBar = true
                textColorRes = R.color.white
                profile(user!!.first_name+" "+user!!.last_name, user!!.email) {
                    icon= R.drawable.profiledemo

                    textColorRes=R.color.white

                }
            }
            onItemClick { _, _, drawerItem ->
                val transaction = this@MainPageActivity.supportFragmentManager.beginTransaction()
                when(drawerItem.identifier){
                    1L->{
                        fragment=MainPage()
                    }
                    2L->{
                        fragment=Announcement()
                    }
                    3L->{
                        fragment=Messages()
                    }
                    4L->{
                        fragment=Profile()

                    }
                    5L->{
                        fragment = AboutFragment()
                    }
                }
                if (drawerItem.identifier!=0L&&drawerItem.identifier!=10L){
                    transaction.replace(R.id.nav_host,fragment!!)
                    transaction.addToBackStack(null)
                    transaction.commit()
                    title=drawerItem.tag.toString()
                }

                false
            }
            primaryItem("Courses"){
                iicon= GoogleMaterial.Icon.gmd_home
                identifier=1L
                tag="Courses"

            }
            secondaryItem("Announcement and News") {
                iicon=GoogleMaterial.Icon.gmd_notifications
                identifier=2L
                tag="Announcement"

            }
            secondaryItem ("ChatCamp Messages"){
                iicon=GoogleMaterial.Icon.gmd_message
                identifier=3L
                tag="Messages"
            }
            secondaryItem ("Online Compiler"){
                iicon=GoogleMaterial.Icon.gmd_keyboard
                identifier=0L
                onClick {_->
                    val intent = Intent(this@MainPageActivity, CompilerActivity::class.java)
                    this@MainPageActivity.startActivity(intent)
                    false
                }

            }
            secondaryItem ("Profile"){
                iicon=GoogleMaterial.Icon.gmd_person
                tag="Profile"
                identifier=4L
            }
            secondaryItem ("About App"){
                iicon=GoogleMaterial.Icon.gmd_question_answer
                tag="About"
                identifier=5L
            }
            footer {
                primaryItem ("Log out"){
                    identifier=10L
                    onClick { _ ->
                        ChatCamp.disconnect {

                        }
                        UserSharedPreference.getInstance(this@MainPageActivity).logOut()
                        val intent = Intent(this@MainPageActivity, MainActivity::class.java)
                        this@MainPageActivity.finish()
                        startActivity(intent)
                        false
                    }
                }

            }
        }
    }
    private fun initFragment() {
        val transaction = this@MainPageActivity.supportFragmentManager.beginTransaction()
        fragment=MainPage()
        transaction.replace(R.id.nav_host,fragment!!)
        transaction.addToBackStack(null)
        transaction.commit()
        title="Classes"
    }

    override fun onBackPressed() {
        super.onBackPressed()
        this.finish()
    }
}
