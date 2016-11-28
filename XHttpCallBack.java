package com.huangxy.XhttpUtils;

import com.google.gson.Gson;
import com.google.gson.internal.$Gson$Types;
import com.zhy.http.okhttp.callback.Callback;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Git@Smark on 2016/7/17.
 * 简化接口，支持返回String、Entity、List<Entity>
 */
public abstract class XHttpCallBack<T> extends Callback<T> {

    @Override
    public T parseNetworkResponse(Response response, int id) throws Exception {
        String result = response.body().string();
        try {
            result = unBunding(result);
        }catch (Exception e){
            onParser(e, id);
            return null;
        }
        if (getClass().getGenericSuperclass() == XHttpCallBack.class){
            return (T)result;// 默认返回String
        }
        if(((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0] == String.class){
            return (T)result;// 返回String类型
        }
        // 这句重点，同时支持T、List<T>等等
        Type type = $Gson$Types.canonicalize(((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
        try {
            return new Gson().fromJson(result, type);
        }catch (Exception e){
            onParser(e, id);
        }
        return null;
    }

    @Override
    public void onResponse(T response, int id) {
        //if (response != null)
        onSuccess(response, id);
    }

    @Override
    public void onError(Call call, Exception e, int id) {
        System.out.println("网络请求错误，统一处理"+e);
    }

    public void onParser(Exception e, int id){
        System.out.println("数据解析错误，统一处理"+e);
    }

    public String unBunding(String json) throws JSONException {
        // 很多情况下返回的json是这种格式的, 统一处理
        // 这里只适用于处理获取一个返回Json串，如果要同时获取多个data1、data2...，建议直接Entity返回
//        {
//            "code": 0,
//            "msg": "上传成功",
//            "data": [
//                  {
//                      "id": 1,
//                      "name": "huangxy"
//                  }
//            ]
//        }
//        //JSONArray array = new JSONArray(json);
//        JSONObject obj = new JSONObject(json);
//        return obj.getString("data");
        return json;
    }

    public abstract void onSuccess(T result, int id);
    
}
