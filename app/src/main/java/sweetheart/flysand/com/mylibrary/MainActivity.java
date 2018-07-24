package sweetheart.flysand.com.mylibrary;

import android.os.Bundle;

import com.flysand.mylibrary.crash.CrashHandler;
import com.flysand.mylibrary.http.HttpAnalysisHelper;
import com.flysand.mylibrary.util.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends com.flysand.mylibrary.base.BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CrashHandler.getInstance(getApplicationContext());
        Utils.isApkDebugable(getApplicationContext(), getPackageName());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected HttpAnalysisHelper getCustumAnalysisHelper() {
        return new MyHttpAnalysisHelper(this);
    }

    @Override
    public void onHttpSuccess(String type, JSONObject jsonObject) throws Exception {

    }

    @Override
    public void onHttpSuccess(String type, JSONArray jsonArray, int page, int size, int count) throws Exception {

    }

    @Override
    public void onHttpFailure(String type, String msg) throws Exception {

    }
}
