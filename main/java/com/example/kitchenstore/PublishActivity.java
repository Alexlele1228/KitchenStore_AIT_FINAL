package com.example.kitchenstore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.kitchenstore.classes.Meal;
import com.example.kitchenstore.classes.Product;
import com.example.kitchenstore.fragments.CustomCookingFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class PublishActivity extends AppCompatActivity {
    Button capture,publish;
    EditText title, instruction;
    ImageView imageView;
    private Bitmap image = null;
    private byte[] post_image = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_publish);

        capture=findViewById(R.id.take_pic_btn);
        publish=findViewById(R.id.btn_confirm_publish);
        imageView=findViewById(R.id.publish_iv);
        title=findViewById(R.id.title_et);
        instruction=findViewById(R.id.insstruction_et);

        final Meal meal= CustomCookingFragment.meal_to_publish;


        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                meal.setName(title.getText().toString().trim());
                meal.setCooking_instruction(instruction.getText().toString().trim());
                if(meal.getName()==null || meal.getCooking_instruction()==null && meal.getSource()==null)
                    return;
                DatabaseReference mRef= FirebaseDatabase.getInstance().getReference("/Exits_Recipes");
                HashMap<Product,Integer> map=meal.getSource();
                Set<Product> set=map.keySet();
                Iterator iterator=set.iterator();
                HashMap<String, Integer> map_to_db=new HashMap<>();
                while(iterator.hasNext())
                {
                    Product product=(Product) iterator.next();
                    map_to_db.put(product.getName(), map.get(product));
                }

                Set<String> set1=map_to_db.keySet();
                Iterator iterator1=set1.iterator();
                while (iterator1.hasNext())
                {
                    String source_name=iterator1.next().toString();
                    mRef.child(meal.getName()).child(source_name).setValue(map_to_db.get(source_name));
                }
                mRef.child(meal.getName()).child("instruction").setValue(meal.getCooking_instruction());
                CustomCookingFragment.meal_to_publish=null;

                if(image!=null) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();
                    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                    StorageReference imagesRef = storageRef.child(meal.getName() + ".jpeg");
                    UploadTask uploadTask = imagesRef.putBytes(data);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(PublishActivity.this, "Publish failed, please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        }
                    });
                }

                finish();
            }
        });

        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDialog(PublishActivity.this);
            }
        });

    }

    private void setDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true).setTitle("Select picture from :").setItems(new String[]{"Take a shot", "Choose from album"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        Intent getImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(getImage, 0);
                        break;
                    case 1:
                        Intent chooseFromAlbum = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        chooseFromAlbum.setType("image/*");
                        startActivityForResult(chooseFromAlbum, 1);
                        break;
                }

            }
        }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 0 && resultCode == RESULT_OK && data != null) {
            Bundle bundle = data.getExtras();
            image = (Bitmap) bundle.get("data");
            imageView.setImageBitmap(image);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            post_image = byteArrayOutputStream.toByteArray();


        } else if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Uri imageURI = data.getData();

            try {
                BitmapFactory.Options op = new BitmapFactory.Options();
                op.inSampleSize = 10;
                image = BitmapFactory.decodeStream(this.getContentResolver().openInputStream(imageURI),null,op);
                imageView.setImageBitmap(image);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                post_image = byteArrayOutputStream.toByteArray();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}