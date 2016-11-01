package com.example.marek.segwayparameter;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;



public class MainActivity extends ActionBarActivity {
    public final byte start = 1;
    public final byte stop  = 0;
    private byte flag_counter = 2;
    private byte data_flag1 = 0;
    private byte data_flag2 = 0;                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       ;
    boolean transmision_request = false;
    boolean status;
    MenuItem bluetoothStatusItem;
    MenuItem bluetooth_startTransmision;

    private final Handler mHandler = new Handler();
    private Runnable mTimer1;

    GraphView graph;
    private LineGraphSeries<DataPoint> mSeries1;
    private LineGraphSeries<DataPoint> mSeries2;

    private double graph2LastXValue = 5d;
    ProcessingFrame processingFrame = new ProcessingFrame();

    TextView Res_M1I0 ;
    TextView Res_M2I0 ;
    TextView Res_M1IZ ;
    TextView Res_M2IZ ;
    TextView Uaku_value  ;
    TextView Angle_value ;

    RadioButton radioA,radioB, radioC,radioD, radioE, radioF;
    boolean radio1, radio2, radio3, radio4, radio5, radio6, radio7;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


       // Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
       // startActivityForResult(intent, 1);

        this.getWindow().setFlags(WindowManager.LayoutParams.
                FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//Hide Status bar

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        IntentFilter filter1 = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
        IntentFilter filter2 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        IntentFilter filter3 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        this.registerReceiver(BTReceiver, filter1);
        this.registerReceiver(BTReceiver, filter2);
        this.registerReceiver(BTReceiver, filter3);


        radio_init();
        parameter_declaration();
        graph_view_init();
        processingFrame.start();

    }

    private void radio_init() {
        radioA = (RadioButton) findViewById(R.id.radioButton);
        radioB = (RadioButton) findViewById(R.id.radioButton2);
        radioC = (RadioButton) findViewById(R.id.radioButton3);
        radioD = (RadioButton) findViewById(R.id.radioButton4);
        radioE = (RadioButton) findViewById(R.id.radioButton5);
        radioF = (RadioButton) findViewById(R.id.radioButton6);
        radioF.setChecked(true);
    }


    private final BroadcastReceiver BTReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                //Do something if connected
                bluetoothStatusItem.setIcon(R.drawable.ic_bluetooth_connected_black_24dp);
                bluetoothStatusItem.setTitle("CONNECTED");

