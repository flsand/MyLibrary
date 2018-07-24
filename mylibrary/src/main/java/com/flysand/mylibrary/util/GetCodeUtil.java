package com.flysand.mylibrary.util;

import android.content.Context;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by FlySand on 2017/10/10.
 */

public class GetCodeUtil {

    private MyCountDownTimer countDownTimer;
    EditText editText;
    Button getCodeBtn;
    Context context;

    // 验证码时间
    private final int MAX_OBTAIN_TIME = 60000;

    public GetCodeUtil(Context context,EditText editText, Button getCodeBtn) {
        this.editText = editText;
        this.getCodeBtn = getCodeBtn;
        this.context = context;
        initCodeBtnText();
    }

    private void initCodeBtnText() {
        long obtainTime = Utils.getSaveLongData(context, "obtainCodeTime", 0l);
        long redus = obtainTime - System.currentTimeMillis();
        if (redus > 0) {
            // 距离上次获取验证码不足指定的时间
            startCountDownTime(redus);
        }
    }

    public void startCountDownTime(long time) {
        if (time > 0) {
            getCodeBtn.setEnabled(false);
            Utils.saveData(context, "obtainCodeTime", System.currentTimeMillis() + time);
            countDownTimer = new MyCountDownTimer(time, 1000);
            countDownTimer.start();
        }
    }

    public void startCountDownTime() {
            getCodeBtn.setEnabled(false);
            Utils.saveData(context, "obtainCodeTime", System.currentTimeMillis() + MAX_OBTAIN_TIME);
            countDownTimer = new MyCountDownTimer(MAX_OBTAIN_TIME, 1000);
            countDownTimer.start();
    }

    private class MyCountDownTimer extends CountDownTimer {

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            getCodeBtn.setText("重新获取");
            getCodeBtn.setEnabled(true);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            getCodeBtn.setText(millisUntilFinished / 1000 + "s");
        }
    }

}
