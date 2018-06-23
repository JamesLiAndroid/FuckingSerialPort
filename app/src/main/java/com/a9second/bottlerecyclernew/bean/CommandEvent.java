package com.a9second.bottlerecyclernew.bean;

/**
 * EventBus事件相关的bean类
 * Created by lsy on 2017/9/15.
 *
 */

public class CommandEvent {
    private String message; // 命令信息，必须有的

    private String value; // 值信息

    private boolean isSuccess = false; // 是否成功

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "CommandEvent{" +
                "message='" + message + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
