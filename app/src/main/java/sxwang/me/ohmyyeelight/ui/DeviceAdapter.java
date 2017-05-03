package sxwang.me.ohmyyeelight.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import sxwang.me.ohmyyeelight.R;
import sxwang.me.ohmyyeelight.entity.Device;

/**
 * Created by Shaoxing on 22/04/2017.
 */

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder> implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private List<Device> mData = new ArrayList<>();
    private OnItemClickListener<Device> mOnItemClickListener;
    private OnItemCheckedChangeListener<Device> mOnItemCheckedChangeListener;

    public DeviceAdapter(List<Device> data) {
        setData(data);
    }

    public void setData(List<Device> data) {
        mData.clear();
        if (data != null) {
            mData.addAll(data);
        }
        notifyDataSetChanged();
    }

    public void appendData(Device... data) {
        for (Device device : data) {
            boolean found = false;
            for (Device exist : mData) {
                if (device.equals(exist)) {
                    found = true;
                }
            }
            if (!found) {
                mData.add(device);
                notifyItemInserted(mData.size() - 1);
            }
        }
    }

    public List<Device> getData() {
        return mData;
    }

    public OnItemClickListener<Device> getOnItemClickListener() {
        return mOnItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener<Device> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public OnItemCheckedChangeListener<Device> getOnItemCheckedChangeListener() {
        return mOnItemCheckedChangeListener;
    }

    public void setOnItemCheckedChangeListener(OnItemCheckedChangeListener<Device> onItemCheckedChangeListener) {
        mOnItemCheckedChangeListener = onItemCheckedChangeListener;
    }

    @Override
    public DeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        DeviceViewHolder vh = new DeviceViewHolder(inflater.inflate(R.layout.item_device, parent, false));
        vh.itemView.setOnClickListener(this);
        vh.mPowerSwitch.setOnCheckedChangeListener(this);
        return vh;
    }

    @Override
    public void onBindViewHolder(DeviceViewHolder holder, int position) {
        Device device = mData.get(position);
        holder.itemView.setTag(device);
        holder.mPowerSwitch.setTag(device);
        holder.bind(device);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public void onClick(View v) {
        Device device = (Device) v.getTag();
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v, device);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Device device = (Device) buttonView.getTag();
        if (mOnItemCheckedChangeListener != null) {
            mOnItemCheckedChangeListener.onItemCheckedChanged(buttonView, isChecked, device);
        }
    }

    static class DeviceViewHolder extends RecyclerView.ViewHolder {
        final TextView mModelText;
        final Switch mPowerSwitch;

        public DeviceViewHolder(View itemView) {
            super(itemView);
            mModelText = (TextView) itemView.findViewById(R.id.text_model);
            mPowerSwitch = (Switch) itemView.findViewById(R.id.switch_power);
        }

        public void bind(Device device) {
            mModelText.setText(device.getModel());
            mPowerSwitch.setChecked(device.isOn());
        }
    }

    interface OnItemClickListener<T> {
        void onItemClick(View v, T data);
    }

    interface OnItemCheckedChangeListener<T> {
        void onItemCheckedChanged(CompoundButton v, boolean isChecked, T data);
    }

}
