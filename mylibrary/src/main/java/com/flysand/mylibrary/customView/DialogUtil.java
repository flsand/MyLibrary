package com.flysand.mylibrary.customView;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.flysand.mylibrary.R;


/**
 * Created by Administrator on 2017/3/31.
 */

public class DialogUtil {

    public static final int CONTENT_TYPE = 0X01;
    public static final int DOUBLE_TYPE = 0X02;

    private View layout;
    private Button confirm;
    private Button cancel;
    private TextView content;

    private Context context;

    private AlertDialog dialog;

    public DialogUtil(Context context) {
        this.context = context;
        layout = LayoutInflater.from(context).inflate(R.layout.dialog_layout, null);// 得到加载view
//        title = (TextView) layout.findViewById(R.id.dialog_title_tv);
        content = (TextView) layout.findViewById(R.id.dialog_content_tv);
//        im = (ImageView) layout.findViewById(R.id.dialog_im);
        confirm = (Button) layout.findViewById(R.id.dialog_confim_btn);
        cancel = (Button) layout.findViewById(R.id.dialog_cancel_btn);
    }


    /**
     * @param type  类型
     * @param ratio 比例
     * @return
     */
    public AlertDialog showDialog(int type, float ratio) {
        if (ratio < 0.5) {
            ratio = 0.5f;
        }
        dialog = new AlertDialog.Builder(context, R.style.AlertDialog).create();
        dialog.setCancelable(false);
        dialog.show();

        Window window = dialog.getWindow();
        window.setContentView(layout);
        // 设置窗口的大小
//        window.setLayout((int) (context.getResources().getDisplayMetrics().widthPixels * ratio), dialog.getWindow().getAttributes().height);

        ImageView devider = (ImageView) layout.findViewById(R.id.dialog_devider_im);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    confirmClick(v);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    cancelClick(v);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        });

        switch (type) {
            case CONTENT_TYPE:
                cancel.setVisibility(View.GONE);
                devider.setVisibility(View.GONE);
                break;
            case DOUBLE_TYPE:
//                content.setVisibility(View.GONE);
                break;
            case CONTENT_TYPE | DOUBLE_TYPE:
                break;
            default:
                cancel.setVisibility(View.GONE);
                devider.setVisibility(View.GONE);
                confirm.setBackgroundResource(R.drawable.btn_click_state);
        }
        return dialog;
    }

    public void confirmClick(View v) {

    }

    public void cancelClick(View v) {

    }
    public void dismiss(){
        dialog.dismiss();
    }

    public DialogUtil setContent(String content) {
        this.content.setText(content);
        return this;
    }


    public DialogUtil setConfirmBtnText(String text) {
        this.confirm.setText(text);
        return this;
    }
}
