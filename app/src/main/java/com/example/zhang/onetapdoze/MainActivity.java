package com.example.zhang.onetapdoze;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;


public class MainActivity extends Library {

    int red = Color.RED;
    int green = Color.GREEN;
    String hint;

    PowerManager powermanger;
    Doze doze;
    TextView Doze_Mode;
    EditText editText;


    NotificationManager notuficationmanger;
    Notification notification;
    SwitchCompat switchCompat;
    TextView notification_bit;
    boolean Notify_Flag = false;

    private IntentFilter intentFilter;
    private IdleModeChange idlemodechage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        powermanger = (PowerManager) getSystemService(Context.POWER_SERVICE);
        Doze_Mode = (TextView) findViewById(R.id.Doze_Mode);
        editText = (EditText)findViewById(R.id.edit_text);
        doze = new Doze(powermanger);
        hint = editText.getText().toString();


        intentFilter = new IntentFilter();
        intentFilter.addAction(powermanger.ACTION_DEVICE_IDLE_MODE_CHANGED);
        idlemodechage = new IdleModeChange();
        registerReceiver(idlemodechage, intentFilter);

        notuficationmanger = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        switchCompat = (SwitchCompat)findViewById(R.id.doze_notification_enabled);
        notification_bit =(TextView)findViewById(R.id.notification_bit);

        check();
      //  Notifity();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //白名单
            case R.id.whilt:
                startActivity(doze.whilt());
                break;
            /*case R.id.setting:
                ;
                break;
                */
            case R.id.about:
                Intent intent = new Intent(MainActivity.this,About.class);
                startActivity(intent);
                break;

            default:
                break;
        }
        return true;
    }

    public void doze_notification(View view){
        if(switchCompat.isChecked()){
            Notify_Flag = true;
            notification_bit.setText("ON");
            notification_bit.setTextColor(green);
        }else {
            Notify_Flag = false;
            notification_bit.setText("OFF");
            notification_bit.setTextColor(red);
        }
    }



    public void EnterDoze(View view) {
        doze.EnterDoze();
    }

    public void ExitDoze(View view) {
        doze.ExitDoze();
    }


    public void adb(View view){
        if (editText.getText().toString()== hint) {
            doze.Shell("dumpsys deviceidle force-idle");
        }else {
            doze.Shell(editText.getText().toString());
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(idlemodechage);
    }

    public void check() {
        if (doze.FlagDoze()) {
            Doze_Mode.setText("ON");
            Doze_Mode.setTextColor(Color.GREEN);
        } else if (!(doze.FlagDoze())) {
            Doze_Mode.setText("OFF");
            Doze_Mode.setTextColor(Color.RED);
        }
    }

    class IdleModeChange extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Toast.makeText(context, "IdleMode_change", Toast.LENGTH_SHORT).show();
            check();
            Notifity();
        }
    }

    public void Notifity(){
        if(Notify_Flag){
            if (doze.FlagDoze()){
                notification = new NotificationCompat.Builder(this)
                        .setContentTitle("Doze Mode")
                        .setContentText("已进入 Doze 模式")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .build();
                notuficationmanger.notify(1,notification);
            }else {
                notification = new NotificationCompat.Builder(this)
                        .setContentTitle("Doze Mode")
                        .setContentText("已退出 Doze 模式")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .build();
                notuficationmanger.notify(1,notification);
            }
        }else {
            if(doze.FlagDoze()){
                Toast.makeText(this, "Enter Doze Success", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "Exit Doze Success", Toast.LENGTH_SHORT).show();
            }
        }
    }



}
