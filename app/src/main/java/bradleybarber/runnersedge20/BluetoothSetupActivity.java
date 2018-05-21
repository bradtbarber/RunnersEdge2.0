package bradleybarber.runnersedge20;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;


public class BluetoothSetupActivity extends Activity implements AdapterView.OnItemClickListener{
    ArrayAdapter<String> deviceListAdapter;
    ListView deviceListView;
    BluetoothAdapter btAdapter;
    Set<BluetoothDevice> devicesArray;
    ArrayList<String> pairedDevices;
    ArrayList<BluetoothDevice> btDevices;
    IntentFilter filter;
    BroadcastReceiver btReceiver;
    String weight;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_setup);

        weight = getIntent().getExtras().getString("weight");

        init();
        if(btAdapter == null){
            Toast.makeText(getApplicationContext(),"No Bluetooth detected",Toast.LENGTH_LONG).show();
            finish();
        }
        else{
            if(!btAdapter.isEnabled()){
                btAdapter.enable();
            }
            getPairedDevices();
            startDiscovery();
        }
    }

    private void startDiscovery() {
        btAdapter.cancelDiscovery();
        btAdapter.startDiscovery();
    }

    private void getPairedDevices(){
        devicesArray = btAdapter.getBondedDevices();
        if(devicesArray.size()>0){
            for(BluetoothDevice device:devicesArray){
                pairedDevices.add(device.getName());
            }
        }
    }

    private void init() {
        deviceListView = (ListView) findViewById(R.id.deviceListView);
        deviceListView.setOnItemClickListener(this);
        deviceListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, 0);
        deviceListView.setAdapter(deviceListAdapter);
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        pairedDevices = new ArrayList<>();
        filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        btDevices = new ArrayList<>();
        btReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if(BluetoothDevice.ACTION_FOUND.equals(action)){
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    btDevices.add(device);
                    String s = "";
                    for(int a = 0; a < pairedDevices.size(); a++){
                        if(device.getName().equals(pairedDevices.get(a))){
                            s = "(Paired)";
                            break;
                        }
                    }
                    deviceListAdapter.add(device.getName()+" "+s+" "+"\n"+device.getAddress());
                }
            }
        };

        registerReceiver(btReceiver, filter);
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        registerReceiver(btReceiver, filter);
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(btReceiver, filter);
        filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(btReceiver, filter);
    }

    @Override
    protected void onPause(){
        super.onPause();
        unregisterReceiver(btReceiver);
    }

    @Override
    protected void onStop(){
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_CANCELED){
            Toast.makeText(getApplicationContext(),"Bluetooth MUST be ENABLED",Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        if(btAdapter.isDiscovering()){
            btAdapter.cancelDiscovery();
        }
        if(deviceListAdapter.getItem(arg2).contains("(Paired)")){
            BluetoothDevice selectedDevice = btDevices.get(arg2);
            Intent startRun = new Intent (this, RunActivity.class);
            startRun.putExtra("btdevice", selectedDevice);
            startRun.putExtra("weight", weight);
            startActivity(startRun);
        }
        else{
            Toast.makeText(getApplicationContext(), "device is not paired", Toast.LENGTH_SHORT).show();
        }
    }
}