package sxwang.me.ohmyyeelight.interaction;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import sxwang.me.ohmyyeelight.entity.Message;

/**
 * Created by Shaoxing on 25/04/2017.
 */

public class DefaultMessageDecoder implements MessageDecoder {
    @Override
    public Message decodeMessage(String data) {
        if (data == null) {
            return null;
        }
        try {
            JSONObject jsonObject = new JSONObject(data);
            Message message = new Message();

            message.setId(jsonObject.optInt("id"));
            if (jsonObject.has("error")) {
                JSONObject errorObject = jsonObject.getJSONObject("error");
                Message.Error error = new Message.Error();
                error.setCode(errorObject.getInt("code"));
                error.setMessage(errorObject.getString("message"));
                message.setError(error);
            }
            if (jsonObject.has("result")) {
                JSONArray resultArray = jsonObject.getJSONArray("result");
                // TODO: extract result array
            }
            return message;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
