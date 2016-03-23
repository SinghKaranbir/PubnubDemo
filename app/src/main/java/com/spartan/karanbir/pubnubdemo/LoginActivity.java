package com.spartan.karanbir.pubnubdemo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by karanbir on 3/18/16.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText usernameEditText;
    private Button joinButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        usernameEditText = (EditText) findViewById(R.id.username);
        joinButton = (Button) findViewById(R.id.join_button);
        joinButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.join_button:
                String username = usernameEditText.getText().toString();
                if (username.isEmpty()){
                    Toast.makeText(this,"Username cannot be empty",Toast.LENGTH_SHORT).show();
                    return;
                }
                SharedPreferences sp = getSharedPreferences(MainActivity.CHAT_PREFS,MODE_PRIVATE);
                SharedPreferences.Editor edit = sp.edit();
                edit.putString(MainActivity.CHAT_USERNAME, username);
                edit.apply();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
        }
    }


}
