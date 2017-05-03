package sxwang.me.ohmyyeelight.entity;

/**
 * Created by Shaoxing on 25/04/2017.
 */

public class Message {
    private int id;
    private Object[] result;
    private Error error;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Object[] getResult() {
        return result;
    }

    public void setResult(Object[] result) {
        this.result = result;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

    public static class Error {
        private int code;
        private String message;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public static final String EFFECT_SMOOTH = "smooth";
    public static final String EFFECT_SUDDEN = "sudden";

    public static final int DURATION_MIN = 30;
    public static final int BRIGHTNESS_MIN = 1;
    public static final int BRIGHTNESS_MAX = 100;

    public static final String ACTION_INCREASE = "increase";
    public static final String ACTION_DECREASE = "decrease";
    public static final String ACTION_CIRCLE = "circle";
    public static final String PROP_BRIGHT = "bright";
    public static final String PROP_CT = "ct";
    public static final String PROP_COLOR = "color";
}
