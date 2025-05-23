package com.example.crabfood_api.service.VNPay;


import com.example.crabfood_api.config.VNPayConfig;
import com.example.crabfood_api.util.VNPayUtil;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class VNPayService {

  private final VNPayConfig vnPayConfig;

  public VNPayService(VNPayConfig vnPayConfig) {
    this.vnPayConfig = vnPayConfig;
  }

  public String createPaymentUrl(Long orderId, Long amount, String orderInfo) {
    String vnp_TxnRef = orderId + "-" + System.currentTimeMillis();
    String vnp_TmnCode = vnPayConfig.getVnp_TmnCode();
    String bankCode = "NCB";

    Map<String, String> vnpParamsMap = vnPayConfig.getVNPayConfig();
    vnpParamsMap.put("vnp_TxnRef",vnp_TxnRef);
    vnpParamsMap.put("vnp_TmnCode",vnp_TmnCode);
    vnpParamsMap.put("vnp_OrderInfo",orderInfo);
    vnpParamsMap.put("vnp_Amount", String.valueOf(amount*100L));
    vnpParamsMap.put("vnp_BankCode", bankCode);
    vnpParamsMap.put("vnp_IpAddr", VNPayUtil.getClientIpAddress());
    //build query url
    String queryUrl = VNPayUtil.getPaymentURL(vnpParamsMap, true);
    String hashData = VNPayUtil.getPaymentURL(vnpParamsMap, false);
    String vnpSecureHash = VNPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), hashData);
    queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
    return vnPayConfig.getVnp_PayUrl() + "?" + queryUrl;
  }
}
