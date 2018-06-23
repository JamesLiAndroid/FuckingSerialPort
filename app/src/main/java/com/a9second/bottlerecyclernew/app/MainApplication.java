package com.a9second.bottlerecyclernew.app;

import android.app.Application;
import android.os.Environment;

import com.a9second.bottlerecyclernew.serialport.impl.SerialControl;
import com.whieenz.LogCook;


/**
 * Created by lsy on 2017/9/15.
 */

public class MainApplication extends Application {

    public SerialControl comTTYS1; // 单片机

    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化日志信息
        String logPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/com.9second.bottlerecyclenew/log";
        LogCook.getInstance() // 单例获取LogCook实例
                .setLogPath(logPath) //设置日志保存路径
                .setLogName("test.log") //设置日志文件名
                .isOpen(true)  //是否开启输出日志
                .isSave(true)  //是否保存日志
                .initialize(); //完成初始化Crash监听

        // 初始化单片机串口
        comTTYS1 = SerialControl.initSerialPort(this, comTTYS1);
        // 起始处关闭发送线程
        comTTYS1.stopSend();
        // 设置延时信息
        comTTYS1.setiDelay(100);
        // 初始化消息接收綫程
        SerialControl.initDispQueueThread();
    }
}
