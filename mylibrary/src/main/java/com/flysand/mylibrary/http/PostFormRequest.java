package com.flysand.mylibrary.http;

import java.io.UnsupportedEncodingException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by zhy on 15/12/14.
 */
public class PostFormRequest
{
    private List<RequestParams.FileInput> files;
    private ConcurrentHashMap<String, String> params;

    public PostFormRequest(RequestParams params)
    {
        this.files = params.getFiles();
        this.params = params.getParams();

    }

    protected RequestBody buildRequestBody()
    {
        if (files == null || files.isEmpty())
        {
            FormBody.Builder builder = new FormBody.Builder();
            addParams(builder);
            FormBody formBody = builder.build();
            return formBody;
        } else
        {
            MultipartBody.Builder builder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM);
            addParams(builder);

            for (int i = 0; i < files.size(); i++)
            {
                RequestParams.FileInput fileInput = files.get(i);
                RequestBody fileBody = RequestBody.create(MediaType.parse(guessMimeType(fileInput.filename)), fileInput.file);
                builder.addFormDataPart(fileInput.key, fileInput.filename, fileBody);
            }
            return builder.build();
        }
    }

//    protected RequestBody wrapRequestBody(RequestBody requestBody, final Callback callback)
//    {
//        if (callback == null) return requestBody;
//        CountingRequestBody countingRequestBody = new CountingRequestBody(requestBody, new CountingRequestBody.Listener()
//        {
//            @Override
//            public void onRequestProgress(final long bytesWritten, final long contentLength)
//            {
//
//                OkHttpUtils.getInstance().getDelivery().execute(new Runnable()
//                {
//                    @Override
//                    public void run()
//                    {
//                        callback.inProgress(bytesWritten * 1.0f / contentLength,contentLength,id);
//                    }
//                });
//
//            }
//        });
//        return countingRequestBody;
//    }


    private String guessMimeType(String path)
    {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = null;
        try
        {
            contentTypeFor = fileNameMap.getContentTypeFor(URLEncoder.encode(path, "UTF-8"));
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        if (contentTypeFor == null)
        {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }
    private void addParams(MultipartBody.Builder builder)
    {
        if (params != null && !params.isEmpty())
        {
            for (String key : params.keySet())
            {
                builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + key + "\""),
                        RequestBody.create(null, params.get(key)));
            }
        }
    }

    private void addParams(FormBody.Builder builder)
    {
        if (params != null)
        {
            for (String key : params.keySet())
            {
                builder.add(key, params.get(key));
            }
        }
    }

}