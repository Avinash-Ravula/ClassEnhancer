package com.example.avinash.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GlobalChat extends AppCompatActivity {

    private static String TAG = "Avinash";

    private ListView chatListView = null;
    private GlobalChatAdapter adapter = null;
    private ConstraintLayout constraintLayout = null,attachmentOptionsLayout = null;
    private FloatingActionButton addAttachmentButton, cameraButton, galleryButton, sendMessageButton;
    private ArrayList<Message> chatList = new ArrayList<Message>();
    private EditText editText;
    DatabaseReference databaseReference;
    public static String email = "";
    String name = "", currentTimestamp = "";

    Uri fileUri = null;


    private static final int MY_CAMERA_PERMISSION_REQUEST_CODE = 2;
    private static final int MY_GALLERY_PERMISSION_REQUEST_CODE = 3;
    private static final int REQUEST_IMAGE_CAPTURE = 4;
    private static final int REQUEST_GALLERY_PICKER = 5;
    private static String chatName = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_layout);
        chatListView = findViewById(R.id.chat_list);
        constraintLayout = findViewById(R.id.constraint_layout);
        editText = findViewById(R.id.editText);
        addAttachmentButton = findViewById(R.id.add_attachment);
        startServices();

    }

    private void startServices() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        name = user.getDisplayName();
        email = user.getEmail().replace(".","");
        String chatName = getIntent().getStringExtra("chatName");
        getSupportActionBar().setTitle(chatName);
        databaseReference = FirebaseDatabase.getInstance().getReference("all_chats/" + chatName);
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(TAG, "child added");
                chatList.add(dataSnapshot.getValue(Message.class));
                populateChat();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        sendMessageButton = findViewById(R.id.send_message_btn);
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Floating button clicked");
                String message = String.valueOf(editText.getText());
                message = message.trim();
                if(!message.isEmpty()){
                    pushMessage(message,email,name,getResources().getString(R.string.text), currentTimestamp);
                }
            }
        });

        addAttachmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchGalleryIntent();
//                if(attachmentOptionsLayout.getVisibility() == View.VISIBLE) {
//                    attachmentOptionsLayout.setVisibility(View.GONE);
//                }
//                else {
//                    attachmentOptionsLayout.setVisibility(View.VISIBLE);
//                }
            }
        });

//        galleryButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dispatchGalleryIntent();
//
//            }
//        });
//
//        cameraButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
//                        requestPermissions(new String[] {Manifest.permission.CAMERA},MY_CAMERA_PERMISSION_REQUEST_CODE);
//                }else{
//                    dispatchTakePictureIntent();
//                }
//            }
//        });

    }

    private void dispatchGalleryIntent() {

        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("file/*");

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
//        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
//        intent.setType("image/*");

        startActivityForResult(Intent.createChooser(intent, "Complete action using"), REQUEST_GALLERY_PICKER);
    }

    private void pushMessage(String message, String email, String name, String type, String filePath) {
        DatabaseReference reference = databaseReference.push();
        reference.setValue(new Message(message,email,name,type, filePath));
        editText.setText("");
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_IMAGE_CAPTURE:
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    final Uri imageUri = getImageUri(this, imageBitmap);
                    StorageReference reference = FirebaseStorage.getInstance().getReference(getResources().getString(R.string.image) + "/" +imageUri.getLastPathSegment());
                    reference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Log.d(TAG, "image uploaded successfully");
                            pushMessage(getFileName(imageUri),email,name,getResources().getString(R.string.image), currentTimestamp);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "failed to upload image");
                        }
                    });
                    break;
                case REQUEST_GALLERY_PICKER:
                    // The document selected by the user won't be returned in the intent.
                    // Instead, a URI to that document will be contained in the return intent
                    // provided to this method as a parameter.
                    // Pull that URI using resultData.getData().
                    if (data != null) {
                        fileUri = data.getData();
                        if(fileUri != null) {
                            Log.i(TAG, "Uri: " + fileUri.getPath());
                            Log.d(TAG, "getRealPathFromUri "+getFileName(fileUri));
                            currentTimestamp = String.valueOf(new Date().getTime());
                            final String fileName = getFileName(fileUri);
                            final String filePath = currentTimestamp + fileName.substring(fileName
                                    .lastIndexOf('.'),fileName.length());
                            StorageReference ref = FirebaseStorage.getInstance().getReference(getResources().getString(R.string.document) + "/" + filePath);
                            ref.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Log.d(TAG, "file uploaded successfully");
                                    pushMessage(fileName,email,name,getResources().getString(R.string.document), filePath);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e(TAG, "failed to upload image");
                                }
                            });
                        }
//                        showImage(uri);

                    }
                    break;
            }
        }
    }

    private void populateChat(){
        if(adapter == null)
        {
            adapter = new GlobalChatAdapter(this,chatList);
            chatListView.setAdapter(adapter);
        }
        else
        {
            adapter.notifyDataSetChanged();
            adapter = new GlobalChatAdapter(this,chatList);
            chatListView.setAdapter(adapter);
        }
        chatListView.setSelection(adapter.getCount() - 1);
//        chatListView.setScrollingCacheEnabled(false);
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        String path = "";
        if (getContentResolver() != null) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
}
