package blackdots.t.punnubchat.pubnub;

import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.presence.PNGetStateResult;
import com.pubnub.api.models.consumer.presence.PNSetStateResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import blackdots.t.punnubchat.Util.Constants;

public class PubNubHandler {

    private static final String TAG = "PubNubHandler";
    private static PubNubHandler INSTANCE = null;
    private PubNub pubnub;

    public static PubNubHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PubNubHandler();
        }
        return(INSTANCE);
    }

    public PubNubHandler() {
        PNConfiguration pnConfiguration = new PNConfiguration();
        pnConfiguration.setPublishKey(Constants.PUBNUB_PUBLISH_KEY);
        pnConfiguration.setSubscribeKey(Constants.PUBNUB_SUBSCRIBE_KEY);
        pubnub = new PubNub(pnConfiguration);
    }

    public PubNub getPubnub() {
        return pubnub;
    }

    public void startToSubscribe(String Channel){
        pubnub.subscribe()
                .channels(new ArrayList<>(Collections.singletonList(Channel)))
                .withPresence()
                .execute();
    }

    public void unSubcribe(String Channel){
        pubnub.unsubscribe()
                .channels(new ArrayList<>(Collections.singletonList(Channel)))
                .execute();
    }

    public void getState(String Channel,String userId){
        pubnub.getPresenceState()
                .channels(new ArrayList<>(Collections.singletonList(Channel)))
                .uuid(userId)
                .async(new PNCallback<PNGetStateResult>() {
                    @Override
                    public void onResponse(PNGetStateResult result, PNStatus status) {
                        if (!status.isError()) {
                            // handle state setting response
                        }
                    }
                });
    }

    public void setState(String Channel, String userId){

    }
}
