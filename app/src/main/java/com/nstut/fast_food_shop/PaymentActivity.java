package com.nstut.fast_food_shop;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.nstut.fast_food_shop.adapter.CartAdapter;
import com.nstut.fast_food_shop.data.models.ProductRoom;
import com.nstut.fast_food_shop.databinding.ActivityPaymentBinding;
import com.nstut.fast_food_shop.model.CartItem;
import com.nstut.fast_food_shop.model.FoodItem;
import com.nstut.fast_food_shop.presentation.ui.activities.BaseActivity;
import com.nstut.fast_food_shop.util.Utils;

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

public class PaymentActivity extends BaseActivity {

    private ActivityPaymentBinding binding;
    private List<CartItem> cartItems;

    private static final int SHIPPING_FEE = 15000;
    private static final int OTHER_FEE = 6000;
    private int finalTotal = 0;

    private static final String VNP_TMNCODE = "BY7EO884";
    private static final String VNP_HASHSECRET = "SFHP2M5E244J9KU4STDIJ2K3UH6OMICE";
    private static final String VNP_RETURNURL = "https://49f7ae33d5e6.ngrok-free.app/vnpay-return";
    private static final String VNP_URL = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    private static final String VNP_QUERY_URL = "https://sandbox.vnpayment.vn/merchant_webapi/api/transaction";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupHeader(true);

        cartItems = getIntent().getParcelableArrayListExtra("cart");
        if (cartItems == null) cartItems = new ArrayList<>();

        CartAdapter adapter = new CartAdapter(cartItems, this::updateTotal);
        binding.rvOrderList.setLayoutManager(new LinearLayoutManager(this));
        binding.rvOrderList.setAdapter(adapter);

        updateTotal();
    }

    private void updateTotal() {
        double itemTotal = 0;
        for (CartItem item : cartItems) {
            itemTotal += item.getProduct().getPrice() * item.getQuantity();
        }
        finalTotal = (int) (itemTotal + SHIPPING_FEE + OTHER_FEE);

        binding.tvItemTotal.setText("Item Total: " + Utils.formatCurrency(itemTotal));
        binding.tvShippingFee.setText("Shipping Fee: " + Utils.formatCurrency(SHIPPING_FEE));
        binding.tvOtherFee.setText("Other Fee: " + Utils.formatCurrency(OTHER_FEE));
        binding.tvTotalPrice.setText("Total Price: " + Utils.formatCurrency(finalTotal));

        binding.btnPlaceOrder.setOnClickListener(v -> {
            String address = binding.edtAddress.getText().toString().trim();
            if (address.isEmpty()) {
                Toast.makeText(this, "Please enter a delivery address.", Toast.LENGTH_SHORT).show();
                return;
            }

            int selectedId = binding.radioGroupPayment.getCheckedRadioButtonId();
            if (selectedId == R.id.radioCod) {
                Toast.makeText(this, "Order placed successfully (COD)!", Toast.LENGTH_SHORT).show();
                finish();
            } else if (selectedId == R.id.radioVnpay) {
                startVnpayPayment(finalTotal);
            }
        });
    }

    private void startVnpayPayment(int amount) {
        try {
            String vnp_TxnRef = String.valueOf(System.currentTimeMillis());
            String vnp_CreateDate = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());

            Map<String, String> vnp_Params = new TreeMap<>();
            vnp_Params.put("vnp_Version", "2.1.0");
            vnp_Params.put("vnp_Command", "pay");
            vnp_Params.put("vnp_TmnCode", VNP_TMNCODE);
            vnp_Params.put("vnp_Amount", String.valueOf((long) amount * 100));
            vnp_Params.put("vnp_CurrCode", "VND");
            vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
            vnp_Params.put("vnp_OrderInfo", "Order_Payment_" + vnp_TxnRef);
            vnp_Params.put("vnp_OrderType", "other");
            vnp_Params.put("vnp_Locale", "vn");
            vnp_Params.put("vnp_ReturnUrl", VNP_RETURNURL);
            vnp_Params.put("vnp_IpAddr", getDeviceIpAddress());
            vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

            Calendar expire = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
            expire.add(Calendar.MINUTE, 15);
            String vnp_ExpireDate = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(expire.getTime());
            vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

            StringBuilder rawData = new StringBuilder();
            boolean first = true;
            for (Map.Entry<String, String> entry : vnp_Params.entrySet()) {
                if (!first) rawData.append("&");
                rawData.append(entry.getKey()).append("=").append(entry.getValue());
                first = false;
            }

            String secureHash = hmacSHA512(VNP_HASHSECRET, rawData.toString());

            StringBuilder query = new StringBuilder();
            first = true;
            for (Map.Entry<String, String> entry : vnp_Params.entrySet()) {
                if (!first) query.append("&");
                String encodedKey = URLEncoder.encode(entry.getKey(), "UTF-8");
                String encodedValue = URLEncoder.encode(entry.getValue(), "UTF-8");
                query.append(encodedKey).append("=").append(encodedValue);
                first = false;
            }

            query.append("&vnp_SecureHash=").append(secureHash);

            String paymentUrl = VNP_URL + "?" + query.toString();
            Log.d("VNPAY_DEBUG", "RawData to hash: " + rawData.toString());
            Intent intent = new Intent(this, VnpayWebViewActivity.class);
            intent.putExtra("paymentUrl", paymentUrl);
            startActivity(intent);
        } catch (Exception e) {
            Log.e("VNPAY_ERROR", "Error creating payment URL", e);
            Toast.makeText(this, "Error creating payment URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    private String getDeviceIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("VNPAY_ERROR", "Error getting IP address", ex);
        }
        return "127.0.0.1";
    }

    public static String hmacSHA512(final String key, final String data) {
        try {

            if (key == null || data == null) {
                throw new NullPointerException();
            }
            final Mac hmac512 = Mac.getInstance("HmacSHA512");
            byte[] hmacKeyBytes = key.getBytes();
            final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
            hmac512.init(secretKey);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();

        } catch (Exception ex) {
            return "";
        }
    }

    private void validateParameters(Map<String, String> params) {
        Log.d("VNPAY_PARAMS", "=== Checking parameters ===");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            Log.d("VNPAY_PARAMS", entry.getKey() + " = " + entry.getValue());
        }
    }
}