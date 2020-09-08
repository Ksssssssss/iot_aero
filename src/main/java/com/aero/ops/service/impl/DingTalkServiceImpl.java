package com.aero.ops.service.impl;

import com.aero.ops.config.DingTalkConfig;
import com.aero.ops.service.IDingTalkService;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiGettokenRequest;
import com.dingtalk.api.request.OapiSnsGetuserinfoBycodeRequest;
import com.dingtalk.api.response.OapiGettokenResponse;
import com.dingtalk.api.response.OapiSnsGetuserinfoBycodeResponse;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static com.aero.ops.constants.DingTalkConst.dingtalkUserInfoUrl;

/**
 * @author 罗涛
 * @title DingTalkServiceImpl
 * @date 2020/7/10 13:55
 */
@Slf4j
@Service
public class DingTalkServiceImpl implements IDingTalkService {
    @Autowired
    DingTalkConfig dingTalkConfig;

    @Override
    public String getAccessToken() {
        try {
            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/gettoken");
            OapiGettokenRequest req = new OapiGettokenRequest();
            req.setAppkey(dingTalkConfig.getAppid());
            req.setAppsecret(dingTalkConfig.getAppSecret());
            req.setHttpMethod("GET");
            OapiGettokenResponse rsp = client.execute(req);
            return rsp.getBody();
        } catch (ApiException e) {
            e.printStackTrace();
        }
        return null;
    }



    @Override
    public String getUserInfo(String code) throws Exception {
        try {
            DingTalkClient client = new DefaultDingTalkClient(dingtalkUserInfoUrl);
            OapiSnsGetuserinfoBycodeRequest  req = new OapiSnsGetuserinfoBycodeRequest ();
            req.setTmpAuthCode(code);
            OapiSnsGetuserinfoBycodeResponse  rsp = client.execute(req, dingTalkConfig.getAppid(), dingTalkConfig.getAppSecret());
            return rsp.getBody();

//            long timestamp = System.currentTimeMillis();
//            String urlEncodeSignature = buildSignature(String.valueOf(timestamp));
//            String url = "https://oapi.dingtalk.com/sns/getuserinfo_bycode?accessKey="+appid+"&timestamp="+timestamp+"&signature="+urlEncodeSignature;
//            Map<String, String> signMap = new HashMap<>();
//            signMap.put("tmp_auth_code",code);
//            String json = JSON.toJSONString(signMap);
//            String resp = OkHttpUtil.doPost(url, json);
//            return resp;
        } catch (ApiException e) {
            e.printStackTrace();
            log.error("获取用户信息发生异常：{0}", e);
        }
        return null;
    }



    public String getUninonId(String code, long timestamp) throws Exception {
        String urlEncodeSignature = buildSignature(String.valueOf(timestamp));
        //获取unionId
        String url = dingtalkUserInfoUrl+"?accessKey="+ dingTalkConfig.getAppid() +"&timestamp="+timestamp+"&signature="+urlEncodeSignature;
        DefaultDingTalkClient client = new DefaultDingTalkClient(url);
        OapiSnsGetuserinfoBycodeRequest req = new OapiSnsGetuserinfoBycodeRequest();
        req.setTmpAuthCode(code);
        OapiSnsGetuserinfoBycodeResponse response = client.execute(req,dingTalkConfig.getAppid(),dingTalkConfig.getAppSecret());
        OapiSnsGetuserinfoBycodeResponse.UserInfo userInfo = response.getUserInfo();
        String unionId = userInfo.getUnionid();
        return unionId;
    }


    public String buildSignature(String timestamp) throws Exception {
        String stringToSign = timestamp;
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(dingTalkConfig.getAppSecret().getBytes("UTF-8"), "HmacSHA256"));
        byte[] signatureBytes = mac.doFinal(stringToSign.getBytes("UTF-8"));
        String signature = new String(Base64.encodeBase64(signatureBytes));
        String urlEncodeSignature = urlEncode(signature,"utf-8");
        return urlEncodeSignature;
    }

    // encoding参数使用utf-8
    public static String urlEncode(String value, String encoding) {
        if (value == null) {
            return "";
        }
        try {
            String encoded = URLEncoder.encode(value, encoding);
            return encoded.replace("+", "%20").replace("*", "%2A")
                    .replace("~", "%7E").replace("/", "%2F");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("FailedToEncodeUri", e);
        }
    }


}
