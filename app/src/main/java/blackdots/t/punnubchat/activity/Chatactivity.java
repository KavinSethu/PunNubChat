package blackdots.t.punnubchat.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import java.util.List;
import java.util.Map;

import blackdots.t.punnubchat.R;
import blackdots.t.punnubchat.Util.Constants;
import blackdots.t.punnubchat.Util.JsonUtil;
import blackdots.t.punnubchat.adapter.ChatAdapter;
import blackdots.t.punnubchat.model.ChatModel;
import blackdots.t.punnubchat.pubnub.PubNubHandler;

public class Chatactivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = Chatactivity.class.getName();
    RecyclerView chat_Recyclerview;
    ChatAdapter chatAdapter;
    LinearLayoutManager linearLayoutManager;
    PubNubHandler pubNubHandler;
    EditText message_et;
    ImageView im_send;
    String UUID;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatactivity);

        initview();
        initPubNub();
    }

    private void initPubNub() {
        pubNubHandler=PubNubHandler.getInstance();
        pubNubHandler.startToSubscribe(Constants.CHANNEL_NAME);
        pubNubHandler.getPubnub().addListener(new SubscribeCallback() {
            @Override
            public void status(PubNub pubnub, PNStatus status) {

            }

            @Override
            public void message(PubNub pubnub, final PNMessageResult message) {
                try {
                    Log.v(TAG, "message(" + JsonUtil.asJson(message) + ")");
                    JsonNode jsonMsg = message.getMessage();
                    final ChatModel chatModel = JsonUtil.convert(jsonMsg, ChatModel.class);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            chatAdapter.addMessage(chatModel);
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {

            }
        });
    }

    private void initview() {
        sp = getSharedPreferences(Constants.DATASTREAM_PREFS, MODE_PRIVATE);
        UUID=sp.getString(Constants.DATASTREAM_UUID,"");
        chat_Recyclerview=findViewById(R.id.chat_Recyclerview);
        message_et=findViewById(R.id.message_et);
        im_send=findViewById(R.id.im_send);
        im_send.setOnClickListener(this);

        linearLayoutManager=new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        chat_Recyclerview.setLayoutManager(linearLayoutManager);
        chatAdapter=new ChatAdapter(this);
        chat_Recyclerview.setAdapter(chatAdapter);
    }

    public void publish() {
        final Map<String, String> message = ImmutableMap.<String, String>of("sender", UUID, "message", message_et.getText().toString());

        pubNubHandler.getPubnub().publish().channel(Constants.CHANNEL_NAME).message(message).async(
                new PNCallback<PNPublishResult>() {
                    @Override
                    public void onResponse(PNPublishResult result, PNStatus status) {
                        try {
                            if (!status.isError()) {
                                message_et.setText("");
                                Log.v(TAG, "publish(" + JsonUtil.asJson(result) + ")");
                            } else {
                                Log.v(TAG, "publishErr(" + JsonUtil.asJson(status) + ")");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.im_send:
                if (message_et.getText().toString()!=null && !message_et.getText().toString().equalsIgnoreCase(""))
                publish();
                else
                    Toast.makeText(this, "Enter Message", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        PubNubHandler.getInstance().unSubcribe(Constants.CHANNEL_NAME);
        PubNubHandler.getInstance().setState(Constants.CHANNEL_NAME,UUID);
    }
}