package com.nstut.fast_food_shop;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.nstut.fast_food_shop.adapter.CartAdapter;
import com.nstut.fast_food_shop.databinding.ActivityPaymentBinding;
import com.nstut.fast_food_shop.model.FoodItem;
import com.nstut.fast_food_shop.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class PaymentActivity extends AppCompatActivity {

    private ActivityPaymentBinding binding;
    private ArrayList<FoodItem> cart;

    private static final int SHIPPING_FEE = 15000;
    private static final int OTHER_FEE = 6000;
    private int finalTotal = 0;

//    private static final String VNP_TMNCODE = "BY7EO884";
//    private static final String VNP_HASHSECRET = "SFHP2M5E244J9KU4STDIJ2K3UH6OMICE";
//    private static final String VNP_RETURNURL = "https://49f7ae33d5e6.ngrok-free.app/vnpay-return";
//    private static final String VNP_URL = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
//    private static final String VNP_QUERY_URL = "https://sandbox.vnpayment.vn/merchant_webapi/api/transaction";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        cart = getIntent().getParcelableArrayListExtra("cart");
        if (cart == null) cart = new ArrayList<>();

        CartAdapter adapter = new CartAdapter(cart, this::updateTotal);
        binding.rvOrderList.setLayoutManager(new LinearLayoutManager(this));
        binding.rvOrderList.setAdapter(adapter);

        binding.tvAddress.setText("70 Tr\u1ecbnh Quang Nghi\u1ec7, P.4, T\u00e2n An");
        binding.tvPhone.setText("Tu\u1ea5n Khang - 84849135986");

        updateTotal();
    }

    private void updateTotal() {
        int itemTotal = 0;
        for (FoodItem item : cart) {
            itemTotal += item.getPrice() * item.getQuantity();
        }
        finalTotal = itemTotal + SHIPPING_FEE + OTHER_FEE;

        binding.tvItemTotal.setText("Tổng giá món: " + Utils.formatCurrency(itemTotal));
        binding.tvShippingFee.setText("Phí giao hàng: " + Utils.formatCurrency(SHIPPING_FEE));
        binding.tvOtherFee.setText("Phí khác: " + Utils.formatCurrency(OTHER_FEE));
        binding.tvTotalPrice.setText("Tổng thanh toán: " + Utils.formatCurrency(finalTotal));

        binding.btnPlaceOrder.setOnClickListener(v -> {
            int selectedId = binding.radioGroupPayment.getCheckedRadioButtonId();
            if (selectedId == R.id.radioCod) {
                Toast.makeText(this, "Đặt đơn thành công (COD)!", Toast.LENGTH_SHORT).show();
                finish();
//            } else if (selectedId == R.id.radioVnpay) {
//                startVnpayPayment(finalTotal);
            }else if (selectedId == R.id.radioStripe) {
                startStripeCheckout(finalTotal);
            }

        });
    }
    private void startStripeCheckout(int amount) {
        String backendUrl = "https://46a0def34127.ngrok-free.app";

        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("productName", "Đơn hàng FastFood");
            jsonBody.put("amount", amount);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    backendUrl + "/create-checkout-session",
                    jsonBody,
                    response -> {
                        try {
                            String checkoutUrl = response.getString("url");
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(checkoutUrl));
                            startActivity(intent);
                        } catch (JSONException e) {
                            Toast.makeText(this, "Lỗi đọc phản hồi Stripe", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> Toast.makeText(this, "Lỗi tạo session Stripe", Toast.LENGTH_SHORT).show()
            );

            Volley.newRequestQueue(this).add(request);

        } catch (Exception e) {
            Toast.makeText(this, "Lỗi khi tạo thanh toán Stripe", Toast.LENGTH_SHORT).show();
        }
    }

