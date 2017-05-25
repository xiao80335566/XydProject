package xydproject.pluginutil.net;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.xyd.project.common.log.LogUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Administrator on 2017/5/15.
 */

public class HttpRequest {
    public final String TAG = "HttpRequest";
    private Context context;
    private HashMap<String, String> requestParamsMap;
    private IHttpCallback callback;
    private String urls;
    private static HTTP_TYPE mType = HTTP_TYPE.GET;//默认get请求方式

    public enum HTTP_TYPE {
        GET, POST
    }

    public HttpRequest(String urls, HashMap<String, String> requestParamsMap, Context context, IHttpCallback callback) {
        this.urls = urls;
        this.context = context;
        this.requestParamsMap = requestParamsMap;
        this.callback = callback;

        if (context == null) {
            LogUtils.e(TAG, "context is null");
            return;
        }

        if (callback == null) {
            LogUtils.e(TAG, "callback is null");
            return;
        }
    }

    public void setRequestMode(HTTP_TYPE mType) {
        this.mType = mType;
    }

    public void execute() {
        httpAccess();
    }

    private void httpAccess() {
        if (mType.equals(HTTP_TYPE.GET)) {
            if (!urls.contains("?")) {
                urls = urls + "?" + combineRequestParams(requestParamsMap);
            } else if (urls.substring(urls.length() - 1).equals("?")) {
                urls = urls + combineRequestParams(requestParamsMap);
            }
        }
        new HttpTask(context, callback, mType, requestParamsMap)
                .execute(new String[]{urls});
    }

    /**
     * 拼接请求参数
     *
     * @return
     */
    public String combineRequestParams(HashMap<String, String> requestParamsMap) {
        if (requestParamsMap == null) {
            return null;
        }
        Iterator<Map.Entry<String, String>> iterator = requestParamsMap.entrySet().iterator();
        StringBuffer stringBuffer = new StringBuffer();
        while (iterator.hasNext()) {
            Map.Entry<String, String> tempMap = iterator.next();

            stringBuffer.insert(0, tempMap.getKey() + "=" + tempMap.getValue() + "&");
        }
        //删除最后一个&
        stringBuffer.deleteCharAt(stringBuffer.length() - 1);
        LogUtils.i("-------------->拼接请求参数：", stringBuffer.toString());
        return stringBuffer.toString();
    }

    /**
     * 设置请求头参数（不能被覆盖）注意请求头参数的设置，因为它有可能决定了服务器返回值的类型
     *
     * @param httpURLConnection
     */
    private void setRequestProperty(HttpURLConnection httpURLConnection, HashMap<String, String> setRequestPropertyMap) {
        Iterator<Map.Entry<String, String>> iterator = setRequestPropertyMap.entrySet().iterator();
        //设置遍历了一遍Map
        while (iterator.hasNext()) {
            Map.Entry<String, String> map = iterator.next();
            LogUtils.e("------------>设置请求头:", map.getKey() + "    " + map.getValue());
            httpURLConnection.setRequestProperty(map.getKey(), map.getValue());
        }
    }

    class HttpTask extends AsyncTask<String, Void, String> {
        private ProgressDialog diaLogUtils;
        private Context mContext = null;
        private IHttpCallback mIHttpCallback = null;
        private HTTP_TYPE mType = HTTP_TYPE.GET;
        private final int CONNECTION_TIMEOUT = 5000; //建立连接超时时间 5s
        private final int READ_TIMEOUT = 5000; //数据传输超时时间 5s
        private HashMap<String, String> mParams;

        public HttpTask(Context context, IHttpCallback callback, HTTP_TYPE type, HashMap<String, String> params) {
            super();
            mContext = context;
            mIHttpCallback = callback;
            mType = type;
            mParams = params;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (context != null) {
                diaLogUtils = ProgressDialog.show(context, "提示", "操作请求正在发送，请稍等", true, false);
            }
        }

        @Override
        protected String doInBackground(String... urls) {
            if (urls == null || urls.length == 0) {
                return null;
            }
            String result = "";
            HttpURLConnection httpUrlCon = null;
            try {
                URL httpUrl = new URL(urls[0]);
                httpUrlCon = (HttpURLConnection) httpUrl.openConnection();
                // set  http  configure
                httpUrlCon.setConnectTimeout(CONNECTION_TIMEOUT);// 建立连接超时时间
                httpUrlCon.setReadTimeout(READ_TIMEOUT);//数据传输超时时间，很重要，必须设置。
                httpUrlCon.setDoInput(true); // 向连接中写入数据
                httpUrlCon.setDoOutput(true); // 从连接中读取数据
                httpUrlCon.setUseCaches(false); // 禁止缓存
                httpUrlCon.setInstanceFollowRedirects(true);
                httpUrlCon.setRequestProperty("Charset", "UTF-8");
                httpUrlCon.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                //设置请求头参数（请求参数必须写在Http正文之内）
                if (mParams != null) {
                    setRequestProperty(httpUrlCon, mParams);
                }

                switch (mType) {
                    case GET:
                        httpUrlCon.setRequestMethod("GET");// 设置请求类型为
                        break;
                    case POST:
                        httpUrlCon.setRequestMethod("POST");// 设置请求类型为
                        DataOutputStream out = new DataOutputStream(httpUrlCon.getOutputStream()); // 获取输出流
                        out.write(combineRequestParams(mParams).getBytes("utf-8"));// 将要传递的数据写入数据输出流,不要使用out.writeBytes(param); 否则中文时会出错
                        out.flush(); // 输出缓存
                        out.close(); // 关闭数据输出流
                        break;
                    default:
                        break;

                }

                httpUrlCon.connect();

                //check the result of connection
                if (httpUrlCon.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStreamReader in = new InputStreamReader(httpUrlCon.getInputStream()); // 获得读取的内容
                    BufferedReader buffer = new BufferedReader(in); // 获取输入流对象
                    String inputLine = "";
                    while ((inputLine = buffer.readLine()) != null) {
                        result += inputLine + "\n";
                    }
                    in.close(); // 关闭字符输入流
                }
            } catch (Exception e) {
                e.printStackTrace();
                //如果需要处理超时，可以在这里写
            } finally {
                if (httpUrlCon != null) {
                    httpUrlCon.disconnect(); // 断开连接
                }
            }
            LogUtils.d("HttpTask", "result_str: " + result);
            return result;

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (diaLogUtils != null && mContext != null) {
                diaLogUtils.dismiss();
            }
            LogUtils.i(System.currentTimeMillis() + "------");
            mIHttpCallback.onResponse(result);
        }
    }
}
