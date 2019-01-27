package com.example.avinash.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class GlobalChatAdapter extends BaseAdapter {
    private LayoutInflater inflater = null;
    private Activity activity;
    private ArrayList<Message> messageArrayList = new ArrayList<Message>();
    private static final String TAG = "Avinash";
    private File localFile;
    private String CONTENT_PATH = "",email = "", filePath = "";

    private Message message;
    private TextView receivedMessageSentByTextView, sentMessageTextView, sentDocumentNameTextView, receivedMessageTextView, receivedDocumentNameTextView;
    private ImageView sentMessageImageView, receivedMessageImageView;
    private VideoView sentMessageVideoView, receivedMessageVideoView;
    private CardView sentDocumentCardView, receivedDocumentCardView;
    private ConstraintLayout sentConstraintLayout, receiveConstraintLayout;

    public GlobalChatAdapter(Activity activity, ArrayList<Message> messageArrayList) {
        Log.d(TAG, "GlobalChatAdapter()");
        this.activity = activity;
        this.messageArrayList = messageArrayList;
        Collections.sort(this.messageArrayList, new SortByTimestamp());
        String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        CONTENT_PATH = rootPath  + "/" +activity.getResources().getString(R.string.app_name).concat("/");
        email = GlobalChat.email;
    }

    @Override
    public int getCount() {
        return messageArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return messageArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Log.d(TAG, "getView()");
        if (inflater == null) {
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.chat_box_layout, parent, false);
        }

        sentConstraintLayout = convertView.findViewById(R.id.sent_layout);
        receiveConstraintLayout = convertView.findViewById(R.id.receive_layout);

        sentMessageTextView = convertView.findViewById(R.id.sent_message_textview);
        sentDocumentCardView = convertView.findViewById(R.id.sent_document_card_view);
        sentDocumentNameTextView = convertView.findViewById(R.id.sent_document_name_text_view);

        receivedMessageSentByTextView = convertView.findViewById(R.id.received_from_textview);
        receivedMessageTextView = convertView.findViewById(R.id.received_message_text_view);
        receivedDocumentCardView = convertView.findViewById(R.id.received_document_card_view);
        receivedDocumentNameTextView = convertView.findViewById(R.id.received_document_name_text_view);

        message = messageArrayList.get(position);
        if (message.getSentByEmail().equals(GlobalChat.email)) {
            sentConstraintLayout.setVisibility(View.VISIBLE);
            receiveConstraintLayout.setVisibility(View.GONE);

            if(message.getMessageType().equals(activity.getResources().getString(R.string.text))) {
                sentDocumentCardView.setVisibility(View.GONE);
                sentMessageTextView.setVisibility(View.VISIBLE);
                sentMessageTextView.setText(message.getMessage());

            } else if(message.getMessageType().equals(activity.getResources().getString(R.string.document))) {
                sentMessageTextView.setVisibility(View.GONE);
                sentDocumentCardView.setVisibility(View.VISIBLE);
                sentDocumentNameTextView.setText(message.getMessage());
                filePath = CONTENT_PATH + "Document/" + message.getFilePath();
                sentDocumentCardView.setOnClickListener(documentOnClickLister);
            }
        } else {
            sentConstraintLayout.setVisibility(View.GONE);
            receiveConstraintLayout.setVisibility(View.VISIBLE);

            if(message.getMessageType().equals(activity.getResources().getString(R.string.text))) {
                receivedDocumentCardView.setVisibility(View.GONE);
                receivedMessageTextView.setVisibility(View.VISIBLE);
                receivedMessageSentByTextView.setText(message.getSentByName());
                receivedMessageTextView.setText(message.getMessage());

            } else if(message.getMessageType().equals(activity.getResources().getString(R.string.document))) {
                receivedMessageTextView.setVisibility(View.GONE);
                receivedDocumentCardView.setVisibility(View.VISIBLE);
                receivedDocumentNameTextView.setText(message.getMessage());
                receivedMessageSentByTextView.setText(message.getSentByName());
                filePath = CONTENT_PATH + "Document/" + message.getFilePath();
                receivedDocumentCardView.setOnClickListener(documentOnClickLister);
            }
        }
        return convertView;
    }

    private View.OnClickListener documentOnClickLister = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(new File(filePath).exists()) {
                Log.d(TAG, "File exists");
                openFile(filePath, message.getMessage());
            }else{
                StorageReference documentRefrence = FirebaseStorage.getInstance().getReference(activity.getResources().getString(R.string.document) + "/" + message.getFilePath());
                documentRefrence.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d(TAG, "download uri is " + uri.toString());
                        Log.d(TAG, "Destination is " + filePath);
                        Log.d(TAG, "getExternalStorageDirectory : " + Environment.getExternalStorageDirectory().getAbsolutePath());
                        Log.d(TAG, "getRootDirectory : " + Environment.getRootDirectory().getAbsolutePath());
                        Log.d(TAG, "getDataDirectory : " + Environment.getDataDirectory().getAbsolutePath());
                        activity.startService(DownloadService.getDownloadService(activity, uri.toString(), (activity.getResources().getString(R.string.app_name)).concat("/")));
                    }
                });
            }
        }
    };

    private void openFile(String filePath, String fileName){
        File file = new File(filePath);
        String type = getFileType(fileName);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri data = Uri.fromFile(file);
        intent.setDataAndType(data, type);
        activity.startActivity(intent);
    }

    private String getFileType(String filename){
        String extension = filename.substring(filename.lastIndexOf('.') + 1,filename.length());
        switch (extension){
            case "jpg":
            case "jpeg":
            case "png":
            case "gif":
                return "image/*";
            case "mp3":
            case "wav":
            case "aac":
            case "mpeg":
            case "ogg":
            case "midi":
            case "x-ms-wma":
                return "audio/*";
            case "mp4":
                return "video/*";
            case "pdf":
                return "application/pdf";
            case "doc":
                return "application/msword";
            case "docx":
                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            default:
                return "*/*";
        }
    }
}

class SortByTimestamp implements Comparator<Message>{
    @Override
    public int compare(Message o1, Message o2) {
        return new Date(o1.getTimestamp()).compareTo(new Date(o2.getTimestamp()));
    }
}
