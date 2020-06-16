package blackdots.t.punnubchat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import blackdots.t.punnubchat.Util.Constants;
import blackdots.t.punnubchat.activity.Chatactivity;
import blackdots.t.punnubchat.activity.StateActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();
    private EditText mUsername;
    Button goBtn, stateBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUsername = findViewById(R.id.usernameEdit);
        goBtn = findViewById(R.id.goBtn);
        stateBtn = findViewById(R.id.stateBtn);

        goBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinChat();
            }
        });
        stateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showState();
            }
        });
    }

    public void joinChat() {
        String username = mUsername.getText().toString();

        if (!isValid(username)) {
            return;
        }

        SharedPreferences sp = getSharedPreferences(Constants.DATASTREAM_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(Constants.DATASTREAM_UUID, username);
        edit.apply();

        Intent intent = new Intent(this, Chatactivity.class);
        startActivity(intent);
    }

    public void showState() {
        String username = mUsername.getText().toString();

        if (!isValid(username)) {
            return;
        }

        SharedPreferences sp = getSharedPreferences(Constants.DATASTREAM_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(Constants.DATASTREAM_UUID, username);
        edit.apply();

        Intent intent = new Intent(this, StateActivity.class);
        startActivity(intent);
    }

    private static boolean isValid(String username) {
        return username.matches("^[a-zA-Z0-9_]+$");
    }

}