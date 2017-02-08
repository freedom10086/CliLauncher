package me.yluo.clilauncher.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.TreeMap;


public class HttpUtils {

    private static HttpURLConnection  build(String url,String method) throws Exception{
        URL resourceUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) resourceUrl.openConnection();
        // Settings
        connection.setConnectTimeout(1000);
        connection.setReadTimeout(1000);
        connection.setUseCaches(false);
        connection.setInstanceFollowRedirects(false);
        connection.setRequestMethod(method);
        connection.setDoInput(true);
        return connection;
    }

    public static String get(String url) throws Exception{
        // Send content as form-urlencoded
        HttpURLConnection connection = build(url,"GET");
        int responseCode = connection.getResponseCode();
        int len = connection.getContentLength();
        if (responseCode >= 200 && responseCode < 303) {
            InputStream in = connection.getInputStream();
            String s = readFrom(in,len);

            in.close();
            connection.disconnect();
            return s;
        }

        return null;
    }


    public static String post(String url,Map<String,String> params) throws Exception{
        // Send content as form-urlencoded
        HttpURLConnection connection = build(url,"POST");
        // Send content as form-urlencoded
        byte[] content = encodeParameters(params);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
        connection.setRequestProperty("Content-Length", Long.toString(content.length));
        connection.setFixedLengthStreamingMode(content.length);
        OutputStream os = connection.getOutputStream();
        os.write(content);
        os.flush();
        os.close();

        int responseCode = connection.getResponseCode();
        int contentLength = connection.getContentLength();
        if (responseCode >= 200 && responseCode < 303) {
            return readFrom(connection.getInputStream(),contentLength);
        }
        return null;
    }


    private static String readFrom(InputStream inputStream, int length) throws IOException {
        if (inputStream == null) {
            return "";
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer, 0, buffer.length)) != -1) {
            os.write(buffer, 0, bytesRead);
        }
        os.flush();
        os.close();
        return os.toString();
    }


    private static byte[] encodeParameters(Map<String, String> map) {
        if (map == null) {
            map = new TreeMap<>();
        }
        StringBuilder encodedParams = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                if (encodedParams.length() > 0) {
                    encodedParams.append("&");
                }
                encodedParams.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                encodedParams.append('=');
                String v = entry.getValue() == null ? "" : entry.getValue();
                encodedParams.append(URLEncoder.encode(v, "UTF-8"));
            }
            return encodedParams.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Encoding not supported: UTF-8", e);
        }
    }

}
