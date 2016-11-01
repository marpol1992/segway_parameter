package com.example.marek.segwayparameter;

// Created by marek on 2015-09-30.

public class SendDataFrame {
    private final byte Header_frame        = (byte)0xFF;
    private int Lenght_Data = 0;
    private byte[] Data;
    private final byte Command_to_startADC = 0x04;
    private final byte Command_to_stopADC  = 0x05;
    private byte[] Frame = new byte[6];
    private byte HEADER_SMARTHOME_FRAME    = 0x05;
    Calculculations calculations = new Calculculations();
    public  SendDataFrame(){
        Frame[0] = Header_frame;
    }
    public void Request_to_startADC(){
        Frame[1] = Command_to_startADC;
        Frame[2] = calculations.MSB_part_of_Bajt(Lenght_Data);
        Frame[3] = calculations.LSB_part_of_Bajt(Lenght_Data);
        Frame[4] = calculations.MSB_part_of_Bajt(calculations.CalcCRC16(Frame, 4));
        Frame[5] = calculations.LSB_part_of_Bajt(calculations.CalcCRC16(Frame, 4));
        Bluetooth.connectedThread.write(Frame);
        Bluetooth.connectedThread.write(Frame);
        Bluetooth.connectedThread.write(Frame);


/*        for(byte frame: Frame){
            Bluetooth.connectedThread.write(frame);
*/
    }
    public void Request_to_stopADC(){
        Frame[1] = Command_to_stopADC;
        Frame[2] = calculations.MSB_part_of_Bajt(Lenght_Data);
        Frame[3] = calculations.LSB_part_of_Bajt(Lenght_Data);
        Frame[4] = calculations.MSB_part_of_Bajt(calculations.CalcCRC16(Frame, 4));
        Frame[5] = calculations.LSB_part_of_Bajt(calculations.CalcCRC16(Frame, 4));
        Bluetooth.connectedThread.write(Frame);
        Bluetooth.connectedThread.write(Frame);
        Bluetooth.connectedThread.write(Frame);

    }

    public void Request_to_Devices(byte adres,byte data){
        Frame[0] = HEADER_SMARTHOME_FRAME;
        Frame[1] = adres;
        Frame[2] = data;
        Frame[3] = calculations.MSB_part_of_Bajt(calculations.CalcCRC16(Frame, 3));
        Frame[4] = calculations.LSB_part_of_Bajt(calculations.CalcCRC16(Frame, 3));
        for (int i = 0;i<5;i++) {
            Bluetooth.connectedThread.write(Frame[i]);
        }
    }

}
