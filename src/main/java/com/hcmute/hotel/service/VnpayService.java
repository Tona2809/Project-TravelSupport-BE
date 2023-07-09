package com.hcmute.hotel.service;

import com.hcmute.hotel.config.VnpayConfig;
import com.hcmute.hotel.model.entity.BookingEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
@Service
public class VnpayService {
    @Autowired
    private VnpayConfig vnpayConfig;

    public Map<String,Object> createPayment(BookingEntity booking, HttpServletRequest req) throws UnsupportedEncodingException {
        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        long amount = booking.getTotalPrice()* 100L;
        String orderType = "topup";

        String vnp_TxnRef = vnpayConfig.getRandomNumber(8);
        String vnp_TmnCode = VnpayConfig.vnp_TmnCode;

        return getStringObjectMap(vnp_Version, vnp_Command, amount, orderType, vnp_TxnRef, vnp_TmnCode);
    }

    public Map<String,Object> createPayment(BookingEntity booking, HttpServletRequest req, String vnp_TxnRef) throws UnsupportedEncodingException {
        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        long amount = booking.getTotalPrice()* 100L;
        String orderType = "topup";

        String vnp_TmnCode = VnpayConfig.vnp_TmnCode;

        return getStringObjectMap(vnp_Version, vnp_Command, amount, orderType, vnp_TxnRef, vnp_TmnCode);
    }

    private Map<String, Object> getStringObjectMap(String vnp_Version, String vnp_Command, long amount, String orderType, String vnp_TxnRef, String vnp_TmnCode) throws UnsupportedEncodingException {
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_BankCode", "NCB");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_TxnRef);
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_OrderType", orderType);
        vnp_Params.put("vnp_ReturnUrl", vnpayConfig.vnp_returnUrl);
        vnp_Params.put("vnp_IpAddr", "116.110.43.200");
        vnp_Params.put("vnp_Locale", "vn");

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = vnpayConfig.hmacSHA512(vnpayConfig.vnp_HashSecret, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = vnpayConfig.vnp_PayUrl + "?" + queryUrl;
        Map<String,Object> map = new HashMap<>();
        map.put("link",paymentUrl);
        map.put("paymentId",vnp_TxnRef);
        return map;
    }


}
