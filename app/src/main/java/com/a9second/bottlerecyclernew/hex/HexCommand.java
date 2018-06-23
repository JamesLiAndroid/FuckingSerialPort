package com.a9second.bottlerecyclernew.hex;

/**
 * 所有的命令编码，先编写字母组成的命令，然后转hex
 * @author lsy
 */
public class HexCommand {

    /**
     * 发送协议头
     */
    public static final String SEND_STR_HEADER = "abcde{";

    /**
     * 发送的协议尾
     */
    public static final String SEND_STR_FOOTER = "}abcde\r\n";

    /**
     * 接收协议头
     */
    public static final String RECEIVED_STR_HEADER = "edcba{";

    /**
     * 接收的协议尾
     */
    public static final String RECEIVERD_STR_FOOTER = "}edcba\r\n";

    // -----------------------------------------------------------------
    // 1. 投瓶门相关的指令
    // -----------------------------------------------------------------
    /**
     * 门指令列表
     */
    // 下发开门命令
    public static final String OPEN_DOOR_SEND = "OpenDoor=1;";
    // 单片机应答开门命令
    public static final String OPEN_DOOR_REC = "OpenDoor=1;";
    // 单片机开门完成上报
    public static final String OPENED_DOOR_REC = "Opened=1;";

    public static final String OPEN_DOOR = "OpenDoor";
}
