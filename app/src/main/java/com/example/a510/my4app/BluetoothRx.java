package com.example.a510.my4app;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class BluetoothRx extends BroadcastReceiver {
    public String sDeviceName, sDeviceAddress;

    public BluetoothRx(String deviceName) {
        sDeviceName = deviceName;
        sDeviceAddress = "";
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //throw new UnsupportedOperationException("Not yet implemented");
        String action = intent.getAction();
        if (action.equals(BluetoothDevice.ACTION_FOUND)){
            Toast.makeText(context,"Discovering the BluetoothDevice!",Toast.LENGTH_SHORT).show();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (sDeviceName.equalsIgnoreCase(device.getName())){
                sDeviceAddress = device.getAddress();
                Toast.makeText(context,"Device"+sDeviceName+": "+sDeviceAddress,Toast.LENGTH_SHORT).show();
            }
        }
    }
}
