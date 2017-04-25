package sxwang.me.ohmyyeelight.interaction;


import sxwang.me.ohmyyeelight.entity.Message;

/**
 * Created by Shaoxing on 25/04/2017.
 */

public interface MessageDecoder {
    Message decodeMessage(String data);
}