//    private String getDeviceIpAddress() {
//        try {
//            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
//                NetworkInterface intf = en.nextElement();
//                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
//                    InetAddress inetAddress = enumIpAddr.nextElement();
//                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
//                        return inetAddress.getHostAddress();
//                    }
//                }
//            }
//        } catch (SocketException ex) {
//            Log.e("VNPAY_ERROR", "Error getting IP address", ex);
//        }
//        return "127.0.0.1";
//    }
//
//    private void validateParameters(Map<String, String> params) {
//        Log.d("VNPAY_PARAMS", "=== Checking parameters ===");
//        for (Map.Entry<String, String> entry : params.entrySet()) {
//            Log.d("VNPAY_PARAMS", entry.getKey() + " = " + entry.getValue());
//        }
//    }
//    private void startVnpayPayment(int amount) {
//        try {
//            String vnp_TxnRef = String.valueOf(System.currentTimeMillis());
//            String vnp_CreateDate = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());
//
//            Map<String, String> vnp_Params = new TreeMap<>();
//            vnp_Params.put("vnp_Version", "2.1.0");
//            vnp_Params.put("vnp_Command", "pay");
//            vnp_Params.put("vnp_TmnCode", VNP_TMNCODE);
//            vnp_Params.put("vnp_Amount", String.valueOf((long) amount * 100));
//            vnp_Params.put("vnp_CurrCode", "VND");
//            vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
//            vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang " + vnp_TxnRef);
//            vnp_Params.put("vnp_OrderType", "other");
//            vnp_Params.put("vnp_Locale", "vn");
//            vnp_Params.put("vnp_ReturnUrl", VNP_RETURNURL);
//            vnp_Params.put("vnp_IpAddr", getDeviceIpAddress());
//            vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
//
//            Calendar expire = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
//            expire.add(Calendar.MINUTE, 15);
//            String vnp_ExpireDate = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(expire.getTime());
//            vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);
//
//            validateParameters(vnp_Params);
//
//            StringJoiner rawData = new StringJoiner("&");
//            for (Map.Entry<String, String> entry : vnp_Params.entrySet()) {
//                rawData.add(entry.getKey() + "=" + entry.getValue());
//            }
//
//            String secureHash = hmacSHA512(VNP_HASHSECRET, rawData.toString());
//
//            StringJoiner query = new StringJoiner("&");
//            for (Map.Entry<String, String> entry : vnp_Params.entrySet()) {
//                String encodedKey = URLEncoder.encode(entry.getKey(), "UTF-8");
//                String encodedValue = URLEncoder.encode(entry.getValue(), "UTF-8");
//                query.add(encodedKey + "=" + encodedValue);
//            }
//
////            query.add("vnp_SecureHash=" + secureHash);
//            query.add("vnp_SecureHash=" + URLEncoder.encode(secureHash, "UTF-8"));
//
//
//
//            String paymentUrl = VNP_URL + "?" + query.toString();
//            Log.d("VNPAY_DEBUG", "RawData to hash: " + rawData.toString());
//            Log.d("VNPAY_URL", "Full URL: " + paymentUrl);
//
//            Intent intent = new Intent(this, VnpayWebViewActivity.class);
//            intent.putExtra("paymentUrl", paymentUrl);
//            startActivity(intent);
//
//        } catch (Exception e) {
//            Log.e("VNPAY_ERROR", "Error creating payment URL", e);
//            Toast.makeText(this, "Lỗi tạo URL thanh toán: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//        }
//    }
//        public static String hmacSHA512(final String key, final String data) {
//            try {
//
//                if (key == null || data == null) {
//                    throw new NullPointerException();
//                }
//                final Mac hmac512 = Mac.getInstance("HmacSHA512");
//                byte[] hmacKeyBytes = key.getBytes();
//                final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
//                hmac512.init(secretKey);
//                byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
//                byte[] result = hmac512.doFinal(dataBytes);
//                StringBuilder sb = new StringBuilder(2 * result.length);
//                for (byte b : result) {
//                    sb.append(String.format("%02x", b & 0xff));
//                }
//                return sb.toString();
//
//            } catch (Exception ex) {
//                return "";
//            }
//        }
}