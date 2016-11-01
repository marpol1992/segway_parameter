package com.example.marek.segwayparameter;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;


public class Bluetooth extends Activity implements AdapterView.OnItemClickListener {


    public static void disconnect(){
        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
            mHandler.obtainMessage(DISCONECT).sendToTarget();

        }
    }


    public static void gethandler(Handler handler){mHandler = handler;}
    static Handler mHandler = new Handler();

    public static ConnectedThread connectedThread;
    public static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    protected static final int SUCCESS_CONNECT = 0;
    protected static final int MESSAGE_READ = 1;
    protected static final int DISCONECT = 2;
    ArrayAdapter<String> listAdapter;
    ListView listView;
    static BluetoothAdapter btAdapter;
    Set<BluetoothDevice> devicesArray;
    ArrayList<String> pairedDevices;
    ArrayList<BluetoothDevice> devices;
    IntentFilter filter;
    BroadcastReceiver receiver;
    private static String address_PC= "C0:18:85:D2:A7:4E";
    private static String address_SG= "00:12:6F:03:58:48";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        init();
        if (btAdapter==null){
            Toast.makeText(getApplicationContext(), "No bluetooth detected", Toast.LENGTH_SHORT).show();
            finish();
        }else{
            if (!btAdapter.isEnabled()){
                turnOnBT();
            }
           //getPairedDevices();
            //startDiscovery();
        }
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice selectedDevice = adapter.getRemoteDevice(address_SG);
        ConnectThread connect = new ConnectThread(selectedDevice);
        connect.start();
    }




    private void startDiscovery() {
        // TODO Auto-generated method stub
        btAdapter.cancelDiscovery();
        btAdapter.startDiscovery();
    }

    private void turnOnBT() {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(intent, 1);
    }


    private void getPairedDevices() {
        devicesArray = btAdapter.getBondedDevices();//Zwraca zestaw obiektów BluetoothDevice, które są połączone (sparowane) do lokalnej adaptacji
        if (devicesArray.size()>0){
            for(BluetoothDevice device:devicesArray){
                pairedDevices.add(device.getName());
            }
        }
    }

    private void init(){


        listView = (ListView)findViewById(R.id.listView);
        listView.setOnItemClickListener(this);
        listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,0);
        listView.setAdapter(listAdapter);
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        pairedDevices = new ArrayList<String>();
        filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        devices = new ArrayList<BluetoothDevice>();
        receiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)){
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    devices.add(device);
                    String s = "";
                    for(int a=0;a<pairedDevices.size();a++){
                        if (device.getName().equals(pairedDevices.get(a))){
                            //append
                            s = "(Paired)";
                            break;
                        }
                    }
                    listAdapter.add(device.getName() + " " + s + " " + "\n" + device.getAddress());

                }
                else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)){

                }else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){

                }else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)){
                    if (btAdapter.getState() == btAdapter.STATE_OFF){
                        turnOnBT();
                    }
                }
            }

        };

        registerReceiver(receiver, filter);
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        registerReceiver(receiver, filter);
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(receiver, filter);
        filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);


    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        //unregisterReceiver(receiver);
        if (btAdapter.isDiscovering()){
            btAdapter.cancelDiscovery();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED){
            Toast.makeText(getApplicationContext(), "Bluetooth must be enabled to continue", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // TODO Auto-generated method stub
        if (btAdapter.isDiscovering()){
            btAdapter.cancelDiscovery();
        }
        if (listAdapter.getItem(arg2).contains("(Paired)")){
            String message = "Połączono z urządzeniem o adresie:" + devices.get(arg2);
            Toast.makeText(Bluetooth.this,message,Toast.LENGTH_LONG).show();
            BluetoothDevice selectedDevice = devices.get(arg2);
            ConnectThread connect = new ConnectThread(selectedDevice);
            connect.start();

        }else {
            Toast.makeText(getApplicationContext(), "device is not paired", Toast.LENGTH_SHORT).show();
        }
    }



    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);//emulowanie portu RS232
            } catch (IOException e) { }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it will slow down the connection
            btAdapter.cancelDiscovery();

            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                mmSocket.connect();
                connectedThread = new ConnectedThread((BluetoothSocket)mmSocket);
                connectedThread.start();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                try {
                    mmSocket.close();
                } catch (IOException closeException) { }
                return;
            }

            // Toast.makeText(getApplicationContext(), "Connected!", Toast.LENGTH_SHORT).show();
            // Do work to manage the connection (in a separate thread)
            //ConnectedThread connected = new ConnectedThread((BluetoothSocket)mmSocket);
            //connected.start();

            mHandler.obtainMessage(SUCCESS_CONNECT, mmSocket).sendToTarget();
            finish();
        }

        /** Will cancel an in-progress connection, and close the socket */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    static class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();

            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }
        StringBuffer sbb = new StringBuffer();



        public void run() {

            byte[] buffer;  // buffer store for the stream
            int bytes;      // bytes returned from read()
            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    buffer = new byte[1000];
                    bytes = mmInStream.read(buffer);
                    mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                }
                catch (IOException e) {
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(byte[] bytes) {

            try {
                mmOutStream.write(bytes);

            } catch (IOException e) { }
        }
        public void write(int out) {
            try {
                mmOutStream.write(out);


            } catch (IOException e) {}
        }
        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

}