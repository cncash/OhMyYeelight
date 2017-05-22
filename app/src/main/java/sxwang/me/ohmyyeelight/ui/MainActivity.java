package sxwang.me.ohmyyeelight.ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import sxwang.me.ohmyyeelight.DeviceController;
import sxwang.me.ohmyyeelight.R;
import sxwang.me.ohmyyeelight.Schedulers;
import sxwang.me.ohmyyeelight.Utils;
import sxwang.me.ohmyyeelight.entity.Device;

public class MainActivity extends BaseActivity implements DeviceAdapter.OnItemClickListener<Device>, DeviceController.OnDeviceSetChangeListener {

    RecyclerView mDeviceRecyclerView;
    DeviceAdapter mDeviceAdapter;
    Handler mHandler = new Handler();

    DeviceController mDeviceController = DeviceController.DEFAULT_INSTANCE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Schedulers.getInstance().runOnIoThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("toggle", "run: ");
                        mDeviceController.toggle();
                    }
                });
            }
        });

        mDeviceRecyclerView = $(R.id.rv_device);
        mDeviceRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mDeviceRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mDeviceRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        mDeviceAdapter = new DeviceAdapter(null);
        mDeviceAdapter.setOnItemClickListener(this);
        mDeviceRecyclerView.setAdapter(mDeviceAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mDeviceAdapter.setData(null);
        mDeviceController.setContinueSearching(true);
        mDeviceController.addOnDeviceSetChangeListener(this);


        if (!Utils.isNetworkAvailable(this)) {
            // wifi not connected
            Snackbar.make(mDeviceRecyclerView, R.string.no_network, Snackbar.LENGTH_LONG).show();
            return;
        }
        Schedulers.getInstance().runOnIoThread(new Runnable() {
            @Override
            public void run() {
                mDeviceController.searchDevice();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDeviceController.setContinueSearching(false);
        mDeviceController.removeOnDeviceSetChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(SettingsActivity.newIntent(this));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onItemClick(View v, Device data) {
        DeviceFragment deviceFragment = DeviceFragment.newInstance(data);
        deviceFragment.show(getSupportFragmentManager(), null);
    }

    @Override
    public void OnNewDevice(DeviceController controller, final Device device) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mDeviceAdapter.appendData(device);
            }
        });
    }
}
