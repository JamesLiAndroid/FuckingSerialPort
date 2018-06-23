package com.a9second.bottlerecyclernew.bean;

import java.text.SimpleDateFormat;
import java.util.Arrays;

/**
 * Created by lsy on 2017/9/5.
 */

public class ComBean {
    public byte[] bRec=null;
    public String sRecTime="";
    public String sComPort="";
    // 需要更改数据结构
    public ComBean(String sPort, byte[] buffer, int size){
        sComPort=sPort;
        bRec=new byte[size];
        for (int i = 0; i < size; i++)
        {
            bRec[i]=buffer[i];
        }
        SimpleDateFormat sDateFormat = new SimpleDateFormat("hh:mm:ss");
        sRecTime = sDateFormat.format(new java.util.Date());
    }

    @Override
    public String toString() {
        return "ComBean{" +
                "bRec=" + Arrays.toString(bRec) +
                ", sRecTime='" + sRecTime + '\'' +
                ", sComPort='" + sComPort + '\'' +
                '}';
    }
}
