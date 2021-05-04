package com.example.kitchenstore.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.kitchenstore.InventoryActivity;
import com.example.kitchenstore.MainActivity;
import com.example.kitchenstore.R;
import com.example.kitchenstore.classes.Inventories;
import com.example.kitchenstore.classes.Product;
import com.example.kitchenstore.classes.Users;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class NotificationService extends Service {
    private int counter=0;
    public NotificationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);

        DatabaseReference mRef= FirebaseDatabase.getInstance().getReference("/Inventories/"+ Users.current_user.getKitchen_id()+"/stocking");

        mRef.child(formattedDate).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                 // Get the PendingIntent containing the entire back stack

                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    Notification.Builder mBuilder = new Notification.Builder(getApplicationContext());

                    mBuilder.setContentTitle("New Stocking Arrive!!")
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

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    if(dataSnapshot.child("expiry").exists()) {
                        Product product = dataSnapshot.getValue(Product.class);
                        product.setName(dataSnapshot.getKey());
                        if (product.getExpiry() < 4 &&product.getExpiry()>0)
                            Inventories.expiringProduct.add(product);
                        if(product.getExpiry()<=0) {
                            Inventories.expiringProduct.remove(product);
                            dataSnapshot.getRef().removeValue();

                            String key = product.getName();
                            Product product_to_db = new Product();
                            product_to_db.setAmount(product.getAmount());
                            product_to_db.setName(product.getName());
                            product_to_db.setPrice(product.getPrice());

                            dataSnapshot.getRef().getParent().getParent().getParent().getParent().child("bin").child(key).setValue(product_to_db);
                        }

                    }
                }


            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return super.onStartCommand(intent, flags, startId);
    }
}