package com.example.avinash.myapplication.app;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.avinash.myapplication.R;

import java.util.ArrayList;

public class AllChatsListAdapter extends BaseAdapter {
    private static final String TAG = "Avinash";
    private LayoutInflater inflater = null;
    ArrayList<ChatItem> chatItems = new ArrayList<>();
    Activity activity;
    ChatItem chatItem;

    public AllChatsListAdapter(Activity activity, ArrayList<ChatItem> chatItems) {
        this.activity = activity;
        this.chatItems = chatItems;
    }

    @Override
    public int getCount() {
        return chatItems.size();
    }

    @Override
    public Object getItem(int position) {
        return chatItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(TAG, "getView()");
        if (inflater == null) {
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.chat_item_layout, parent, false);
        }
        chatItem = chatItems.get(position);
        TextView chatNameTextView = convertView.findViewById(R.id.chat_name_text_view);
        TextView chatLastMessageTextView = convertView.findViewById(R.id.chat_last__message_text_view);
        chatNameTextView.setText(chatItem.getChatName());
        chatLastMessageTextView.setText(chatItem.getChatLastMessage());
        return convertView;
    }
}