                Toast.makeText(getApplicationContext(), "BT Connected", Toast.LENGTH_SHORT).show();
            }
            else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                //Do something if disconnected
                bluetoothStatusItem.setIcon(R.drawable.ic_bluetooth_disabled_black_24dp);
                Toast.makeText(getApplicationContext(), "BT Disconnected", Toast.LENGTH_SHORT).show();
                bluetoothStatusItem.setTitle("DISCONNECTED");
            }
            //else if...
        }
    };
    @Override
    public void onResume() {
        super.onResume();
        mTimer1 = new Runnable() {
            @Override
            public void run() {
                Angle_value.setText(Float.toString(processingFrame.graph_point[0]));
                Res_M1I0.setText(Float.toString(processingFrame.graph_point[1]));
                Res_M2I0.setText(Float.toString(processingFrame.graph_point[2]));
                Res_M1IZ.setText(Float.toString(processingFrame.graph_point[3]));
                Res_M2IZ.setText(Float.toString(processingFrame.graph_point[4]));
                //float Uaku = processingFrame.graph_point[5]*(3.3f/4095f)*12.72f;
                Uaku_value.setText(Float.toString(processingFrame.graph_point[5]));
                if (Bluetooth.connectedThread != null) {
                    graph2LastXValue += 1d;
                }
                if(processingFrame.graph_point[data_flag1] > -45 & processingFrame.graph_point[data_flag1] < 45) {//proste filtrowanie
                    mSeries1.appendData(new DataPoint(graph2LastXValue, processingFrame.graph_point[data_flag1]), true, 100);
                }
                if(data_flag2<6 & processingFrame.graph_point[data_flag2] > -45 & processingFrame.graph_point[data_flag1] < 45) {
                    mSeries2.appendData(new DataPoint(graph2LastXValue, processingFrame.graph_point[data_flag2]), true, 100);
                }
                mHandler.postDelayed(this, 200);
            }
        };
        mHandler.postDelayed(mTimer1, 1000);
    }
    @Override
    public void onPause() {
        mHandler.removeCallbacks(mTimer1);
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        bluetoothStatusItem = menu.findItem(R.id.status_connected);
        bluetooth_startTransmision = menu.findItem(R.id.start);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch(item.getItemId()){
            //noinspection SimplifiableIfStatement
            case R.id.connect:{
                startActivity(new Intent("android.intent.action.BT2"));
                // startActivity(new Intent(this,bluetooth.class));
            }
            case R.id.disconnect: {
                Bluetooth.disconnect();
                status = true;
                //Bluetooth.ConnectThread.cancel();
            }
            case R.id.status_connected: {
                status = true;
            }
            case R.id.start:{
                if (Bluetooth.connectedThread != null) {
                    if (transmision_request == false) {
                        Bluetooth.connectedThread.write(start);
                        bluetooth_startTransmision.setIcon(R.drawable.ic_pause_black_48dp);
                        bluetooth_startTransmision.setTitle("STOP");
                    } else {
                        Bluetooth.connectedThread.write(stop);
                        bluetooth_startTransmision.setIcon(R.drawable.ic_play_arrow_black_48dp);
                        bluetooth_startTransmision.setTitle("START");
                    }
                    transmision_request = !transmision_request;
                }
            }

            default:
                return super.onOptionsItemSelected(item);
        }

    }
