package com.devbashar.programminglearning.util

import android.app.Notification
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.app.PendingIntent
import android.content.Intent
import io.chatcamp.sdk.GroupChannelListQuery
import com.devbashar.programminglearning.activities.ConversationActivity
import android.app.NotificationManager
import android.app.NotificationChannel
import android.content.Context
import android.os.Build
import android.graphics.Color
import android.util.Log
import androidx.core.app.NotificationCompat
import com.devbashar.programminglearning.R
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import io.chatcamp.sdk.ChatCamp
import io.chatcamp.sdk.Message




class ChatFireBaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val channelId = remoteMessage.data["channelId"]
        val channelType = remoteMessage.data["channelType"]
        val data = remoteMessage.data["message"]
        val message = Message.createfromSerializedData(data)
        val serverType = remoteMessage.data["server"]
        sendNotification(this, channelId!!, channelType!!, message, serverType!!)
        Log.e("PUSH NOTIFICATION", "push notification")
    }

    override fun onNewToken(p0: String?) {
        super.onNewToken(p0)
       // val refreshedToken = FirebaseInstanceId.getInstance().token
        if (FirebaseInstanceId.getInstance().token != null && ChatCamp.getConnectionState() == ChatCamp.ConnectionState.OPEN) {
            ChatCamp.updateUserPushToken(FirebaseInstanceId.getInstance().token){ _, _->}
        }
    }
    companion object {

        fun sendNotification(context: Context, channelId: String, channelType: String, message: Message, serverType: String) {
            if (!serverType.equals("Chatcamp", ignoreCase = true)) {
                return
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val CHANNEL_ID = "CHANNEL_ID"
            if (Build.VERSION.SDK_INT >= 26) {  // Build.VERSION_CODES.O
                val mChannel = NotificationChannel(CHANNEL_ID, "CHANNEL_NAME", NotificationManager.IMPORTANCE_HIGH)
                notificationManager.createNotificationChannel(mChannel)
            }

            val intent = Intent(context, ConversationActivity::class.java)
            val participantState = GroupChannelListQuery.ParticipantState.ALL.name
            intent.putExtra("channelId", channelId)
            intent.putExtra("channelType", channelType)
            intent.putExtra("participantState", participantState)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            val pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent, PendingIntent.FLAG_UPDATE_CURRENT)

            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val notificationBuilder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.icon_default_contact)
                        .setColor(Color.parseColor("#7469C4"))  // small icon background color
                        .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.icon_default_contact))
                        .setContentTitle(message.user.displayName)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setPriority(NotificationManager.IMPORTANCE_HIGH)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setContentIntent(pendingIntent)
            } else {
                NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.icon_default_contact)
                        .setColor(Color.parseColor("#7469C4"))  // small icon background color
                        .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.icon_default_contact))
                        .setContentTitle(message.user.displayName)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setContentIntent(pendingIntent)
            }

            if (message.type == "attachment") {
                if (message.attachment.type.contains("image")) {
                    notificationBuilder.setContentText("Image")
                } else if (message.attachment.type.contains("video")) {
                    notificationBuilder.setContentText("video")
                } else if (message.attachment.type.contains("application") || message.attachment.type.contains("css") ||
                        message.attachment.type.contains("csv") || message.attachment.type.contains("text")) {
                    notificationBuilder.setContentText("document")
                }
            } else if (message.type == "text") {
                notificationBuilder.setContentText(message.text)
            } else {
                notificationBuilder.setContentText("new Message")
            }

            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
        }
    }
}