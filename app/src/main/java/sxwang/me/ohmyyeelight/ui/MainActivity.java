package sxwang.me.ohmyyeelight.ui;

import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import sxwang.me.ohmyyeelight.DeviceController;
import sxwang.me.ohmyyeelight.R;
import sxwang.me.ohmyyeelight.entity.Device;

public class MainActivity extends BaseActivity implements DeviceAdapter.OnItemClickListener<Device>, DeviceController.OnDeviceSetChangeListener {

    private RecyclerView mDeviceRecyclerView;
    private DeviceAdapter mDeviceAdapter;
    private Handler mHandler = new Handler();

    private DeviceController mDeviceController = DeviceController.DEFAULT_INSTANCE;

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
                onItemClick(null, Device.parse(""));
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

        Thread searchThread = new Thread(new Runnable() {
            @Override
            public void run() {
                mDeviceController.searchDevice();
            }
        });
        searchThread.start();

        Thread listenThread = new Thread(new Runnable() {
            @Override
            public void run() {
                WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
                WifiManager.MulticastLock lock = wifiManager.createMulticastLock("ohmyyeelight");
                lock.acquire();

                mDeviceController.listenToAdvertisement();

                lock.release();
            }
        });
        listenThread.start();
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
