package sxwang.me.ohmyyeelight.interaction;

import java.lang.reflect.Method;

/**
 * Created by Shaoxing on 25/04/2017.
 */

public interface MessageEncoder {
    String encodeMessage(Method method, Object[] args);
}
