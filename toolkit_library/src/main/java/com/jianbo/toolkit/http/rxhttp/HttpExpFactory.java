package com.jianbo.toolkit.http.rxhttp;

import android.net.ParseException;
import android.text.TextUtils;

import com.google.gson.JsonParseException;
import com.jianbo.toolkit.http.execption.HttpThrowable;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;

import java.net.ConnectException;
import java.net.UnknownHostException;

import javax.net.ssl.SSLPeerUnverifiedException;

import retrofit2.HttpException;


/**
 * Created by Jianbo on 2018/4/11.
 */

public class HttpExpFactory {

    private static final int UNAUTHORIZED = 401;
    private static final int FORBIDDEN = 403;
    private static final int NOT_FOUND = 404;
    private static final int REQUEST_TIMEOUT = 408;
    private static final int INTERNAL_SERVER_ERROR = 500;
    private static final int BAD_GATEWAY = 502;
    private static final int SERVICE_UNAVAILABLE = 503;
    private static final int GATEWAY_TIMEOUT = 504;
    private static final int ACCESS_DENIED = 302;
    private static final int HANDEL_ERRROR = 417;

    private static int code = -1;

    public static HttpThrowable handleException(Throwable e) {
        String ex = "";
        if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            code = httpException.code();
            switch (code) {
                case UNAUTHORIZED:
                    ex = "未授权的请求";
                    break;
                case FORBIDDEN:
                    ex = "禁止访问";
                    break;
                case NOT_FOUND:
                    ex = "服务器地址未找到";
                    break;
                case REQUEST_TIMEOUT:
                    ex = "请求超时";
                    break;
                case GATEWAY_TIMEOUT:
                    ex = "网关响应超时";
                    break;
                case INTERNAL_SERVER_ERROR:
                    ex = "服务器出错";
                    break;
                case BAD_GATEWAY:
                    ex = "无效的请求";
                    break;
                case SERVICE_UNAVAILABLE:
                    ex = "服务器不可用";
                    break;
                case ACCESS_DENIED:
                    ex = "网络错误";
                    break;
                case HANDEL_ERRROR:
                    ex = "接口处理失败";
                    break;
                default:
                    if (!TextUtils.isEmpty(ex)) {
                        ex = e.getMessage();
                        break;
                    }
                    if (TextUtils.isEmpty(ex) && e.getLocalizedMessage() != null) {
                        ex = e.getLocalizedMessage();
                        break;
                    }
                    if (TextUtils.isEmpty(ex)) {
                        ex = "未知错误";
                    }
                    break;
            }
        } else if (e instanceof JsonParseException
                || e instanceof JSONException
                || e instanceof ParseException) {
            ex = "数据解析错误";
            code = ERROR.PARSE_ERROR;
        } else if (e instanceof ConnectException) {
            ex = "连接失败";
            code = ERROR.NETWORD_ERROR;
        } else if (e instanceof javax.net.ssl.SSLHandshakeException) {
            ex = "证书验证失败";
            code = ERROR.SSL_ERROR;
        } else if (e instanceof java.security.cert.CertPathValidatorException) {
            ex = "证书路径没找到";
            code = ERROR.SSL_NOT_FOUND;
        } else if (e instanceof SSLPeerUnverifiedException) {
            ex = "无有效的SSL证书";
            code = ERROR.SSL_NOT_FOUND;
        } else if (e instanceof ConnectTimeoutException) {
            ex = "连接超时";
            code = ERROR.TIMEOUT_ERROR;
        } else if (e instanceof java.net.SocketTimeoutException) {
            ex = "连接超时";
            code = ERROR.TIMEOUT_ERROR;
        } else if (e instanceof java.lang.ClassCastException) {
            ex = "类型转换出错";
            code = ERROR.FORMAT_ERROR;
        } else if (e instanceof NullPointerException) {
            ex = "数据有空";
            code = ERROR.NULL;
        } else if (e instanceof UnknownHostException) {
            ex = "服务器地址未找到,请检查网络或Url";
            code = NOT_FOUND;
        } else {
            ex = e.getMessage();
            code = ERROR.UNKNOWN;
        }
        return new HttpThrowable(code, ex);
    }

    /**
     * 约定异常
     */
    public class ERROR {
        /**
         * 未知错误
         */
        public static final int UNKNOWN = 1000;
        /**
         * 解析错误
         */
        public static final int PARSE_ERROR = 1001;
        /**
         * 网络错误
         */
        public static final int NETWORD_ERROR = 1002;
        /**
         * 协议出错
         */
        public static final int HTTP_ERROR = 1003;

        /**
         * 证书出错
         */
        public static final int SSL_ERROR = 1005;

        /**
         * 连接超时
         */
        public static final int TIMEOUT_ERROR = 1006;

        /**
         * 证书未找到
         */
        public static final int SSL_NOT_FOUND = 1007;

        /**
         * 出现空值
         */
        public static final int NULL = -100;

        /**
         * 格式错误
         */
        public static final int FORMAT_ERROR = 1008;
    }

}
