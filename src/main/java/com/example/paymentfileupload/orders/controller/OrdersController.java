package com.example.paymentfileupload.orders.controller;

import com.example.paymentfileupload.orders.model.dto.OrdersDtoReq;
import com.example.paymentfileupload.orders.model.dto.OrdersDtoRes;
import com.example.paymentfileupload.orders.service.OrdersService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin("*")
// 일단 모든 요청을 다 허용한다.
// 나중에 프론트가 개발되면 특정 주소만 허용해주는 것으로 수정해야 함.
@RequestMapping("/order")
public class OrdersController {
    private OrdersService ordersService;

    public OrdersController(OrdersService ordersService) {
        this.ordersService = ordersService;
    }

    // 실질적으로 결제를 검증하는 메소드
    @RequestMapping("/validation")
    public ResponseEntity paymentValidation(String impUid) throws IOException {
        String dbPrice = "10000.0";     // 원래는 repository에서 findByName으로 db에서 조회해서 가져와야 하는 값.
        Map<String, String> paymentResult = ordersService.getPaymentInfo(impUid);

        System.out.println("Payment Result: ");
        for (Map.Entry<String, String> entry : paymentResult.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

        if (dbPrice.equals(paymentResult.get("amount"))) {
            return ResponseEntity.ok().body("ok");
        } else {
            // 환불처리
            String token = ordersService.getToken();
            ordersService.payMentCancel(token, impUid, paymentResult.get("amount").toString(), "결제 금액 에러");
            return ResponseEntity.badRequest().body("error");
        }
    }

    /**
     * amount값은 있으니까
     * Map<String, Object> 로 저장해서
     * Object에 customData를 저장하고
     * customData에 id, name, price가 있으니까
     * 그럼 map에 map을 저장할 수 있는 구조가 되고
     * 그 object의 id에 접근해서 findById로 조회해서
     * 가격 가져와서 다 더하고
     * amount와 비교하면 될 듯.
     *
     */

    @RequestMapping(method = RequestMethod.POST, value = "/create")
    public ResponseEntity createOrders(OrdersDtoReq ordersDtoReq) {

        return ResponseEntity.ok().body("");
    }


    @RequestMapping(method = RequestMethod.GET, value = "/list")
    public ResponseEntity findOrdersList() {

        return ResponseEntity.ok().body("");
    }
}
