package com.flysand.mylibrary.listener;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by FlySand on 2017\7\24 0024.
 */


public interface AsyncHttpReturnListener {
    /**
     * Called when the request could not be executed due to cancellation, a connectivity problem or
     * timeout. Because networks can fail during an exchange, it is possible that the remote server
     * accepted the request before the failure.
     */
    void onFailure(String type, Call call, Exception e);

    /**
     * Called when the HTTP response was successfully returned by the remote server. The callback may
     * proceed to read the response body with {@link Response#body}. The response is still live until
     * its response body is {@linkplain ResponseBody closed}. The recipient of the callback may
     * consume the response body on another thread.
     * <p>
     * <p>Note that transport-layer success (receiving a HTTP response code, headers and body) does
     * not necessarily indicate application-layer success: {@code response} may still indicate an
     * unhappy HTTP response code like 404 or 500.
     */
    void onResponse(String type, Call call, Response response,String body) throws Exception;

}