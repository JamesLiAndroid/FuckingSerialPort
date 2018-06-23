package com.a9second.bottlerecyclernew.serialport;

import android.content.Context;
import android.widget.Toast;

import com.whieenz.LogCook;

import java.io.IOException;
import java.security.InvalidParameterException;

/**
 * Created by lsy on 2017/9/12.
 */

public class SerialHelperUtils {
    private static class SerialHelperImplHolder {
        private static final SerialHelperUtils INSTANCE = new SerialHelperUtils();
    }
    private SerialHelperUtils(){}

    public static final SerialHelperUtils getInstance() {
        return SerialHelperImplHolder.INSTANCE;
    }

    //----------------------------------------------------关闭串口
    public void closeComPort(SerialHelper comPort) {
        if (comPort != null) {
            comPort.stopSend();
            comPort.close();
        }
    }

    //----------------------------------------------------打开串口
    public void openComPort(Context context, SerialHelper comPort) {
        try {
            comPort.open();
        } catch (SecurityException e) {
            LogCook.e("context", "打开串口失败:没有串口读/写权限!");
            showMessage(context, "打开串口失败:没有串口读/写权限!");
        } catch (IOException e) {
            LogCook.e("context", "打开串口失败:未知错误!");
            showMessage(context, "打开串口失败:未知错误!");
        } catch (InvalidParameterException e) {
            LogCook.e("context", "打开串口失败:参数错误!");
            showMessage(context, "打开串口失败:参数错误!");
        }
    }

    //----------------------------------------------------串口发送
    public void sendPortData(Context context, SerialHelper comPort, String sOut) {
        if (comPort != null && comPort.isOpen()) {
            comPort.sendHex(sOut);
        } else {
            LogCook.d("TAG", "ComPort off!");
            showMessage(context, "请先打开串口！");
            // Toast.makeText(context, "请先打开端口！", Toast.LENGTH_LONG).show();
        }
    }

    //------------------------------------------显示消息
    private void showMessage(Context context, String sMsg) {
        Toast.makeText(context, sMsg, Toast.LENGTH_SHORT).show();
    }
}
