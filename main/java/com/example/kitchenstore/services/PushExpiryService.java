package com.example.kitchenstore.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.kitchenstore.InventoryActivity;
import com.example.kitchenstore.MainActivity;
import com.example.kitchenstore.R;
import com.example.kitchenstore.classes.Inventories;
import com.example.kitchenstore.classes.Product;
import com.example.kitchenstore.classes.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PushExpiryService extends Service {

    public PushExpiryService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int counter_for_expiring=Inventories.expiringProduct.size();
        //int counter_for_bin=Inventories.binProduct.size();
         if(counter_for_expiring>0){

             NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
             Notification.Builder mBuilder = new Notification.Builder(getApplicationContext());

             mBuilder.setContentTitle("You have "+counter_for_expiring+" food expiring !!")
                     .setContentText("Check it out")
                     //设置大图标
                     .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                     //设置小图标
                     .setSmallIcon(R.mipmap.ic_launcher)
                     //设置通知时间
                     .setWhen(System.currentTimeMillis())
                     //首次进入时显示效果
                     .setTicker("我是测试内容")
                     .setVisibility(Notification.VISIBILITY_PRIVATE)
                     .setPriority(Notification.PRIORITY_HIGH)
                     .setCategory(Notification.CATEGORY_MESSAGE)

                     //设置通知方式，声音，震动，呼吸灯等效果，这里通知方式为声音
                     .setDefaults(Notification.DEFAULT_ALL);
             notificationManager.notify(10, mBuilder.build());
         }

//         if(counter_for_bin>0){
//            DatabaseReference mRef= FirebaseDatabase.getInstance().getReference();
//            mRef.child("Inventories").child(Users.current_user.getKitchen_id()).child("bin");
//            for(Product product:Inventories.binProduct)
//            {
//                String key=product.getName();
//                Product product_to_db=new Product();
//                product_to_db.setAmount(product.getAmount());
//                product_to_db.setPrice(product.getPrice());
//                mRef.child(product.getName()).setValue(product_to_db);
//                Inventories.binProduct.remove(product);
//            }
//
//
//         }


        return super.onStartCommand(intent, flags, startId);
    }
}