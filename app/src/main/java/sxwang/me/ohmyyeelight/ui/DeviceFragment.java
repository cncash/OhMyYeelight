package sxwang.me.ohmyyeelight.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import sxwang.me.ohmyyeelight.R;
import sxwang.me.ohmyyeelight.entity.Device;

/**
 * Created by Shaoxing on 22/04/2017.
 */

public class DeviceFragment extends BottomSheetDialogFragment {

    public static DeviceFragment newInstance(Device device) {
        DeviceFragment fragment = new DeviceFragment();
        Bundle args = new Bundle();
        args.putString("DEVICE_SOURCE_TEXT", device.getSourceText());
        fragment.setArguments(args);
        return fragment;
    }

    private Device mDevice;

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
    }
}
