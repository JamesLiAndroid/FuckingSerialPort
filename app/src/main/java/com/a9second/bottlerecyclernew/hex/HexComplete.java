package com.a9second.bottlerecyclernew.hex;

import com.whieenz.LogCook;

/**
 * 命令组合的内容
 * Created by XC_LSY on 2017/9/7.
 */
public class HexComplete {
    /**
     * 组合字符串为Hex数据
     * @param command 命令
     * @param data 数据内容
     * @return
     */
    public static String completeFinalSentData(String command, String data) {
        // 首先将data字符串转换为hex
       // String hexData = HexUtils.str2HexStr(data);
        // 获取数据结果
        String result = buildStr(HexCommand.SEND_STR_HEADER, command, "=", data, HexCommand.SEND_STR_FOOTER);
        // 去空格
        result = result.replace(" ", "");
        LogCook.d("Command", "命令信息：" + result);
        // 转换hex信息
        String hexResult = HexUtils.str2HexStr(result);
        LogCook.d("Command", "hex信息：" + hexResult);
        return hexResult;
    }

    /**
     * 组合字符串为String数据发送
     * @param commandAndData 命令+数据内容
     * @return
     */
    public static String completeStrFinalSentData(String commandAndData) {
        // 首先将data字符串转换为hex
        // String hexData = HexUtils.str2HexStr(data);
        LogCook.d("Command", "开始拼接字符串。。。");
        // 获取数据结果
        String result = buildStrWithoutTrim(HexCommand.SEND_STR_HEADER, commandAndData, HexCommand.SEND_STR_FOOTER);
        LogCook.d("Command", "命令信息：" + result + "：命令信息结尾");
        return result;
    }

    /**
     * 组合字符串为Hex数据
     * @param commandAndData 命令+数据内容
     * @return
     */
    public static String completeFinalSentData(String commandAndData) {
        // 首先将data字符串转换为hex
        // String hexData = HexUtils.str2HexStr(data);
        // 获取数据结果
        String result = buildStr(HexCommand.SEND_STR_HEADER, commandAndData, HexCommand.SEND_STR_FOOTER);
        // 去空格
        result = result.replace(" ", "");
        LogCook.d("Command", "命令信息：" + result);
        // 转换hex信息
        String hexResult = HexUtils.str2HexStr(result);
        LogCook.d("Command", "hex信息：" + hexResult);
        return hexResult.replace(" ", "");
    }

    /**
     * 组合字符串的方法,不进行trim
     * @param params 多个字符串数据
     * @return 组合完成的字符串
     */
    public static String buildStrWithoutTrim(String... params) {
        StringBuffer buffer = new StringBuffer();
        for (String param : params) {
            buffer.append(param);
        }
        return buffer.toString();
    }

    /**
     * 组合字符串的方法
     * @param params 多个字符串数据
     * @return 组合完成的字符串
     */
    public static String buildStr(String... params) {
        StringBuffer buffer = new StringBuffer();
        for (String param : params) {
            buffer.append(param);
        }
        return buffer.toString().trim();
    }

}
