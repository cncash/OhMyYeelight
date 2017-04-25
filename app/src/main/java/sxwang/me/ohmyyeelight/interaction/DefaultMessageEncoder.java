package sxwang.me.ohmyyeelight.interaction;

import java.lang.reflect.Method;

import sxwang.me.ohmyyeelight.Utils;
import sxwang.me.ohmyyeelight.interaction.annotation.CmdMethod;

/**
 * Created by Shaoxing on 25/04/2017.
 */

public class DefaultMessageEncoder implements MessageEncoder {

    @Override
    public String encodeMessage(Method method, Object[] args) {
        CmdMethod methodAnnotation = method.getAnnotation(CmdMethod.class);

        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("{\"id\": 1, ");
        messageBuilder.append("\"method\": \"")
                .append(methodAnnotation != null ? methodAnnotation.value() : Utils.toUnderlined(method.getName()))
                .append("\", ");
        messageBuilder.append("\"params\": ");
        messageBuilder.append(flatEncodeArray(args));
        messageBuilder.append("}\r\n");
        String message = messageBuilder.toString();

        return message;
    }

    private String flatEncodeArray(Object[] args) {
        return flatEncodeArray(args, true);
    }

    private String flatEncodeArray(Object[] args, boolean withBrackets) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            if (args[i] == null || args[i] instanceof Number || args[i] instanceof Boolean) {
                builder.append(args[i]);
            } else if (args[i].getClass().isArray()) {
                builder.append(flatEncodeArray((Object[]) args[i], false));
            } else {
                builder.append("\"").append(args[i]).append("\"");
            }

            if (i < args.length - 1) {
                builder.append(", ");
            }
        }
        return withBrackets ? ("[" + builder.toString() + "]") : builder.toString();
    }
}
