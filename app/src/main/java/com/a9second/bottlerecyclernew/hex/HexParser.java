package com.a9second.bottlerecyclernew.hex;

import com.a9second.bottlerecyclernew.bean.CommandEvent;
import com.whieenz.LogCook;


/**
 * 命令数据解析类
 * Created by XC_LSY on 2017/9/7.
 */
public class HexParser {
    // 完成消息计数
    public static int msgFinishedCount = 0;

    /**
     * 数据解析，进行拼包
     * @param result 返回的命令结果
     * @return 解析后的键值对，key对应命令，value对应全新的值
     */
    public static synchronized CommandEvent getCommandValue(byte[] result) {
        CommandEvent ce = new CommandEvent();
        String tmpCommand = "";
        String tmpValue = "";
        // 对消息分类
        String resultStr = HexUtils.byteArrayToStr(result);
        LogCook.d("TAG", "转换前的消息："+resultStr + "::" + resultStr.length());
        // 去除空格
        resultStr = resultStr.replace(" ", "");
        LogCook.d("Command", "转换后的消息："+resultStr+"::"+resultStr.length());
        // 判断消息类型
        switch (resultStr) {
            // OPEN_DOOR：
            case HexCommand.RECEIVED_STR_HEADER + HexCommand.OPEN_DOOR_REC + HexCommand.RECEIVERD_STR_FOOTER:
                tmpCommand = HexCommand.OPEN_DOOR;
                tmpValue = "1";
                break;
        }
        // 拼接信息
        ce.setMessage(tmpCommand);
        ce.setValue(tmpValue);

        return ce;
    }

    /**
     * 抛出异常信息，针对命令收尾信息进行验证
     */
    public static class InvalidHeaderOrFooterException extends Exception {
        public InvalidHeaderOrFooterException(String message) {
            super(message);
        }
    }
}
