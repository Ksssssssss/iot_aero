package com.aero.ops.utils;

import com.alibaba.fastjson.JSON;
import com.aero.common.model.PageModel;
import com.aero.common.model.ResponseModel;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class LayTableUtil<T> {
    public static final LayTableUtil me = new LayTableUtil();

    public String convertResult2LayTable(ResponseModel<PageModel<T>> responseModel){
        PageModel<T> pageModel = responseModel.getData();
        LayTableModel model = new LayTableModel();
        model.setCode(0);
        if (pageModel!=null) {
            model.setCount(pageModel.getCount());
        }
        model.setMessage(responseModel.getStatus().getMessage());
        if (pageModel!=null) {
            model.setData(pageModel.getData());
        }
        String dateformat = "yyyy-MM-dd HH:mm:ss";
        String json = JSON.toJSONStringWithDateFormat(model,dateformat, SerializerFeature.WriteDateUseDateFormat);
        return json;
    }

    public class LayTableModel {
        private int count;
        private int code;
        private String message;
        private T data;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }
    }
}
