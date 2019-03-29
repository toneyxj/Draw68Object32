package com.moxi.haierexams.http;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by zhengdelong on 15/12/23.
 */
public class VolleyHttpUtil {

    private static RequestQueue requestQueue = null;

    private static RequestQueue getRequestQueue(Context context) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        return requestQueue;
    }

    public static void get(Context context, String url, final HttpVolleyCallback httpVolleyCallback) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        httpVolleyCallback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                String errorMsg = volleyError.getMessage();
                httpVolleyCallback.onFilad(errorMsg);
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,//默认超时时间，应设置一个稍微大点儿的，例如本处的500000
                5,//默认最大尝试次数
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        getRequestQueue(context).add(stringRequest);
    }

    public static void get(Context context, String url, HashMap param, final HttpVolleyCallback httpVolleyCallback) {
        if (!param.isEmpty() || param != null) {
            StringBuffer sb = new StringBuffer();
            boolean isFirst = true;
            //迭代param
            Iterator iter = param.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                if (isFirst) {
                    isFirst = false;
                    if (url.contains("?")) {
                        sb.append("&" + entry.getKey() + "=" + entry.getValue());
                    } else {
                        sb.append("?" + entry.getKey() + "=" + entry.getValue());
                    }
                } else {
                    sb.append("&" + entry.getKey() + "=" + entry.getValue());
                }
            }
            String params = sb.toString();
            url = url + params;
        }
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        httpVolleyCallback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                String errorMsg = volleyError.getMessage();
                httpVolleyCallback.onFilad(errorMsg);
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,//默认超时时间，应设置一个稍微大点儿的，例如本处的500000
                5,//默认最大尝试次数
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        getRequestQueue(context).add(stringRequest);
    }

    public static void get(Context context, String url, final HashMap headers, HashMap param, final HttpVolleyCallback httpVolleyCallback) {
        if (param != null) {
            if (!param.isEmpty()) {
                StringBuffer sb = new StringBuffer();
                boolean isFirst = true;
                //迭代param
                Iterator iter = param.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    if (isFirst) {
                        isFirst = false;
                        if (url.contains("?")) {
                            sb.append("&" + entry.getKey() + "=" + entry.getValue());
                        } else {
                            sb.append("?" + entry.getKey() + "=" + entry.getValue());
                        }
                    } else {
                        sb.append("&" + entry.getKey() + "=" + entry.getValue());
                    }
                }
                String params = sb.toString();
                url = url + params;
            }
        }

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        httpVolleyCallback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                String errorMsg = volleyError.getMessage();
                httpVolleyCallback.onFilad(errorMsg);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return headers;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,//默认超时时间，应设置一个稍微大点儿的，例如本处的500000
                5,//默认最大尝试次数
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        getRequestQueue(context).add(stringRequest);
    }

    public static void post(Context context, String url, final HashMap param, final HttpVolleyCallback httpVolleyCallback) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        httpVolleyCallback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                String errorMsg = volleyError.getMessage();
                httpVolleyCallback.onFilad(errorMsg);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return param;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,//默认超时时间，应设置一个稍微大点儿的，例如本处的500000
                5,//默认最大尝试次数
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        getRequestQueue(context).add(stringRequest);
    }

}
