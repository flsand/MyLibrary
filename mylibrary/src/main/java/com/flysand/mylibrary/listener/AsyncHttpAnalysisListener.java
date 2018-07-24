package com.flysand.mylibrary.listener;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by FlySand on 2017\7\24 0024.
 */


public interface AsyncHttpAnalysisListener {

    void onHttpSuccess(String type, JSONObject jsonObject) throws Exception;

    void onHttpSuccess(String type, JSONArray jsonArray, int page, int size, int count) throws Exception;

    void onHttpFailure(String type, String msg) throws Exception;
}