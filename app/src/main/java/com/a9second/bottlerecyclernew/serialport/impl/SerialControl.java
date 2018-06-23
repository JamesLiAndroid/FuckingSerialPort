package com.a9second.bottlerecyclernew.serialport.impl;

import android.content.Context;

import com.a9second.bottlerecyclernew.bean.ComBean;
import com.a9second.bottlerecyclernew.bean.CommandEvent;
import com.a9second.bottlerecyclernew.serialport.SerialHelper;
import com.a9second.bottlerecyclernew.serialport.SerialHelperUtils;
import com.a9second.bottlerecyclernew.utils.ArrayUtils;
import com.whieenz.LogCook;

import org.greenrobot.eventbus.EventBus;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.Queue;

import static com.a9second.bottlerecyclernew.hex.HexParser.getCommandValue;


/**
 * 串口操作类
 * Created by lsy on 2017/9/15.
 */

public class SerialControl extends SerialHelper {

    private ByteBuffer mByteBuffer = ByteBuffer.wrap(new byte[1024*1000]);
    private int unDisposeLen = 0;
    private int MIN_LEN = 23;
    private int MAX_CONTENT_LENGTH = 15;

    ComBean comBean;

    private static DispQueueThread dispQueue = null;//刷新显示线程
    // 锁信息
    private static final Object objLock = new Object();
    // 是否清除缓冲区所有数据
    boolean isClearAllData = false;

    //		public SerialControl(String sPort, String sBaudRate){
//			super(sPort, sBaudRate);
//		}
    public SerialControl() {

    }

    /**
     * 串口初始化操作
     */
    public static SerialControl initSerialPort(Context activity, SerialControl comTTYS1) {
        LogCook.d("TAG", "串口打开！");
        if (comTTYS1 == null) {
            comTTYS1 = new SerialControl();
            comTTYS1.setBaudRate("9600");
            // TODO：在此打开串口需要修改
            comTTYS1.setPort("/dev/ttymxc1");
            SerialHelperUtils.getInstance().openComPort(activity, comTTYS1);
        }
        LogCook.d("TAG", "串口打开完毕！");
        return comTTYS1;

    }

    /**
     * 串口初始化操作
     */
    public static DispQueueThread initDispQueueThread() {
       // Log.d("TAG", "串口打开！");
        if (dispQueue == null) {
            dispQueue = new DispQueueThread();
            dispQueue.start();
        }
        return dispQueue;
    }

    @Override
    protected void onDataReceived(final ComBean comRecData) {
        LogCook.e("ERROR:", "comRecData::" + comRecData.toString());

        byte[] received = comRecData.bRec;
        int size = received.length;

        mByteBuffer.put(received);

        unDisposeLen += size;
       // int temp = 0; // 数组下标
        if (unDisposeLen < MIN_LEN) {
            LogCook.d("TAG", "数据长度不足！！！！\n"+"unDisposeLen = "+unDisposeLen+"\n");
            return;
        }
        while (true) {
            mByteBuffer.flip();

            if (unDisposeLen >= MIN_LEN) {
                if (mByteBuffer.position() == mByteBuffer.limit()) {
                    LogCook.d("TAG", "未解析到有效数据！！！");
                    break;
                }
                LogCook.d("TAG", "....position::"+mByteBuffer.position()
                        +":::limit:"+mByteBuffer.limit()+"....unDisposeLen::"+unDisposeLen);
                mByteBuffer.mark();
                // 每次读取缓冲区中有效值
                byte[] remaining = new byte[mByteBuffer.remaining()];
                mByteBuffer.get(remaining);

                // 校验数据头
                byte[] head = "report{".getBytes();
                byte[] footer = "}report\r\n".getBytes();

                // 校验数据尾
                int posFooter = ArrayUtils.indexOf(remaining, footer);
                if (posFooter == -1) {
                    LogCook.d("TAG", "未获取到尾部位置！");
                    // 未获取到尾部，继续读取，不要清除数据
                     break;
                }

                // 校验数据头
                int posHead = ArrayUtils.indexOf(remaining, head);
                if (posHead == -1) {
                    LogCook.d("TAG", "未获取到头部位置！");
                    // 找到了尾找不到头，就把尾之前的数据移除
                    unDisposeLen -= posFooter + footer.length - mByteBuffer.position();
                    mByteBuffer.position(posFooter + footer.length);
                    mByteBuffer.compact();
                    continue;
                }

                // d定位到数据头位置
                mByteBuffer.position(posHead);
                if (posFooter > posHead) {
                    if (posFooter - (posHead + head.length) > MAX_CONTENT_LENGTH) {
                        // 过长的数据
                        LogCook.d("TAG", "....................-------------.........");
                        LogCook.w("Error1", "position:::"+mByteBuffer.position()+"\n limit::::"+mByteBuffer.limit());
                        LogCook.d("TAG", "....................-------------.........");
                        LogCook.w("Error2", "posHead:::"+posHead+"\n" + "posFooter:::"+posFooter+"\n");
                        LogCook.w("Error1", "positionNew" + posFooter+footer.length);
                        // 将posFooter之前的全部移除
                        unDisposeLen -= posHead + head.length;
                        mByteBuffer.position(posFooter+footer.length);
                        mByteBuffer.compact();
                        continue;
                    }
                    // 确定将position移动到posHeader位置
                    mByteBuffer.position(posHead);
                    LogCook.w("Error1", "position:::"+mByteBuffer.position()+"\n limit::::"+mByteBuffer.limit());
                    byte[] getContent = new byte[posFooter + footer.length - posHead];
                    mByteBuffer.get(getContent);
                    LogCook.d("TAG", "getContent:"+ new String(getContent) + "................................");
                    // 添加有效数据
                    comBean = new ComBean(comRecData.sComPort, getContent, getContent.length);
                    dispQueue.addQueue(comBean);
                    // 移动当前
                    int currentPosCount = posFooter + footer.length - mByteBuffer.position();
                    mByteBuffer.position(posFooter+footer.length);
                    mByteBuffer.compact();
                    // 变更判断的长度
                    unDisposeLen -= currentPosCount;
                } else {
                    // 将posFooter之前的全部移除
                    int currentPosCount = posFooter + footer.length - mByteBuffer.position();
                    mByteBuffer.position(posFooter+footer.length);
                    mByteBuffer.compact();
                    // 变更判断的长度
                    unDisposeLen -= currentPosCount;
                }
            } else {
                break;
            }
        }
        if (!isClearAllData) {
            mByteBuffer.clear();
            unDisposeLen = 0;
        }

    }

    //----------------------------------------------------刷新显示线程
    private static class DispQueueThread extends Thread {
        private Queue<ComBean> queueList = new LinkedList<ComBean>();

        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                final ComBean comData;
                while ((comData = queueList.poll()) != null) {
                    // 逻辑更改，将数据分类后发送出去到主线程显示
                    LogCook.d("TAG", "接收数据信息："+comData.toString());
                    // 同步，发送信息，防止信息出现断裂的情况
                    synchronized (objLock) {
                        CommandEvent commandEvent = getCommandValue(comData.bRec);
                        EventBus.getDefault().post(commandEvent);
                        LogCook.d("TAG", "接收：" + commandEvent.toString());
                    }

                    try {
                        Thread.sleep(1000);//显示性能高的话，可以把此数值调小。
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }

        public synchronized void addQueue(ComBean comData) {
            queueList.add(comData);
        }
    }
}