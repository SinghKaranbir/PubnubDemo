package com.spartan.karanbir.pubnubdemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by karanbir on 3/18/16.
 */
public class MessageAdapter extends ArrayAdapter {
    private Context mContext;
    private ArrayList<Message> messageList;
    private LayoutInflater inflater;
    public MessageAdapter(Context context, ArrayList<Message> messageList) {
        super(context, R.layout.message_item);
        mContext = context;
        this.messageList = messageList;
        this.inflater = LayoutInflater.from(context);
    }
    class ViewHolder {
        TextView user;
        TextView message;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Message message = messageList.get(position);
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.message_item, parent, false);
            holder.user = (TextView) convertView.findViewById(R.id.chat_user);
            holder.message = (TextView) convertView.findViewById(R.id.chat_message);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.user.setText(message.getUsername());
        holder.message.setText(message.getMessage());

        return convertView;
    }
    @Override
    public int getCount() {
        return messageList.size();
    }

    public void addMessage(Message message){
        messageList.add(message);
        notifyDataSetChanged();
    }

    public void setMessages(List<Message> messages){
        messageList.clear();
        messageList.addAll(messages);
        notifyDataSetChanged();
    }
}
