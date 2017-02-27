package com.example.fyy.quickturnoff;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Switch mySwitch;
    private TextView switchStatus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mySwitch = (Switch) findViewById(R.id.mySwitch);
        switchStatus = (TextView) findViewById(R.id.switchStatus);
        final WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        mySwitch.setChecked(wifi.isWifiEnabled());


        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if(isChecked){
                    switchStatus.setText("Switch is currently ON");
                    wifi.setWifiEnabled(true);


                }else{
                    switchStatus.setText("Switch is currently OFF");
                    wifi.setWifiEnabled(false);

                }

            }
        });

        //check the current state before we display the screen
        if(mySwitch.isChecked()){
            switchStatus.setText("Switch is currently ON");
        }
        else {
            switchStatus.setText("Switch is currently OFF");
        }


    }
}