@Override
public void onWindowFocusChanged(boolean hasFocus) {
    super.onWindowFocusChanged(hasFocus);
    View decorView = getWindow().getDecorView();
    if (hasFocus) {
        decorView.setSystemUiVisibility(
                 View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
}
    private void parameter_declaration(){
        TextView M1I0 = (TextView)findViewById(R.id.M1I0);
        TextView M2I0 = (TextView)findViewById(R.id.M2I0);
        TextView M1IZ = (TextView)findViewById(R.id.M1IZ);
        TextView M2IZ = (TextView)findViewById(R.id.M2IZ);
        TextView Uaku_sign     = (TextView)findViewById(R.id.Uaku_sign);

        Res_M1I0 = (TextView)findViewById(R.id.resM1I0);
         Res_M2I0 = (TextView)findViewById(R.id.resM2I0);
        Res_M1IZ = (TextView)findViewById(R.id.resM2IZ);
         Res_M2IZ = (TextView)findViewById(R.id.resM1IZ);
         Uaku_value    = (TextView)findViewById(R.id.Uaku_value);
        Angle_value    = (TextView)findViewById(R.id.Angle_value);


        M1I0.setText(Html.fromHtml("M1<sub>Io</sub>= "));
        M2I0.setText(Html.fromHtml("M2<sub>Io</sub>= "));
        M1IZ.setText(Html.fromHtml("M1<sub>Iz</sub>= "));
        M2IZ.setText(Html.fromHtml("M2<sub>Iz</sub>= "));
        Uaku_sign.setText(Html.fromHtml("U<sub>aku</sub>= "));
    }

    private void graph_view_init(){
        graph = (GraphView) findViewById(R.id.graph);
        mSeries1 = new LineGraphSeries<DataPoint>();
        mSeries2 = new LineGraphSeries<DataPoint>();

        graph.addSeries(mSeries2);
        graph.addSeries(mSeries1);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(40);
        graph.clearAnimation();
       // graph.setPadding(500,0,0,0);
        graph.getGridLabelRenderer().setLabelsSpace(0);
        graph.getGridLabelRenderer().setVerticalAxisTitle("Amplituda");
        graph.getGridLabelRenderer().setNumHorizontalLabels(10);
        graph.getGridLabelRenderer().setNumVerticalLabels(10);
       // graph.getGridLabelRenderer().);
        graph.getViewport().setScalable(true);
        graph.getViewport().setScrollable(true);
        graph.getGridLabelRenderer().setPadding(40);
        graph.getGridLabelRenderer().setGridColor(Color.RED);
        //mSeries1.setColor(Color.RED);
        //mSeries2.setColor(Color.BLACK);
    }
    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radioButton:
                if (checked) {
                    flag_counter++;
                    if(flag_counter >=3){
                        flag_counter = 1;
                        radioB.setChecked(false);
                        radioC.setChecked(false);
                        radioD.setChecked(false);
                        radioE.setChecked(false);
                        radioF.setChecked(false);
                        data_flag1 = 1;
                        data_flag2 = 6;
                        mSeries1.setColor(Color.MAGENTA);
                        graph.clearAnimation();
                    }else{
                        data_flag2 = 1;
                        mSeries2.setColor(Color.MAGENTA);
                    }


                    break;
                }
            case R.id.radioButton2:
                if (checked)
                    if (checked) {
                        flag_counter++;
                        if(flag_counter >=3){
                            flag_counter = 1;
                            radioA.setChecked(false);
                            radioC.setChecked(false);
                            radioD.setChecked(false);
                            radioE.setChecked(false);
                            radioF.setChecked(false);
                            data_flag1 = 2;
                            data_flag2 = 6;
                            mSeries1.setColor(Color.BLACK);
                            graph.clearAnimation();
                        }else{
                            data_flag2 = 2;
                            mSeries2.setColor(Color.BLACK);
                        }

                        break;
                    }
            case R.id.radioButton3:
                if (checked) {
                    flag_counter++;
                    if(flag_counter >=3){
                        flag_counter = 1;
                        radioB.setChecked(false);
                        radioA.setChecked(false);
                        radioD.setChecked(false);
                        radioE.setChecked(false);
                        radioF.setChecked(false);
                        data_flag1 = 3;
                        data_flag2 = 6;
                        graph.clearAnimation();
                        mSeries1.setColor(Color.GREEN);
                    }else{
                        data_flag2 = 3;
                        mSeries2.setColor(Color.GREEN);
                    }

                    break;
                }
            case R.id.radioButton4:
                if (checked)
                    if (checked) {
                        flag_counter++;
                        if(flag_counter >=3){
                            flag_counter = 1;
                            radioB.setChecked(false);
                            radioC.setChecked(false);
                            radioA.setChecked(false);
                            radioE.setChecked(false);
                            radioF.setChecked(false);
                            data_flag1 = 4;
                            data_flag2 = 6;
                            mSeries1.setColor(Color.YELLOW);
                            graph.clearAnimation();
                        }else{
                            data_flag2 = 4;
                            mSeries2.setColor(Color.YELLOW);
                        }

                        break;
                    }
            case R.id.radioButton5:
                if (checked) {
                    flag_counter++;
                    if(flag_counter >=3){
                        flag_counter = 1;
                        radioB.setChecked(false);
                        radioC.setChecked(false);
                        radioD.setChecked(false);
                        radioA.setChecked(false);
                        radioF.setChecked(false);
                        data_flag1 = 5;
                        data_flag2 = 6;

                        graph.clearAnimation();
                    }else{
                        data_flag2 = 5;

                    }

                    break;
                }
            case R.id.radioButton6:
                if (checked) {
                    flag_counter++;
                    if(flag_counter >=3){
                        flag_counter = 1;
                        radioB.setChecked(false);
                        radioC.setChecked(false);
                        radioD.setChecked(false);
                        radioE.setChecked(false);
                        radioA.setChecked(false);
                        data_flag1 = 0;
                        data_flag2 = 6;
                        mSeries1.setColor(Color.BLUE);
                        graph.clearAnimation();
                    }else{
                        data_flag2 = 0;
                        mSeries2.setColor(Color.BLUE);
                    }

                    break;
                }

        }
        Log.d("Data flag 1",Byte.toString(data_flag1));
        Log.d("Data flag 2",Byte.toString(data_flag2));
    }

    }
