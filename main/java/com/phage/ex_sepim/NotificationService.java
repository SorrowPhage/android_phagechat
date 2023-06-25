package com.phage.ex_sepim;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;

import androidx.core.app.NotificationCompat;

import com.phage.ex_sepim.LoginActivity;
import com.phage.ex_sepim.R;
import com.phage.ex_sepim.NotifyClickReceiver;
import com.phage.ex_sepim.utils.ConnectRabbitMQUtils;
import com.phage.ex_sepim.utils.SPUtils;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeoutException;

public class NotificationService extends Service {

    private Context context;

    private static String EXCHANGE_NAME = "app_msg_push_exchange";
    private static String QUEUE_NAME = "";


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        context = this;

        //        先判断是否登录，
        SharedPreferences sharedPreferences = getSharedPreferences("data", 0);
        String id= sharedPreferences.getString("id","");
        if (id.equals("")) {
//            未登录，跳转到登录界面
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        //设置每个用户对应一个队列
        if (TextUtils.isEmpty((String) SPUtils.getParam(context, "QUEUE_NAME", "")))
        {
            SPUtils.setParam(context, "QUEUE_NAME", "app_msg_push_queue_" + id);
        }

        QUEUE_NAME = (String) SPUtils.getParam(context, "QUEUE_NAME", "");
        getDataFromMQ(id);

    }

    /**
     * 从消息队列获取数据
     */
    private void getDataFromMQ(String id)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    // 获取到连接
                    Connection connection = ConnectRabbitMQUtils.getConnection();
                    // 获取通道
                    final Channel channel = connection.createChannel();
                    // 声明队列
                    channel.queueDeclare(QUEUE_NAME, true, false, false, null);
                    // 绑定队列到交换机，同时指定需要订阅的routing key。
                    channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "app.pc" + id);

                    // 定义队列的消费者
                    DefaultConsumer consumer = new DefaultConsumer(channel)
                    {
                        @Override
                        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException
                        {
                            // body 即消息体
                            final String msg = new String(body);
                            System.out.println("===========================");
                            System.out.println(msg);
                            showNotifictionIcon(context, getAppName(context), msg);


                            //手动设置ACK
                            channel.basicAck(envelope.getDeliveryTag(), false);
                        }
                    };
                    // 监听队列，手动ACK
                    channel.basicConsume(QUEUE_NAME, false, consumer);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (TimeoutException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    public void showNotifictionIcon(Context context, String title, String content)
    {
        NotificationManager manager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setAutoCancel(true);//点击后消失
        builder.setLargeIcon((getBitmap(context)));//设置通知栏消息标题的头像
        builder.setSmallIcon(R.mipmap.phage);
        builder.setDefaults(NotificationCompat.DEFAULT_SOUND);//设置通知铃声
        builder.setContentTitle(title);//设置标题
        builder.setContentText(content);//设置内容
        builder.setPriority(Notification.PRIORITY_DEFAULT); //设置该通知优先级
        //利用PendingIntent来包装我们的intent对象,使其延迟跳转 设置通知栏点击意图
        builder.setContentIntent(createIntent(context, title + content));
        manager.notify(new Random().nextInt(20), builder.build());
    }

    /**
     * 创建通知栏消息点击后跳转的intent。
     */
    public PendingIntent createIntent(Context context, String data)
    {
        Intent intent = new Intent(context, NotifyClickReceiver.class);
        Bundle mBundle = new Bundle();
        mBundle.putString("data", data);
        intent.putExtras(mBundle);
        intent.setAction("com.example.myapp");

        PendingIntent contentIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        return contentIntent;
    }

    /**
     * 获取应用图标bitmap
     */
    public static Bitmap getBitmap(Context context)
    {
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo = null;
        try
        {
            packageManager = context.getApplicationContext().getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e)
        {
            applicationInfo = null;
        }
        Drawable d = packageManager.getApplicationIcon(applicationInfo); //xxx根据自己的情况获取drawable
        BitmapDrawable bd = (BitmapDrawable) d;
        Bitmap bm = bd.getBitmap();
        return bm;
    }

    /**
     * 获取应用程序名称
     */
    public static String getAppName(Context context)
    {
        try
        {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }



}
