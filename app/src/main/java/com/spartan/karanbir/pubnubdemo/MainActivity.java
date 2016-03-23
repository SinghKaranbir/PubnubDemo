package com.spartan.karanbir.pubnubdemo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String CHAT_USERNAME = "username";
    public static final String CHAT_PREFS = "chat_prefs";
    private Pubnub mPubnub;
    private static final String CHANNEL_NAME = "Technology";
    private MessageAdapter messageAdapter;
    private ListView listView;
    private ImageButton sendButton;
    private EditText messageEditText ;
    private SharedPreferences mSharedPrefs;
    private String username;
    private TextView countTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSharedPrefs = getSharedPreferences(CHAT_PREFS, MODE_PRIVATE);
        if (!mSharedPrefs.contains(CHAT_USERNAME)){
            Intent toLogin = new Intent(this, LoginActivity.class);
            startActivity(toLogin);
            return;
        }
        countTextView = (TextView) findViewById(R.id.count);
        listView = (ListView) findViewById(R.id.message_list);
        sendButton = (ImageButton) findViewById(R.id.send_button);
        messageEditText = (EditText) findViewById(R.id.message_edittext);
        sendButton.setOnClickListener(this);
        username = mSharedPrefs.getString(CHAT_USERNAME,"NOBODY");
        initPubnub();
        subscribeToChannel(CHANNEL_NAME);
        messageAdapter = new MessageAdapter(this, new ArrayList<Message>());
        listView.setAdapter(messageAdapter);
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.i(TAG, "onResume");
        if(mPubnub != null) {
            boolean isSubscribed = false;
            String[] channelList = mPubnub.getSubscribedChannelsArray();
            Log.d(TAG, channelList.toString());
            for (String channel : channelList) {
                if (channel.equals(CHANNEL_NAME))
                    isSubscribed = true;
            }
            if (isSubscribed) {
                getHistory();
                checkOnlineUsers(CHANNEL_NAME);
            }
        }
    }

    private Pubnub initPubnub(){
        if(mPubnub == null){
            mPubnub = new Pubnub(getString(R.string.pubnub_publish_key), getString(R.string.pubnub_subscribe_key));
            mPubnub.setUUID(username);
        }
        return mPubnub;
    }

    private void subscribeToChannel(final String channelName) {
        boolean isSubscribed = false;
        String[] channelList = mPubnub.getSubscribedChannelsArray();
        Log.d(TAG, channelList.toString());
        for (String channel : channelList){
            if (channel.equals(channelName))
                isSubscribed = true;
        }
        if(isSubscribed) {
            getHistory();
        }else{
            try {
                    mPubnub.subscribe(channelName, new Callback() {
                        @Override
                        public void successCallback(String channel, Object message) {
                            super.successCallback(channel, message);
                            Log.i(TAG, "Successfully subscribed");
                        }

                        @Override
                        public void connectCallback(String channel, Object message) {
                            Log.i(TAG,"Successfully Connected to Channel");
                            subscribePresence(channelName);
                        }

                        @Override
                        public void errorCallback(String channel, PubnubError error) {
                            super.errorCallback(channel, error);
                            Log.e(TAG, error.toString());
                        }
                    });
                } catch (PubnubException e) {
                    e.printStackTrace();
                }
            }

    }

    private void subscribePresence(String channelName){
        try {
            mPubnub.presence(channelName, new Callback() {
                @Override
                public void successCallback(String channel, Object message) {
                    super.successCallback(channel, message);
                    Log.i(TAG, "Checking");
                    try {

                        JSONObject json = (JSONObject) message;
                        Log.i(TAG, json.toString());
                        final int count = json.getInt("occupancy");

                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (countTextView != null)
                                    countTextView.setText(Integer.toString(count));
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void errorCallback(String channel, PubnubError error) {
                    super.errorCallback(channel, error);
                    Log.e(TAG, error.getErrorString());
                }
            });
        } catch (PubnubException e) {
            e.printStackTrace();
        }
    }
    private void checkOnlineUsers(String channelName){

            mPubnub.hereNow(channelName, new Callback() {
                @Override
                public void successCallback(String channel, Object message) {
                    super.successCallback(channel, message);
                    Log.i(TAG, "Checking");
                    try {

                        JSONObject json = (JSONObject) message;
                        Log.i(TAG, json.toString());
                        final int count = json.getInt("occupancy");

                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (countTextView != null)
                                    countTextView.setText(Integer.toString(count));
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void errorCallback(String channel, PubnubError error) {
                    super.errorCallback(channel, error);
                    Log.e(TAG, error.getErrorString());
                }
            });

    }

    private void publishToChannel(final String message){
        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("username",mPubnub.getUUID());
            jsonObject.put("message", message);

            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    messageAdapter.addMessage(new Message(message,mPubnub.getUUID()));
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mPubnub.publish(CHANNEL_NAME, jsonObject, true, new Callback() {
            @Override
            public void successCallback(String channel, final Object message) {
                super.successCallback(channel, message);
                Log.i(TAG,"Successfully Published");
            }

            @Override
            public void errorCallback(String channel, PubnubError error) {
                super.errorCallback(channel, error);
                Log.e(TAG, error.toString());
            }
        });
    }

    private void getHistory() {
        mPubnub.history(CHANNEL_NAME, 100, new Callback() {
            @Override
            public void successCallback(String channel, Object message) {
                super.successCallback(channel, message);
                try {
                    JSONArray jsonArray = (JSONArray) message;
                    final ArrayList<Message> chatMessages = new ArrayList<Message>();
                    Log.d("History", jsonArray.toString());
                    final JSONArray messages = jsonArray.getJSONArray(0);
                    for (int i = 0; i < messages.length(); i++) {
                        try {
                            JSONObject jsonMsg = messages.getJSONObject(i);
                            String name = jsonMsg.getString("username");
                            String msg = jsonMsg.getString("message");
                            Message chatMessage = new Message(msg,name);
                            chatMessages.add(chatMessage);
                        } catch (JSONException e) { // Handle errors silently
                            e.printStackTrace();
                        }
                    }
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            messageAdapter.setMessages(chatMessages);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
               }
           }

           @Override
           public void errorCallback(String channel, PubnubError error) {
               super.errorCallback(channel, error);
               Log.d("History", error.toString());
           }
       });
   }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.send_button){
            publishToChannel(messageEditText.getText().toString());
            messageEditText.setText("");
        }
    }


}