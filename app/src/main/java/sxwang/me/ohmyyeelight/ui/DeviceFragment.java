package sxwang.me.ohmyyeelight.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import sxwang.me.ohmyyeelight.R;
import sxwang.me.ohmyyeelight.Schedulers;
import sxwang.me.ohmyyeelight.entity.Device;
import sxwang.me.ohmyyeelight.interaction.Command;
import sxwang.me.ohmyyeelight.interaction.Commander;

/**
 * Created by Shaoxing on 22/04/2017.
 */

public class DeviceFragment extends BottomSheetDialogFragment {

    public static DeviceFragment newInstance(@NonNull Device device) {
        DeviceFragment fragment = new DeviceFragment();
        Bundle args = new Bundle();
        args.putString("DEVICE_SOURCE_TEXT", device.getSourceText());
        fragment.setArguments(args);
        return fragment;
    }

    private Device mDevice;
    private Command mCommand;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_device, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mDevice == null) {
            mDevice = Device.parse(getArguments().getString("DEVICE_SOURCE_TEXT", ""));
        }

        Knob knob = (Knob) view.findViewById(R.id.knob_color_temperature);
        knob.setOnTwistListener(new Knob.OnTwistListener() {
            @Override
            public void onTwist(Knob knob, float degrees) {
                Log.i("KK", "onTwist: " + degrees);
            }
        });

        mCommand = Commander.newBuilder()
                .withHost(mDevice.getHost())
                .withPort(mDevice.getPort())
                .build()
                .find(Command.class);

        final TextView brightnessText = (TextView) view.findViewById(R.id.brightness_text);
        final SeekBar brightnessSeekBar = (SeekBar) view.findViewById(R.id.brightness_seek_bar);
        brightnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    brightnessText.setText(getString(R.string.brightness_format, progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(final SeekBar seekBar) {
                Schedulers.getInstance().runOnIoThread(new Runnable() {
                    @Override
                    public void run() {
                        mCommand.setBright(seekBar.getProgress(), "smooth", 500);
                    }
                });
            }
        });

    }
}
