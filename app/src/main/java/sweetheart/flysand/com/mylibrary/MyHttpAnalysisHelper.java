package sweetheart.flysand.com.mylibrary;

import com.flysand.mylibrary.http.HttpAnalysisHelper;
import com.flysand.mylibrary.listener.AsyncHttpAnalysisListener;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by FlySand on 2018/4/11.
 */

public class MyHttpAnalysisHelper extends HttpAnalysisHelper {
    public MyHttpAnalysisHelper(AsyncHttpAnalysisListener listener) {
        super(listener);
    }

    @Override
    public void onFailure(String type, Call call, Exception e) {
        if (listener != null){
            try {
                listener.onHttpFailure(type,"");
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    @Override
    public void onResponse(String type, Call call, Response response, String body) {
        if (listener != null){
            try {
                listener.onHttpSuccess(type,null);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

}
