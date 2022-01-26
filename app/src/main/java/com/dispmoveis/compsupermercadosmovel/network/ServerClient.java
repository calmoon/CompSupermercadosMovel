package com.dispmoveis.compsupermercadosmovel.network;

import com.dispmoveis.compsupermercadosmovel.util.Config;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

public class ServerClient {

    private static final String SERVER_SELECT_FILE = "server_select.php";
    private static final String SERVER_INSERT_FILE = "server_insert.php";
    private static final String SERVER_UPDATE_FILE = "server_update.php";
    private static final String SERVER_DELETE_FILE = "server_delete.php";
    private static final String SERVER_BLUESOFT_FILE = "bluesoftApi.php";
    private static final String SERVER_S3_UPLOAD_FILE = "s3_image_upload.php";

    private static final AsyncHttpClient client = new AsyncHttpClient();

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
    public static void insert(String table, RequestParams values, JsonHttpResponseHandler responseHandler) {
        RequestParams params = values;
        params.put("table", table);
        client.post(getAbsoluteUrl(SERVER_INSERT_FILE), params, responseHandler);
    }

    public static void update(String table, String whereId, RequestParams values, JsonHttpResponseHandler responseHandler) {
        RequestParams params = values;
        params.put("table", table);

        Map<String, String> whereConditions = new HashMap<>();
        whereConditions.put("id", whereId);
        params.put("where", whereConditions);

        client.post(getAbsoluteUrl(SERVER_UPDATE_FILE), params, responseHandler);
    }

    public static void update(String table, Map<String, String> whereConditions, RequestParams values, JsonHttpResponseHandler responseHandler) {
        RequestParams params = values;
        params.put("table", table);
        params.put("where", whereConditions);
        client.post(getAbsoluteUrl(SERVER_UPDATE_FILE), params, responseHandler);
    }

    public static void delete(String table, String whereId, JsonHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams();
        params.put("table", table);

        Map<String, String> whereConditions = new HashMap<>();
        whereConditions.put("id", whereId);
        params.put("where", whereConditions);

        client.post(getAbsoluteUrl(SERVER_DELETE_FILE), params, responseHandler);
    }

    public static void delete(String table, Map<String, String> whereConditions, JsonHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams();
        params.put("table", table);
        params.put("where", whereConditions);
        client.post(getAbsoluteUrl(SERVER_DELETE_FILE), params, responseHandler);
    }

    public static void bluesoftProductInfo(String barcode, String supermarketId, JsonHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams();
        params.put("barcode", barcode);
        params.put("supermarketId", supermarketId);
        client.post(getAbsoluteUrl(SERVER_BLUESOFT_FILE), params, responseHandler);
    }

    public static void s3ImageUpload(String itemId, File imageFile, JsonHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams();
        params.put("itemId", itemId);
        try {
            params.put("userfile", imageFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        client.post(getAbsoluteUrl(SERVER_S3_UPLOAD_FILE), params, responseHandler);
    }

    public static void login(String username, String password, JsonHttpResponseHandler responseHandler){
        client.setBasicAuth(username, password);
        client.post(getAbsoluteUrl(SERVER_SELECT_FILE + "?queryType=verifyLogin"), responseHandler);
    }
}
