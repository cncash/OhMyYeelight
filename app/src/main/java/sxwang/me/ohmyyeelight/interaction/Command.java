package sxwang.me.ohmyyeelight.interaction;

import sxwang.me.ohmyyeelight.entity.Message;
import sxwang.me.ohmyyeelight.interaction.annotation.CmdMethod;

/**
 * Created by Shaoxing on 25/04/2017.
 */

public interface Command {
    @CmdMethod("get_prop")
    Message getProp(String... params);

    @CmdMethod("set_ct_abx")
    Message setCtAbx(int ctValue, String effect, int duration);

    @CmdMethod("set_rgb")
    Message setRgb(int rgbValue, String effect, int duration);

    @CmdMethod("set_hsv")
    Message setHsv(int hue, int sat, String effect, int duration);

    @CmdMethod("set_bright")
    Message setBright(int brightness, String effect, int duration);

    @CmdMethod("set_power")
    Message setPower(String power, String effect, int duration);

    @CmdMethod("toggle")
    Message toggle();

    @CmdMethod("set_default")
    Message setDefault();

    @CmdMethod("start_cf")
    Message startCf(int count, int action, String flowExpression);

    @CmdMethod("stop_cf")
    Message stopCf();

    @CmdMethod("set_scene")
    Message setScene(String clazz, int val1, int val2, int val3);

    @CmdMethod("cron_add")
    Message cronAdd(int type, int value);

    @CmdMethod("cron_get")
    Message cronGet(int type);

    @CmdMethod("cron_del")
    Message cron_del(int type);

    @CmdMethod("set_adjust")
    Message setAdjust(String action, String prop);

    @CmdMethod("set_music")
    Message setMusic(int action, String host, int port);

    @CmdMethod("set_name")
    Message setName(String name);
}
