package com.a9second.bottlerecyclernew;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.a9second.bottlerecyclernew.app.MainApplication;
import com.a9second.bottlerecyclernew.bean.CommandEvent;
import com.a9second.bottlerecyclernew.hex.HexCommand;
import com.a9second.bottlerecyclernew.hex.HexComplete;
import com.whieenz.LogCook;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends AppCompatActivity {

    String barCodeInfo = "";
    Button btnContinueIn;
    boolean isBackBottle = false;

    int temp = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnSend = findViewById(R.id.btn_open_door_send);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 开门操作的命令
                ((MainApplication)getApplication()).comTTYS1.setTxtLoopData(HexComplete
                        .completeStrFinalSentData(HexCommand.OPEN_DOOR_SEND));
                // 开启发送线程，只发送一条信息，转为静止状态
                ((MainApplication)getApplication()).comTTYS1.startSend();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 消息接收并显示的方法
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(CommandEvent commandEvent) {
        Toast.makeText(this, commandEvent.getValue()+"........."+commandEvent.getMessage(), Toast.LENGTH_SHORT).show();
        // 已经开门
       if (HexCommand.OPENED_DOOR.equals(commandEvent.getMessage())) {
           if ("1".equals(commandEvent.getValue())) {
               Toast.makeText(this, "已开门", Toast.LENGTH_SHORT).show();
           }
           LogCook.d("TAG", "OPEN_DOOR....."+commandEvent.isSuccess()+":::"+commandEvent.getMessage()+":::"+commandEvent.getValue());
       }
    }
}
