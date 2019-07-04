package com.devbashar.programminglearning.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.devbashar.programminglearning.R
import kotlinx.android.synthetic.main.activity_conversation.*
import io.chatcamp.sdk.ChatCampException
import io.chatcamp.sdk.GroupChannel
import io.chatcamp.sdk.GroupChannelListQuery
import io.chatcamp.sdk.OpenChannel
import io.chatcamp.sdk.BaseChannel
import io.chatcamp.sdk.PreviousMessageListQuery
import android.content.Intent

import android.view.View

import com.chatcamp.uikit.messages.sender.AttachmentSender
import com.chatcamp.uikit.messages.sender.CameraAttachmentSender
import com.chatcamp.uikit.messages.sender.GalleryAttachmentSender
import com.chatcamp.uikit.messages.typing.DefaultTypingFactory

import com.chatcamp.uikit.messages.messagetypes.*
import android.text.TextUtils
import com.devbashar.programminglearning.helperClasses.FileAttach
import com.devbashar.programminglearning.helperClasses.UserSharedPreference


class ConversationActivity : AppCompatActivity(), AttachmentSender.UploadListener {


    private lateinit var channelType: String
    private lateinit var channelId: String
    private lateinit var previousMessageListQuery: PreviousMessageListQuery
    private lateinit var channel: BaseChannel



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme2)
        setContentView(R.layout.activity_conversation)
        setSupportActionBar(header_view.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)

        channelType = intent.getStringExtra("channelType")
        channelId = intent.getStringExtra("channelId")
        if (channelType == "open") {
            OpenChannel.get(channelId) { openChannel, _ ->
                setChannel(openChannel)
                openChannel.join {
                    previousMessageListQuery = openChannel.createPreviousMessageListQuery()
                    channel = openChannel
                }
            }
        } else {
            val groupFilter = GroupChannelListQuery.ParticipantState.ACCEPTED
            GroupChannel.get(channelId) { groupChannel, _ ->
                setChannel(groupChannel)
                if (groupFilter == GroupChannelListQuery.ParticipantState.INVITED) {
                    groupChannel.acceptInvitation { groupChannel1, _ -> channel = groupChannel1 }
                } else {
                    channel = groupChannel
                }
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, dataFile: Intent?) {
        edit_conversation_input.onActivityResult(requestCode, resultCode, dataFile)
        messagesList.onActivityResult(requestCode, resultCode, dataFile)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        edit_conversation_input.onRequestPermissionsResult(requestCode, permissions, grantResults)
        messagesList.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

     private fun setChannel(channel: BaseChannel) {
         messagesList.setOnMessagesLoadedListener { load_message_pb.visibility = View.GONE }
         header_view.setChannel(channel)
        invalidateOptionsMenu()
         edit_conversation_input.setChannel(channel)
        // edit_conversation_input.setOnSendClickListener { text -> val a = text }
         messagesList.addMessageFactories(TextMessageFactory(),ImageMessageFactory(this),VideoMessageFactory(this),VoiceMessageFactory(this),FileMessageFactory<MessageFactory<*>>(this))
         messagesList.setChannel(channel)
         messagesList.setTypingFactory(DefaultTypingFactory(this))
        val fileAttachmentSender = FileAttach(this, channel, "File", R.drawable.ic_assignment)
        fileAttachmentSender.setUploadListener(this)
        val galleryAttachmentSender = GalleryAttachmentSender(this, channel, "Gallery", R.drawable.ic_gallery)
        galleryAttachmentSender.setUploadListener(this)
        val cameraAttachmentSender = CameraAttachmentSender(this, channel, "Camera", R.drawable.ic_camera)
        cameraAttachmentSender.setUploadListener(this)
        val attachmentSenders = mutableListOf<AttachmentSender>()
        attachmentSenders.add(fileAttachmentSender)
        attachmentSenders.add(cameraAttachmentSender)
        attachmentSenders.add(galleryAttachmentSender)

         edit_conversation_input.setAttachmentSenderList(attachmentSenders)
    }

    override fun onUploadProgress(p0: Int) {
        try {
            progress_bar.visibility = View.VISIBLE
            progress_bar.progress = p0
        }catch (e:Exception){

        }

    }

    override fun onUploadFailed(p0: ChatCampException?) {
        try {
            progress_bar.visibility = View.GONE
        }catch (e:Exception){

        }

    }

    override fun onUploadSuccess() {
        try {
            progress_bar.visibility = View.GONE
        }catch (e:Exception){

        }
    }

    override fun onResume() {
        super.onResume()
        if (!TextUtils.isEmpty(channelId)) {
            UserSharedPreference.getInstance(this.applicationContext).setGroupID(channelId)
        }
    }

    override fun onPause() {
        UserSharedPreference.getInstance(this.applicationContext).setGroupID("")
        super.onPause()
    }


}
