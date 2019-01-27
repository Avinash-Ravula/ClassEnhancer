package com.example.avinash.myapplication;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ChatListAdapter extends BaseAdapter {

    ArrayList<Group> chatList = new ArrayList<Group>();
    private LayoutInflater inflater;
    private Activity activity;

    public ChatListAdapter(Activity activity, ArrayList<Group> chatList) {
        this.chatList = chatList;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return chatList.size();
    }

    @Override
    public Object getItem(int position) {
        return chatList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(inflater==null)
        {
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if(convertView==null)
        {
            convertView = inflater.inflate(R.layout.chat_item,parent,false);
        }

        TextView chatNameTV = convertView.findViewById(R.id.chat_name);
        chatNameTV.setText(chatList.get(position).getTitle());

        TextView lastMessageTV = convertView.findViewById(R.id.last_message);
        lastMessageTV.setText(chatList.get(position).getLastMessage());

        return convertView;

    }
}
