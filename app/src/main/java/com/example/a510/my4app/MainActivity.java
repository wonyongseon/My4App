package com.example.a510.my4app;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final int BTH_ENABLE_CODE = 789;//int 앞에는 메모리 관리 명시
    protected BluetoothAdapter bthAdapter;
    protected BluetoothRx bthRx;
    protected BluetoothSerialService bthService;
    protected Button btFind,btFindable,btConnect,btWrite,btRead;
    protected EditText edWrite;
    protected TextView txRead;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BTH_ENABLE_CODE && resultCode == RESULT_OK){
            Toast.makeText(this,"Bluetooth is enabled!", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bthAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bthAdapter == null){
            Toast.makeText(this,"Bluetooth isn't supported!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!bthAdapter.isEnabled()){
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BTH_ENABLE_CODE);
        }
        if (!bthAdapter.isEnabled()) return;

        btFindable = (Button)findViewById(R.id.btFindable);
        btFindable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,120);
                startActivity(intent);
            }
        });

        btFind = (Button)findViewById(R.id.btFind);
        btFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bthAdapter.isDiscovering()) bthAdapter.cancelDiscovery(); //누군가 다른앱에서 디스커버리 하고 있으면 취소시키고 내꺼를 동작시킴
                bthAdapter.startDiscovery();
            }
        });

        bthRx = new BluetoothRx("yongseon");//메모리 할당해서 불러옴
        IntentFilter intentFilter = new  IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(bthRx, intentFilter);

        btConnect = (Button)findViewById(R.id.btConnect);
        btConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!bthRx.sDeviceAddress.isEmpty()) {
                    BluetoothDevice device = bthAdapter.getRemoteDevice(bthRx.sDeviceAddress);
                    bthService.connect(device);
                }
            }
        });

        bthService = new BluetoothSerialService(this, bthAdapter);

        txRead = (TextView) findViewById(R.id.txRead);
        btRead = (Button)findViewById(R.id.btRead);
        btRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = bthService.sReadBuffer;
                bthService.sReadBuffer = "";
                txRead.append(str);
            }
        });

        edWrite = (EditText) findViewById(R.id.edWrite);
        btWrite = (Button)findViewById(R.id.btWrite);
        btWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = edWrite.getText().toString();
                byte[] buf = new byte[1];
                for (int i = 0; i < str.length(); i++){
                    buf[0] = (byte)str.charAt(i);
                    bthService.write(buf);
                }
            }
        });

    }
}
