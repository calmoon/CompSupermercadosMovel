package com.dispmoveis.compsupermercadosmovel.network;

import com.dispmoveis.compsupermercadosmovel.util.Config;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class ServerClient {

    private static final String SERVER_SELECT_FILE = "server_select.php";
    private static final String SERVER_INSERT_FILE = "server_insert.php";
    private static final String SERVER_UPDATE_FILE = "server_update.php";
    private static final String SERVER_BLUESOFT_FILE = "bluesoftApi.php";

    private static AsyncHttpClient client = new AsyncHttpClient();

    private static String getAbsoluteUrl(String relativeUrl) {
        return Config.SERVER_URL_BASE + relativeUrl;
    }

    // Select sem id
    public static void select(String queryType, JsonHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams();
        params.put("queryType", queryType);
        client.get(getAbsoluteUrl(SERVER_SELECT_FILE), params, responseHandler);
    }

    // Select com id
    public static void select(String queryType, String id, JsonHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams();
        params.put("queryType", queryType);
        params.put("id", id);
        client.get(getAbsoluteUrl(SERVER_SELECT_FILE), params, responseHandler);
    }

    /*
    public static JSONObject insert(String table, Map<String, String> values) {
        Map<String, Object> params = new HashMap<>();
        params.put("table", table);
        params.putAll(values);
        return getHTTPRequestResponse(METHOD_POST, SERVER_INSERT_FILE, params);
    }

    public static JSONObject update(String table, String whereId, Map<String, String> values) {
        Map<String, Object> params = new HashMap<>();
        params.put("table", table);
        params.put("id", whereId);
        params.putAll(values);
        return getHTTPRequestResponse(METHOD_POST, SERVER_UPDATE_FILE, params);
    }
    */

    public static void bluesoftProductInfo(String barcode, String supermarketId, JsonHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams();
        params.put("barcode", barcode);
        params.put("supermarketId", supermarketId);
        client.post(getAbsoluteUrl(SERVER_BLUESOFT_FILE), params, responseHandler);
    }

    /*
    private static JSONObject getHTTPRequestResponse(String method, String serverFile, Map params) {

        ExecutorService executor = Executors.newSingleThreadExecutor();

        Future<JSONObject> threadResult = executor.submit(() -> {

                HttpRequest httpRequest = new HttpRequest(
                        Config.SERVER_URL_BASE + serverFile,
                        method,
                        "UTF-8"
                );

                for(Object paramName : params.keySet()) {
                    httpRequest.addParam((String) paramName, (String) params.get(paramName));
                }

                try {
                    InputStream inputStream = httpRequest.execute();

                    String resultString = Util.inputStream2String(inputStream, "UTF-8");
                    JSONObject responseJSON = new JSONObject(resultString);

                    httpRequest.finish();

                    return responseJSON;
                }

                catch (IOException | JSONException e) {
                    e.printStackTrace();
                    return null;
                }

            });

        JSONObject responseJSON = null;

        try {
            responseJSON = threadResult.get(20, TimeUnit.SECONDS);
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            e.printStackTrace();
        }

        if (responseJSON == null) {
            Log.e(
            "HTTP_REQUEST_NULL",
            "HTTP request failed: null response - " + serverFile + " - " + params.toString()
            );
        } else {
            Log.i(
            "HTTP_REQUEST_SENT",
            "HTTP request sent - " + serverFile + " - " + params.toString()
            );
            Log.i(
            "HTTP_REQUEST_RESPONSE",
            "Received HTTP request response: " + responseJSON.toString()
            );
        }

        executor.shutdown();

        return responseJSON;
    }
     */

}
