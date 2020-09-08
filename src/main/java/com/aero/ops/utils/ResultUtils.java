package com.aero.ops.utils;


import com.aero.common.model.BaseStatus;
import com.aero.common.model.LayUIResultModel;
import com.aero.common.model.ResponseModel;
import com.aero.common.model.LayUIResultModel;
import com.aero.common.model.ResultState;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class ResultUtils<T> {

    public static final ResultUtils me = new ResultUtils();

    /**
     * 成功
     */
    public static ResponseModel<Boolean> ok() {
        BaseStatus result = new BaseStatus(ResultState.SUCCESS);
        ResponseModel<Boolean> model = new ResponseModel<Boolean>();
        model.setStatus(result);
        model.setData(true);
        return model;
    }

    /**
     * 失败
     */
    public static ResponseModel<Boolean> fail() {
        BaseStatus result = new BaseStatus(ResultState.FAIL);
        ResponseModel<Boolean> model = new ResponseModel<Boolean>();
        model.setStatus(result);
        model.setData(false);
        return model;
    }
    
    public ResponseModel<T> success(T data) {
        BaseStatus res = new BaseStatus(ResultState.SUCCESS);
        ResponseModel R = new ResponseModel<>();
        R.setStatus(res);
        R.setData(data);
        return R;
    }

    /**
     * 自定义状态
     * @param resultState 基本状态枚举
     * @return
     */
    public static ResponseModel error(ResultState resultState, ResponseModel r) {
        BaseStatus res = new BaseStatus(resultState);
        r.setStatus(res);
        r.setData(resultState.getMessage());
        return r;
    }
    
    public static ResponseModel error(ResultState resultState, String msg) {
        BaseStatus res = new BaseStatus(resultState, msg);
        ResponseModel R = new ResponseModel<>();
        R.setStatus(res);
        R.setData(null);
        return R;
    }

    
    public ResponseModel<T> error(ResultState resultState, String msg, T t) {
        BaseStatus res = new BaseStatus(resultState, msg);
        ResponseModel R = new ResponseModel<>();
        R.setData(t);
        R.setStatus(res);
        return R;
    }

    
    public static ResponseModel error(ResultState statusCode) {
        BaseStatus res = new BaseStatus(statusCode);
        ResponseModel R = new ResponseModel<>();
        R.setStatus(res);
        R.setData(statusCode.getMessage());
        return R;
    }

    /**
     * 服务器异常
     */
    public static ResponseModel systemError() {
        BaseStatus res = new BaseStatus(ResultState.EXCEPTION);
        ResponseModel R = new ResponseModel<>();
        R.setStatus(res);
        R.setData(null);
        return R;
    }

    public static String ResultLayUIData(Object data,Integer count,String message){
        LayUIResultModel model = new LayUIResultModel();
        model.setCode(0);
        model.setCount(count);
        model.setMessage(message);
        model.setData(data);
        String dateformat = "yyyy-MM-dd HH:mm:ss";
        String json = JSON.toJSONStringWithDateFormat(model,dateformat, SerializerFeature.WriteDateUseDateFormat);
        return json;
    }
}
