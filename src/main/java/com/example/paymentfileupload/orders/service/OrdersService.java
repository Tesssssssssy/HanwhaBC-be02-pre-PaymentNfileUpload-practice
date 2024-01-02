package com.example.paymentfileupload.orders.service;

import com.example.paymentfileupload.orders.model.dto.OrdersDtoReq;
import com.example.paymentfileupload.orders.repository.OrdersRepository;
import com.example.paymentfileupload.product.model.Product;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.*;

@Service
public class OrdersService {
    @Value("${portone.imp_key}")
    private String imp_key;
    @Value("${portone.imp_secret}")
    private String imp_secret;
    private OrdersRepository ordersRepository;
    public OrdersService(OrdersRepository ordersRepository) {
        this.ordersRepository = ordersRepository;
    }


    // access_token을 받아오는 메소드
    public String getToken() throws IOException {
        // 입출력 stream하는 거니까 예외 처리를 해주어야 함.

        HttpsURLConnection conn = null;

        // http method, header 설정
        URL url = new URL("https://api.iamport.kr/users/getToken");
        conn = (HttpsURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);

        JsonObject json = new JsonObject();
        json.addProperty("imp_key", imp_key);
        json.addProperty("imp_secret", imp_secret);

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
        bw.write(json.toString());
        bw.flush();
        bw.close();

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
        Gson gson = new Gson();
        String response = gson.fromJson(br.readLine(), Map.class).get("response").toString();

        String token = gson.fromJson(response, Map.class).get("access_token").toString();
        br.close();

        conn.disconnect();

        return token;
    }

    // 결제 정보 받아오는 메소드
    @RequestMapping(method = RequestMethod.GET, value = "/get/payInfo")
    public Map<String, String> getPaymentInfo(String impUid) throws IOException {
        String token = getToken();
        HttpsURLConnection conn = null;

        // http method, header 설정
        URL url = new URL("https://api.iamport.kr/payments/" + impUid);
        // + ""에 imp_uid가 들어가야 함.

        conn = (HttpsURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", token);
        conn.setDoOutput(true);

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
        Gson gson = new Gson();
        String response = gson.fromJson(br.readLine(), Map.class).get("response").toString();

        System.out.println(response.toString());

        br.close();
        conn.disconnect();

        String amount = response.split("amount")[1].split(",")[0].replace("=", "");
        String name = response.split(" name")[1].split(",")[0].replace("=", "");

        String customData = response.split("custom_data")[1].split("customer_uid")[0].replace(", ", "").replace("=", "");
        System.out.println(customData);

        Map<String, String> result = new HashMap<>();

        Integer sum = 0;
        String price;

        price = sum.toString();

        result.put("amount", amount);
        result.put("name", name);
        // result.put("price", price);
        // result.put("custom_data", customData);

        return result;
    }


    // 결제 취소(환불) 메소드
    public ResponseEntity payMentCancel(String access_token, String imp_uid, String amount, String reason) throws IOException {
        System.out.println("imp_uid = " + imp_uid);
        HttpsURLConnection conn = null;
        URL url = new URL("https://api.iamport.kr/payments/cancel");

        conn = (HttpsURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Authorization", access_token);
        conn.setDoOutput(true);

        JsonObject json = new JsonObject();

        json.addProperty("reason", reason);
        json.addProperty("imp_uid", imp_uid);
        json.addProperty("amount", amount);
        json.addProperty("checksum", amount);

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));

        bw.write(json.toString());
        bw.flush();
        bw.close();

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
        br.close();
        conn.disconnect();

        return ResponseEntity.ok().body("payment cancel success");
    }

    public void createOrders(OrdersDtoReq ordersDtoReq) {

    }

    public void findOrdersList() {

    }


}
