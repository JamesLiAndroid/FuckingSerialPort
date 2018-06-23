package com.a9second.bottlerecyclernew.serialport;

import com.a9second.bottlerecyclernew.bean.ComBean;
import com.a9second.bottlerecyclernew.utils.DataUtils;
import com.a9second.bottlerecyclernew.utils.DelayUtils;
import com.whieenz.LogCook;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import android_serialport_api.SerialPort;

/**
 * @author lisongyang
 *         串口辅助抽象类
 *         Created by lsy on 2017/9/5.
 */

public abstract class SerialHelper {
    private SerialPort mSerialPort;
    private OutputStream mOutputStream;
    private InputStream mInputStream;
    private ReadThread mReadThread;
    private SendThread mSendThread;
    private String sPort = "/dev/s3c2410_serial0";
    private int iBaudRate = 9600;
    private boolean _isOpen = false;
    private byte[] _bLoopData = new byte[]{0x0D, 0x0A};

    // 消息队列
    private Queue<byte[]> msgQueue = new LinkedList<byte[]>();

    private byte[] heartBeat = new byte[]{0x0D, 0x0A};
    private int iDelay = 750;

    // 消息计数
    public static int msgCount = 0;
    private static ByteBuffer mByteBuffer = ByteBuffer.wrap(new byte[1024*10]);
    private int MIN_LEN = 23;
    private int MAX_CONTENT_LENGTH = 15;

    //----------------------------------------------------
    public SerialHelper(String sPort, int iBaudRate) {
        this.sPort = sPort;
        this.iBaudRate = iBaudRate;
    }

    public SerialHelper() {
        this("/dev/s3c2410_serial0", 9600);
    }

    public SerialHelper(String sPort) {
        this(sPort, 9600);
    }

    public SerialHelper(String sPort, String sBaudRate) {
        this(sPort, Integer.parseInt(sBaudRate));
    }

    //----------------------------------------------------
    public void open() throws SecurityException, IOException, InvalidParameterException {
        mSerialPort = new SerialPort(new File(sPort), iBaudRate, 0);
        mOutputStream = mSerialPort.getOutputStream();
        mInputStream = mSerialPort.getInputStream();
        mReadThread = new ReadThread();
        mReadThread.start();
        mSendThread = new SendThread();
        mSendThread.setSuspendFlag();
        mSendThread.start();
        _isOpen = true;
    }

    //----------------------------------------------------
    public void close() {
        if (mReadThread != null)
            mReadThread.interrupt();
        if (mSerialPort != null) {
            mSerialPort.close();
            mSerialPort = null;
        }
        _isOpen = false;
    }

    //----------------------------------------------------
    // 发送方法同步
    public synchronized void send(byte[] bOutArray) {
        try {
            LogCook.d("::::SEND::::", "BBBBB::"+ Arrays.toString(bOutArray));
            mOutputStream.write(bOutArray);
        } catch (IOException e) {
            e.printStackTrace();
            LogCook.d("TAG", "发送出现异常:" + e.getMessage());
        }
    }

    //----------------------------------------------------
    public void sendHex(String sHex) {
        byte[] bOutArray = DataUtils.HexToByteArr(sHex);
        send(bOutArray);
    }

    //----------------------------------------------------
    public void sendTxt(String sTxt) {
        byte[] bOutArray = sTxt.getBytes();
        send(bOutArray);
    }

