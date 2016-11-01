package com.example.marek.segwayparameter;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

/*
  Created by marek on 2015-09-16.
 */
 public class ProcessingFrame extends Thread {
    final int TIME_OUT = 10;
    private boolean END_DATA_READ = false;
    private int BUFFER_SIZE = 500;
    public byte[] readBuffer;
    public int bytes;
    public byte[] bufer = new byte[BUFFER_SIZE];
    public float[]  graph_point = new float[BUFFER_SIZE];
    public int stary_bajt;
    public int mlody_bajt;
    public int bytes_all;
    public double point;
    public int CRC;
    public int CRC_parse;
    private boolean SET_THREAD = true;
    public int  Error_count;
    protected static final int SUCCESS_CONNECT = 0;
    protected static final int DISCONECT = 2;
    public boolean flaga = false;
    private long currentTime_start = 0;
    private int counter_bytes;
    Calculculations calculations = new Calculculations();
    static Handler mHandler = new Handler();
    public static void get_handler(Handler handler){mHandler = handler;}
    Handler hHandler;{
        hHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                //TODO Auto-generated method stub
                super.handleMessage(msg);
                switch (msg.what) {
                        case Bluetooth.MESSAGE_READ:
                             if (Bluetooth.connectedThread != null) {
                                 Konstruktor(msg.obj, msg.arg1);
                                 break;
                             }
                            case Bluetooth.DISCONECT:{
                                mHandler.obtainMessage(DISCONECT).sendToTarget();
                                break;
                            }
                            case Bluetooth.SUCCESS_CONNECT:{
                                mHandler.obtainMessage(SUCCESS_CONNECT).sendToTarget();
                                break;
                            }
                }
            }

        };
    }
  /*  @Override
    public void run() {
        while(true){
            if((System.currentTimeMillis() - currentTime_start) >= TIME_OUT || bytes_all >= 12){
                END_DATA_READ = true;
                counter_bytes = bytes_all;
                bytes_all = 0;
                END_DATA_READ = true;
            }

            if (END_DATA_READ) {
                int counter = 0;
                Log.d("point", "DANE");
                for (int i = 0; i < 9; i += 2) {
                    graph_point[counter] = calculations.two_bytes_to_float(calculations.convert_Byte_to_Int(bufer[i]),calculations.convert_Byte_to_Int(bufer[i+1]));
                    counter++;
                }
                Log.d("point:", Byte.toString(bufer[0]));
                Log.d("point:", Byte.toString(bufer[1]));
                Log.d("point:", "Parsowanie:");
                Log.d("point:", Float.toString(graph_point[0]));

                graph_point[counter]=  calculations.Uzas(calculations.parse_bytes(calculations.convert_Byte_to_Int(bufer[11]), calculations.convert_Byte_to_Int(bufer[10])));
                END_DATA_READ = false;
            }



        }

    }*/



    public ProcessingFrame(){
        Bluetooth.gethandler(hHandler);
    }
    public void Konstruktor (Object obj, int arg1){
        if(bytes_all == 0){
            currentTime_start = System.currentTimeMillis();
            Log.d("WSZEDŁ", "START");

        }
        Log.d("WSZEDŁ", "PROCESS");
        readBuffer = (byte[]) obj;
        bytes = arg1;
        System.arraycopy(readBuffer, 0, bufer, bytes_all, bytes);
        bytes_all+=bytes;
        if((System.currentTimeMillis() - currentTime_start) >= TIME_OUT || bytes_all >= 12){
            END_DATA_READ = true;
            counter_bytes = bytes_all;
            bytes_all = 0;
            END_DATA_READ = true;
        }
        if (END_DATA_READ) {
            Thread thdA = new Thread(r);
            thdA.start();
        }
        }


    /*Thread thdA = new Thread(r);
            thdA.start();*/


    Runnable r = new Runnable()
    {
        @Override
        public void run()
        {
            int counter = 0;
            for (int i = 0; i < 9; i += 2) {
                graph_point[counter] = calculations.two_bytes_to_float(calculations.convert_Byte_to_Int(bufer[i]),calculations.convert_Byte_to_Int(bufer[i+1]));
                counter++;
            }
            graph_point[counter]=  calculations.Uzas(calculations.parse_bytes(calculations.convert_Byte_to_Int(bufer[11]), calculations.convert_Byte_to_Int(bufer[10])));
            END_DATA_READ = false;

        }
    };



    public void SET_THREAD(boolean b) {
        SET_THREAD = b;
    }
}