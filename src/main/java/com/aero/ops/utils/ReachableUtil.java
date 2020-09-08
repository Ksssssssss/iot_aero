package com.aero.ops.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.*;

/**
 * @author 罗涛
 * @title ReachableUtil
 * @date 2020/8/12 16:03
 */
public class ReachableUtil {
    public static boolean checkUrl(String httpUrl){
        return checkUrlWithTimeOut(httpUrl,2000);
    }

    //http url monitor
    public static boolean checkUrlWithTimeOut(String httpUrl, Integer timeOutMillSeconds){
        boolean flag = false;
        URL url;
        try {
            if(!StringUtils.startsWith(httpUrl,"http")){
                httpUrl = StringUtils.join("http://", httpUrl);
            }
            url = new URL(httpUrl);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            /**
             * public int getResponseCode()throws IOException
             * 从 HTTP 响应消息获取状态码。
             * 例如，就以下状态行来说：
             * HTTP/1.0 200 OK
             * HTTP/1.0 401 Unauthorized
             * 将分别返回 200 和 401。
             * 如果无法从响应中识别任何代码（即响应不是有效的 HTTP），则返回 -1。
             *
             * 返回 HTTP 状态码或 -1
             */
            conn.setConnectTimeout(timeOutMillSeconds);
            int state = conn.getResponseCode();
            if(state == 200){
                conn.disconnect();
                flag = true;
            }
        } catch (Exception e) {
//            log.error("健康状态检查异常：链接打不开,URL = {}, e = {}", httpUrl, e.getMessage());
            e.printStackTrace();
        }
        return flag;
    }


    //执行ping命令
    public static boolean ping(String ipAddress) throws Exception{
        //超时应该在3钞以上
        int mills =  3000 ;
        return pingWithTimeOut(ipAddress,mills);
    }


    public static boolean pingWithTimeOut(String ipAddress, int mills) throws Exception{
        Socket s = new Socket();
        String[] split = StringUtils.split(ipAddress, ":");
        if(split.length<2){
            InetAddress inetAddress = InetAddress.getByName(ipAddress);
            return inetAddress.isReachable(mills);
        }else if(split.length==2) {
            String host = split[0];
            int port = Integer.valueOf(split[1]);
            SocketAddress addr = new InetSocketAddress(host, port);
            try {
                s.connect(addr, mills);
                return true;
            } catch (IOException e) {
//            log.error("健康状态检查异常：地址ping不通, URL = {},e = {}", ipAddress, e.getMessage());
                return false;
            } finally {
                try {
                    s.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }else {
            throw new Exception("ip地址格式不对！");
        }
    }


//    public static void main(String[] args) throws Exception {
//        String url = "localhost:9200";
//        String url = "localhost:9092";
//        int time = 3000;
//        boolean flag = checkUrlWithTimeOut(url,time);
//        System.out.println(flag);

//        boolean flag = ping(url);
//        System.out.println(flag);

//        List<String> zkClusterUrls = Arrays.asList("127.0.0.1:2181", "192.168.1.151:2181", "192.168.1.152:2181", "192.168.1.153:2181", "192.168.1.154:2181");
//        for (int i = 0; i < zkClusterUrls.size(); i++) {
//            String url = zkClusterUrls.get(i);
//            boolean ping = ping(url);
//            if(!ping){
//                System.out.println("url:" + url + "无法访问,请检查！");
//            }else {
//                System.out.println("url:" + url + "连接正常！");
//            }
//        }
//    }
}
