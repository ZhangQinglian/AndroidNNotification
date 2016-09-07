package com.zql.android;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String KEY_TEXT_REPLY = "key_text_reply";

    private static final String DIRECT_REPLY_ACTION = "com.android.zql.dirct";

    private static final String MESSAGING_STYLE_ACTION = "com.android.zql.messaging";

    private final int directNotiId = 2311;
    private final int customNotiId = 2312;
    private final int messagingStyleId = 2313;
    private final int notiGroupIdStart = 2400;
    private MyBoradcastReceiver receiver;
    private List<Conversation> conversationList = new ArrayList<>();

    NotificationManagerCompat notificationManagerCompat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        conversationList.add(new Conversation("在吗?",System.currentTimeMillis(),"隔壁老宋"));
        conversationList.add(new Conversation("不在!",System.currentTimeMillis(),"老王"));
        conversationList.add(new Conversation("在吗在吗?",System.currentTimeMillis(),"隔壁老宋"));

        setContentView(R.layout.activity_main);

        notificationManagerCompat = NotificationManagerCompat.from(this);

        Button direcReply = (Button) findViewById(R.id.direct_reply);
        direcReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notificationManagerCompat.notify(directNotiId,getDirctReplyNotification());
            }
        });

        final Button customNoti = (Button)findViewById(R.id.custom_view);
        customNoti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notificationManagerCompat.notify(customNotiId,getCustomNotification());
            }
        });

        Button groupNoti = (Button) findViewById(R.id.group_noti);
        groupNoti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNotiGroup();
            }
        });

        final Button messagingStyle = (Button) findViewById(R.id.messaging_style);
        messagingStyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notificationManagerCompat.notify(messagingStyleId,getMessagingStyleNoti().build());
            }
        });

        receiver = new MyBoradcastReceiver();
        IntentFilter intentFilter = new IntentFilter(DIRECT_REPLY_ACTION);
        intentFilter.addAction(MESSAGING_STYLE_ACTION);
        registerReceiver(receiver,intentFilter);

    }

    private Notification getDirctReplyNotification(){
        String replyLabel = "say something";
        RemoteInput remoteInput = new RemoteInput.Builder(KEY_TEXT_REPLY)
                .setLabel(replyLabel)
                .build();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this,11,new Intent(DIRECT_REPLY_ACTION),PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Action action =
                new NotificationCompat.Action.Builder(R.mipmap.ic_launcher, replyLabel, pendingIntent)
                        .addRemoteInput(remoteInput)
                        .setAllowGeneratedReplies(true)
                        .build();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this);
        builder.addAction(action).setSmallIcon(R.mipmap.ic_launcher).setContentTitle("隔壁老宋")
                .setContentText("老王你在家吗?").setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.gebilaosong));
        return builder.build();
    }

    @TargetApi(Build.VERSION_CODES.N)
    private Notification getCustomNotification(){

        Notification noti = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setCustomContentView(new RemoteViews("com.zql.android",R.layout.remote_view))
                .setStyle(new Notification.DecoratedCustomViewStyle())
                .build();
        return noti;
    }

    private void showNotiGroup(){
        Notification notiSummary = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("隔壁老宋:")
                .setContentText("在吗?")
                .setGroup("zql")
                .setGroupSummary(true)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.gebilaosong))
                .build();
        notificationManagerCompat.notify(notiGroupIdStart,notiSummary);
        StringBuilder sb = new StringBuilder("?");
        for(int i = 0;i<10;i++){
            sb.delete(0,sb.length());
            for(int j = 0;j<i;j++){
                sb.append(" ?");
            }
            Notification noti = new Notification.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("隔壁老宋:")
                    .setContentText("在吗?" + sb.toString())
                    .setGroup("zql")
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.gebilaosong))
                    .build();
            notificationManagerCompat.notify(notiGroupIdStart+i+1,noti);
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    private NotificationCompat.Builder getMessagingStyleNoti(){
        String replyLabel = "say something";
        RemoteInput remoteInput = new RemoteInput.Builder(KEY_TEXT_REPLY)
                .setLabel(replyLabel)
                .build();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this,11,new Intent(MESSAGING_STYLE_ACTION),PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Action action =
                new NotificationCompat.Action.Builder(R.mipmap.ic_launcher, replyLabel, pendingIntent)
                        .addRemoteInput(remoteInput)
                        .setAllowGeneratedReplies(true)
                        .build();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this);
        builder.addAction(action).setSmallIcon(R.mipmap.ic_launcher);
        NotificationCompat.MessagingStyle messagingStyle = new NotificationCompat.MessagingStyle("老王");
        messagingStyle.setConversationTitle("来自老宋");
        builder.setStyle(messagingStyle);
        for(Conversation conversation:conversationList){
            messagingStyle.addMessage(conversation.content,conversation.time,conversation.person);
        }

        return builder;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    public class MyBoradcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if(DIRECT_REPLY_ACTION.equals(intent.getAction())){
                Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
                if(remoteInput != null){
                    Toast.makeText(MainActivity.this,remoteInput.getCharSequence(KEY_TEXT_REPLY),Toast.LENGTH_LONG).show();
                    notificationManagerCompat.cancel(directNotiId);
                }
            }
            if(MESSAGING_STYLE_ACTION.equals(intent.getAction())){
                Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
                if(remoteInput != null){
                    CharSequence content = remoteInput.getCharSequence(KEY_TEXT_REPLY);
                    conversationList.add(new Conversation(content.toString(),System.currentTimeMillis(),"老王"));
                    conversationList.add(new Conversation("我知道你在的,出来吧!",System.currentTimeMillis(),"隔壁老宋"));
                    notificationManagerCompat.notify(messagingStyleId,getMessagingStyleNoti().build());
                    notificationManagerCompat.cancel(directNotiId);
                }
            }
        }
    }

    public class Conversation{
        final String content;
        final long  time;
        final String person;

        public Conversation(String content,long time,String person){
            this.content = content;
            this.time = time;
            this.person = person;

        }

    }
}
