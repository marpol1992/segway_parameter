package com.example.marek.segwayparameter;

// Created by marek on 2015-09-30.

import android.util.Log;

public class Calculculations {

    private int data_count = 0;
    private float Uaku = 0f;
    private final byte sample = 10;
    private float[] data = new float[sample];


 /*  public void Calculculations(){

   }*/
    public int CalcCRC16(byte[] data_array, int data_lenght) {
        int crc = 0xFFFF;
        //byte[] data = data_array;
        for (int i = 0; i < data_lenght; i++) {
            crc ^= (convert_Byte_to_Int(data_array[i])) << 8;
            crc = crc & 0x0000FFFF;
            for (int j = 0; j < 8; j++) {
                if ((crc & 0x8000) > 0) {
                    crc = (crc << 1) ^ 0x1021;
                    crc = crc & 0x0000FFFF;
                }
                else {
                    crc <<= 1;
                    crc = crc & 0x0000FFFF;
                }
            }
        }
        return  crc & 0x0000FFFF;
    }
    public int convert_Byte_to_Int(byte b) {
        if (b < 0) {
            return b + 256;
        } else return b;
    }
   public byte MSB_part_of_Bajt(int licznik){


        return(byte)((licznik>>8) & 0x000000FF);
    }

  public byte LSB_part_of_Bajt(int licznik){

        return (byte)(licznik & 0x000000FF);
    }

    public int parse_bytes(int mlody, int stary) {
        return 0xFFFF&((stary << 8) | (mlody));
    }

    public float two_bytes_to_float(int total, int value_fraction){
       float fraction = 0;
        if (value_fraction > 0 && value_fraction <10){
            fraction = value_fraction/10.0f;
        }else if(value_fraction > 10 && value_fraction < 100){
            fraction = value_fraction/100.0f;
        }else if(value_fraction > 100 && value_fraction < 1000){
            fraction = value_fraction/1000.0f;
        }else
        {
            fraction = value_fraction/100.0f;
        }

       return (((float)(total - 90)) + fraction);//minus 90 bo w segway'u dodalismy do total 90 zeby nie było liczb ujemnych
}
    public float Uzas(float ADC){
        data[data_count] = (float)(ADC*3.3f)/(4095f*0.077757685f);
        if(data[data_count] > 45){//zabezpieczenie przed błedami
            return Uaku;
        }
        data_count++;
        Log.d("Datacount", Float.toString(data_count));
        if(data_count >= sample) {
            data_count = 0;
            Uaku = 0;
            for (int i = 0; i < sample; i++) {
                Uaku += data[i];
                Log.d("Datacount", Float.toString(Uaku));
            }
            Uaku = Math.round((Uaku*100)/(sample));
        }
        return Uaku/100f;
    }
}
