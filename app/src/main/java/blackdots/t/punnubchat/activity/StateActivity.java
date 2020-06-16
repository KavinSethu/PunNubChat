package blackdots.t.punnubchat.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.presence.PNHereNowChannelData;
import com.pubnub.api.models.consumer.presence.PNHereNowOccupantData;
import com.pubnub.api.models.consumer.presence.PNHereNowResult;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import blackdots.t.punnubchat.R;
import blackdots.t.punnubchat.Util.Constants;
import blackdots.t.punnubchat.Util.DateTimeUtil;
import blackdots.t.punnubchat.Util.JsonUtil;
import blackdots.t.punnubchat.adapter.ChatAdapter;
import blackdots.t.punnubchat.adapter.StateAdapter;
import blackdots.t.punnubchat.model.ChatModel;
import blackdots.t.punnubchat.model.StateModel;
import blackdots.t.punnubchat.pubnub.PubNubHandler;

public class StateActivity extends AppCompatActivity {

    private static final String TAG = StateActivity.class.getName();
    RecyclerView state_Recyclerview;
    StateAdapter stateAdapter;
    LinearLayoutManager linearLayoutManager;
    PubNubHandler pubNubHandler;
    SharedPreferences sp;
    String UUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_state);
        initview();
        initPubNub();
    }

    private void initPubNub() {
        pubNubHandler=PubNubHandler.getInstance();
        pubNubHandler.startToSubscribe(Constants.CHANNEL_NAME);

        pubNubHandler.getPubnub().hereNow()
                // tailor the next two lines to example
                .channels(new ArrayList<>(Collections.singletonList(Constants.CHANNEL_NAME)))
                .includeUUIDs(true)
                .includeState(true)
                .async(new PNCallback<PNHereNowResult>() {
                    @Override
                    public void onResponse(PNHereNowResult result, PNStatus status) {
                        if (status.isError()) {
                            // handle error
                            return;
                        }

                        for (PNHereNowChannelData channelData : result.getChannels().values()) {
                            System.out.println("---");
                            System.out.println("channel:" + channelData.getChannelName());
                            System.out.println("occupancy: " + channelData.getOccupancy());
                            System.out.println("occupants:");
                            for (PNHereNowOccupantData occupant : channelData.getOccupants()) {
                                System.out.println("uuid: " + occupant.getUuid() + " state: " + occupant.getState());

                                String timestamp = DateTimeUtil.getTimeStampUtc();
                                final StateModel sm = new StateModel(occupant.getUuid(), timestamp);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        stateAdapter.addMessage(sm);
                                    }
                                });
                            }
                        }
                    }
                });

        pubNubHandler.getPubnub().addListener(new SubscribeCallback() {
            @Override
            public void status(PubNub pubnub, PNStatus status) {

            }

            @Override
            public void message(PubNub pubnub, final PNMessageResult message) {
            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {
                try {
                    Log.v(TAG, "presenceP(" + JsonUtil.asJson(presence) + ")");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (presence.getEvent().equalsIgnoreCase("join")) {
                    Log.d("User","Joined");
                    String sender = presence.getUuid();
                    String timestamp = DateTimeUtil.getTimeStampUtc();
                    final StateModel sm = new StateModel(sender, timestamp);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            stateAdapter.addMessage(sm);
                        }
                    });
                }else if (presence.getEvent().equalsIgnoreCase("leave")){
                    Log.d("User","Leave");
                    String sender = presence.getUuid();
                    String timestamp = DateTimeUtil.getTimeStampUtc();
                    final StateModel sm = new StateModel(sender, timestamp);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            stateAdapter.removeUser(sm);
                        }
                    });
                }

            }
        });
    }

    private void initview() {
        sp = getSharedPreferences(Constants.DATASTREAM_PREFS, MODE_PRIVATE);
        UUID=sp.getString(Constants.DATASTREAM_UUID,"");
        state_Recyclerview=findViewById(R.id.state_Recyclerview);

        linearLayoutManager=new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        state_Recyclerview.setLayoutManager(linearLayoutManager);
        stateAdapter=new StateAdapter(this);
        state_Recyclerview.setAdapter(stateAdapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        PubNubHandler.getInstance().unSubcribe(Constants.CHANNEL_NAME);
        PubNubHandler.getInstance().setState(Constants.CHANNEL_NAME,UUID);
    }
}