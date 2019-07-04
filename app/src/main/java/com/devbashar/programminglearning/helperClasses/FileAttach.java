package com.devbashar.programminglearning.helperClasses;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.chatcamp.uikit.messages.sender.AttachmentSender;
import com.chatcamp.uikit.utils.FileUtils;
import com.chatcamp.uikit.utils.Utils;

import java.io.File;
import java.lang.ref.WeakReference;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import io.chatcamp.sdk.BaseChannel;
import io.chatcamp.sdk.ChatCampException;

public class FileAttach extends AttachmentSender {
    private WeakReference<Object> a;

    public FileAttach(@NonNull Activity activity, @NonNull BaseChannel channel, @NonNull String title, @NonNull int drawableRes) {
        super(channel, title, drawableRes);
        this.a = new WeakReference(activity);
    }

    public FileAttach(@NonNull Fragment fragment, @NonNull BaseChannel channel, @NonNull String title, @NonNull int drawableRes) {
        super(channel, title, drawableRes);
        this.a = new WeakReference(fragment);
    }




    public void clickSend() {
        Context var1 = Utils.getContext(this.a.get());
        if (var1 == null) {
            ChatCampException var2 = new ChatCampException("Context is null", "FILE UPLOAD ERROR");
            this.sendAttachmentError(var2);
        } else {
            if (Build.VERSION.SDK_INT >= 16 && ContextCompat.checkSelfPermission(var1, "android.permission.READ_EXTERNAL_STORAGE") != 0) {
                Utils.requestPermission(new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 100, this.a.get());
            } else {
                this.a();
            }

        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == 0 && this.a.get() != null) {
            this.a();
        }

    }

    private void a() {
        Intent var1 = new Intent();
        var1.setType("*/*");
        var1.setAction(Intent.ACTION_GET_CONTENT);
        Utils.startActivityForResult(Intent.createChooser(var1, "Select files"), 120, this.a.get());
    }

    public void onActivityResult(int requestCode, int resultCode, Intent dataFile) {
        if (resultCode == -1 && requestCode == 120 && dataFile != null) {
            Uri var4 = dataFile.getData();
            this.a(var4);
        }

    }

    private void a(Uri var1) {
        Context var2 = Utils.getContext(this.a.get());
        if (var2 == null) {
            ChatCampException var7 = new ChatCampException("Context is null", "FILE UPLOAD ERROR");
            this.sendAttachmentError(var7);
        } else {
            String var3 = FileUtils.getPath(var2, var1);
            if (TextUtils.isEmpty(var3)) {
                Log.e("FileAttachmentSender", "File path is null");
                ChatCampException var8 = new ChatCampException("File Path is null", "FILE UPLOAD ERROR");
                this.sendAttachmentError(var8);
            } else {
                String var4 = FileUtils.getFileName(var2, var1);
                String var5 = null;
                if (var1.getScheme().equals("content")) {
                    ContentResolver var6 = var2.getContentResolver();
                    var5 = var6.getType(var1);
                } else {
                    String var9 = MimeTypeMap.getFileExtensionFromUrl(var1.toString());
                    var5 = MimeTypeMap.getSingleton().getMimeTypeFromExtension(var9.toLowerCase());
                }

                if (TextUtils.isEmpty(var5)) {
                    Log.e("FileAttachmentSender", "content type is null");
                    ChatCampException var11 = new ChatCampException("content type is null", "FILE UPLOAD ERROR");
                    this.sendAttachmentError(var11);
                } else {
                    File var10 = new File(var3);
                    this.sendAttachment(var10, var4, var5);
                }
            }
        }
    }
}
