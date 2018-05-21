package bradleybarber.runnersedge20;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.UUID;


public class RunActivity extends ActionBarActivity {

    static int offSet = 48;
    int force;
    int i;
    long startTime;

    Button endRunButton;

    String weightString;
    Integer weight;

    ListView runReadout;
    ArrayAdapter<Integer> runReadoutAdapter;

    ArrayList<Integer> dataSet;
    ArrayList<Integer> timeSet;

    BluetoothAdapter btAdapter;
    BluetoothDevice bluetoothDevice;
    public static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    protected static final int SUCCESS_CONNECT = 0;
    protected static final int MESSAGE_READ = 1;

    Handler mHandler = new Handler(){
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            switch(msg.what){
                case SUCCESS_CONNECT:
                    startTime = System.currentTimeMillis();
                    ConnectedThread connectedThread = new ConnectedThread((BluetoothSocket)msg.obj);
                    connectedThread.start();
                    break;

                case MESSAGE_READ:
                    byte[] readBufferB = (byte[])msg.obj;
                    int readBuffer = readBufferB[0] - offSet;
                    force = readBuffer*weight;
                    if (force < 0) {
                        force = 0;
                    }
                    updateInterfaceAndDataLists(force);
                    break;

                default:

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);

        endRunButton = (Button) findViewById(R.id.endRunButton);
        endRunButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runResults();
            }
        });

        bluetoothDevice = getIntent().getExtras().getParcelable("btdevice");
        weightString = getIntent().getExtras().getString("weight");
        dataSet = new ArrayList<>();
        timeSet = new ArrayList<>();
        runReadout = (ListView) findViewById(R.id.runReadout);
        runReadoutAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, 0);
        runReadout.setAdapter(runReadoutAdapter);
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        weight = Integer.valueOf(weightString);

        i=0;

        ConnectThread connect = new ConnectThread(bluetoothDevice);
        connect.start();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_run, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void runResults(){
        btAdapter.disable();
        ConnectThread connect = new ConnectThread(bluetoothDevice);
        connect.cancel();

        Intent runResults = new Intent(this, RunResultsActivity.class);
        runResults.putIntegerArrayListExtra("data", dataSet);
        runResults.putIntegerArrayListExtra("time", timeSet);
        startActivity(runResults);
    }

    private void updateInterfaceAndDataLists (int force){
        if (force > 0) {
            runReadoutAdapter.add(force);
            i++;
            runReadoutAdapter.notifyDataSetChanged();
            runReadout.smoothScrollToPosition(i);
        }
        dataSet.add(force);
        timeSet.add((int)(System.currentTimeMillis() - startTime));
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // tmp = temporary object that is later assigned to mmSocket,
            // mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                tmp = mmDevice.createRfcommSocketToServiceRecord(MY_UUID);
            }
            catch (IOException e) {
                Toast.makeText(getApplicationContext(), "Socket unable to connect", Toast.LENGTH_SHORT).show();
            }
            mmSocket = tmp;
        }

        public void run() {
            btAdapter.cancelDiscovery();
            try {
                // Connect to the device through the socket.
                mmSocket.connect();
            }
            catch (IOException connectException) {
                // Unable to connect; close the socket
                try {
                    mmSocket.close();
                }
                catch (IOException closeException) {
                    Toast.makeText(getApplicationContext(), "Unable to close socket", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            mHandler.obtainMessage(SUCCESS_CONNECT, mmSocket).sendToTarget();
        }

        // Cancel an in-progress connection, close the socket
        public void cancel() {
            try {
                mmSocket.close();
            }
            catch (IOException e) {
                Toast.makeText(getApplicationContext(), "Unable to close socket", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Called when device is connected
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        //No need for OutStream, may use later
        //private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            //OutputStream tmpOut = null;

            // Get input/output streams, using temp since streams are final
            try {
                tmpIn = socket.getInputStream();
                //tmpOut = socket.getOutputStream();
            }
            catch (IOException e) {
                Toast.makeText(getApplicationContext(), "ERROR finding input", Toast.LENGTH_SHORT).show();
            }
            mmInStream = tmpIn;
            //mmOutStream = tmpOut;
        }

        public void run() {
            // buffer to store data from stream.
            byte[] buffer;
            // bytes returned from read()
            int bytes;
            // Keep listening to the InputStream until an exception occurs
            while (true) {
                //for (int i = 0; i < 20; i++){
                try {
                    // Read from the InputStream
                    // Place in buffer, will overwrite previous string.
                    buffer = new byte[1024];
                    bytes = mmInStream.read(buffer);
                    // Send the obtained bytes to the UI activity (.sendToTarget)
                    mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                }
                catch (IOException e)
                {
                    break;
                }
            }
        }

        // Call from the main activity to send data to the remote device
        // No need currently, may use later (?)
        /*public void write(byte[] bytes) {
            //Toast.makeText(getApplicationContext(), "Writting to device", Toast.LENGTH_SHORT).show();
            try {
                mmOutStream.write(bytes);
            }
            catch (IOException e) {
                Toast.makeText(getApplicationContext(), "Unable to send data", Toast.LENGTH_SHORT).show();
            }
        }*/

        //Call from the main activity to shutdown the connection
        public void cancel() {
            try {
                mmSocket.close();
            }
            catch (IOException e) {
                Toast.makeText(getApplicationContext(), "Unable to close socket", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
