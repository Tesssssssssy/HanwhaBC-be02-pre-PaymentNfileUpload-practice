package com.example.paymentfileupload.orders.service;

import com.example.paymentfileupload.orders.model.dto.OrdersDtoReq;
import com.example.paymentfileupload.orders.repository.OrdersRepository;
import com.example.paymentfileupload.product.model.Product;
import com.example.paymentfileupload.product.model.dto.ProductDtoRes;
import com.example.paymentfileupload.product.repository.ProductRepository;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import net.bytebuddy.description.method.MethodDescription;
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
    private ProductRepository productRepository;

    public OrdersService(OrdersRepository ordersRepository, ProductRepository productRepository) {
        this.ordersRepository = ordersRepository;
        this.productRepository = productRepository;
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
    public Map<String, Object> getPaymentInfo(String impUid) throws IOException {
        String token = getToken();
        HttpsURLConnection conn = null;

        // http method, header 설정
        URL url = new URL("https://api.iamport.kr/payments/" + impUid);        // + ""에 imp_uid가 들어가야 함.

        conn = (HttpsURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", token);
        conn.setDoOutput(true);

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
        Gson gson = new Gson();
        String response = gson.fromJson(br.readLine(), Map.class).get("response").toString();

        System.out.println("response: " + response.toString());

        br.close();
        conn.disconnect();

        Map<String, Object> result = new HashMap<>();

        String amount = response.split("amount")[1].split(",")[0].replace("=", "");
        String name = response.split(" name")[1].split(",")[0].replace("=", "");

        result.put("amount", amount);
        result.put("name", name);

        String customData = response.split("custom_data")[1].split("customer_uid")[0].replace(", ", "").replace("=", "");
        System.out.println("custom_data: " + customData);

        Type productListType = new TypeToken<List<ProductDtoRes>>(){}.getType();
        List<ProductDtoRes> productList = gson.fromJson(customData, productListType);

        List<Map<String, String>> productsInfo = new ArrayList<>();  // Accumulate product information

        for (ProductDtoRes res : productList) {
            Map<String, String> productInfo = new HashMap<>();
            productInfo.put("id", res.getId().toString());
            productInfo.put("name", res.getName());
            productInfo.put("price", res.getPrice().toString());
            productsInfo.add(productInfo);
        }
        result.put("products", productsInfo);

        return result;
    }

    public Integer findProductPriceById(Long id) {
        Optional<Product> result = productRepository.findById(id);
        if (result.isPresent()) {
            Product product = result.get();
            Integer price = product.getPrice();

            return price;
        } else {
            return null;
        }
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
