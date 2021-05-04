package com.example.kitchenstore.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.kitchenstore.R;
import com.example.kitchenstore.classes.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

public class ExpiryService extends Service {
    public ExpiryService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final DatabaseReference mRef= FirebaseDatabase.getInstance().getReference("/Inventories/"+ Users.current_user.getKitchen_id()+"/stocking");
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //date
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //timemillstone
                    for (DataSnapshot snapshot : postSnapshot.getChildren()) {
                        //stocking
                        for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                            mRef.child(postSnapshot.getKey()).child(snapshot.getKey()).child(dataSnapshot1.getKey()).child("expiry").setValue(ServerValue.increment(-1));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return super.onStartCommand(intent, flags, startId);
    }
}