    private class ReadThread extends Thread {
        @Override
        public void run() {
            byte[] received = new byte[512];
           //  ByteBuffer bb = ByteBuffer.allocate(4096);
            int size;
            LogCook.e("TAG", "开始读线程");
            while (true) {
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }
                try {
                    // 延时2s再读取
                    DelayUtils.delay2500();
                    int available = mInputStream.available();

                    if (available > 0) {
                        size = mInputStream.read(received);
                        if (size < 0) {
                            break;
                        }
                        LogCook.d("TAG", "...available:::" + available + "\n size = " + size + "\n received:::" + received.length);
                        LogCook.d("TAG...Content", "received:::" + Arrays.toString(received)
                                + "\nContent:::" + new String(received, "ASCII") + "\n");

                        ComBean ComRecData = new ComBean(sPort, received, size);
                        onDataReceived(ComRecData);

                        // 重置数组！
                        received = new byte[512];
                    }
                } catch (IOException e) {
                    LogCook.e("TAG", "读取数据失败" + e.getMessage());
                }
            }
        }
    }

    //----------------------------------------------------
    private class SendThread extends Thread {
        public boolean suspendFlag = true;// 控制线程的执行

        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                synchronized (this) {
                    while (suspendFlag) {
                        try {
                            wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                // 从队列中取信息并发送
                LogCook.d("TAG","结束等待，准备发送数据。。。。。。");
                byte[] msgTmp = getbLoopData();
                if (msgTmp != null) {
                    String content = new String(msgTmp);
                    sendTxt(content);
                    // send(msgTmp);
                    ++msgCount;

                    LogCook.d("CommandCount", "第几次发送数据：" + msgCount + "\n 发送内容:" + content + "数据结尾");
                    LogCook.d("TAG", "发送完成！");
                }

                try {
                    Thread.sleep(iDelay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // 发送一条完成后转为静止状态
                setSuspendFlag();
            }
        }

        //线程暂停
        public void setSuspendFlag() {
            this.suspendFlag = true;
        }

        //唤醒线程
        public synchronized void setResume() {
            this.suspendFlag = false;
            notify();
        }
    }

    //----------------------------------------------------
    public int getBaudRate() {
        return iBaudRate;
    }

    public boolean setBaudRate(int iBaud) {
        if (_isOpen) {
            return false;
        } else {
            iBaudRate = iBaud;
            return true;
        }
    }

    public boolean setBaudRate(String sBaud) {
        int iBaud = Integer.parseInt(sBaud);
        return setBaudRate(iBaud);
    }

    //----------------------------------------------------
    public String getPort() {
        return sPort;
    }

    public boolean setPort(String sPort) {
        if (_isOpen) {
            return false;
        } else {
            this.sPort = sPort;
            return true;
        }
    }

    //----------------------------------------------------
    public boolean isOpen() {
        return _isOpen;
    }

    //----------------------------------------------------
    public byte[] getbLoopData() {
        // 获取队列数据
        _bLoopData = msgQueue.poll();
//        if (_bLoopData == null) {
//            // 序列为空,启用心跳
//            _bLoopData = heartBeat;
//        }
        return _bLoopData;
    }

    //----------------------------------------------------
    public void setbLoopData(byte[] bLoopData) {
        // 添加队列数据
        msgQueue.offer(bLoopData);
        //this._bLoopData = bLoopData;
    }

    //----------------------------------------------------
    public void setTxtLoopData(String sTxt) {
        msgQueue.offer(sTxt.getBytes());
        // this._bLoopData = sTxt.getBytes();
    }

    //----------------------------------------------------
    // 添加到队列
    // ---------------------------------------------------
    public void setHexLoopData(String sHex) {
        msgQueue.offer(DataUtils.HexToByteArr(sHex));
    }

    //----------------------------------------------------
    public int getiDelay() {
        return iDelay;
    }

    //----------------------------------------------------
    public void setiDelay(int iDelay) {
        this.iDelay = iDelay;
    }

    //----------------------------------------------------
    // 开启发送线程
    public void startSend() {
        if (mSendThread != null) {
            mSendThread.setResume();
        }
    }

    //----------------------------------------------------
    // 终止发送线程
    public void stopSend() {
        if (mSendThread != null) {
            mSendThread.setSuspendFlag();
        }
    }

    //----------------------------------------------------
    protected abstract void onDataReceived(ComBean ComRecData);
}
