package com.example.zhang.onetapdoze;

import android.content.Intent;
import android.graphics.Color;
import android.media.audiofx.BassBoost;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.View;

import java.io.DataOutputStream;
import java.io.OutputStream;

/**
 * Created by ZHANG on 2017/2/12.
 */

public class Doze {

    private PowerManager powerManager;
    private boolean flag=false;

    public Doze(PowerManager powerManager){
        this.powerManager=powerManager;
    }

    //执行adb命令
    public void Shell(String shell){
        try{
            //尝试获取root权限
            Process p = Runtime.getRuntime().exec("su");
            //获取输出流
            OutputStream outputStream = p.getOutputStream();
            DataOutputStream dataOutputStream=new DataOutputStream(outputStream);
            //将命令写入
            dataOutputStream.writeBytes(shell);
            //提交命令
            dataOutputStream.flush();
            //关闭流操作
            dataOutputStream.close();
            outputStream.close();
        }
        catch(Throwable t)
        {
            t.printStackTrace();
        }
    }
    //检查Doze状态
    private boolean checkDoze(){
        if (powerManager.isDeviceIdleMode()){
            flag = true;
        }else if(!(powerManager.isDeviceIdleMode())){
            flag = false;
        }
        return flag;
    }




    public boolean EnterDoze(){
        //执行进入Doze命令
        Shell("dumpsys deviceidle force-idle");
        //不在Doze模式时执行命令
        if (checkDoze()) {
            return true;
        }else {
            return false;
        }

    }

    public boolean ExitDoze(){
        //执行离开Doze命令
        Shell("dumpsys deviceidle step");
        if (!checkDoze()) {
            return true;
        }else {
            return false;
        }
    }

    public boolean FlagDoze(){
        checkDoze();
        return flag;
    }

    public Intent whilt(){
        Intent intent = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
        return intent;
    }

}
