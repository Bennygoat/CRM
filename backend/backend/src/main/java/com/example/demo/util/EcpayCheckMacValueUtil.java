package com.example.demo.util;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;
import java.util.TreeMap;

public class EcpayCheckMacValueUtil {

    /**
     * 產生 ECPay CheckMacValue
     */
    public static String generate(Map<String, String> params, String hashKey, String hashIv) {
        Map<String, String> sortedParams = new TreeMap<>(params);

        StringBuilder sb = new StringBuilder();
        sb.append("HashKey=").append(hashKey);
        for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
            sb.append("&").append(entry.getKey()).append("=").append(entry.getValue());
        }
        sb.append("&HashIV=").append(hashIv);

        String urlEncoded = ecpayUrlEncode(sb.toString()).toLowerCase();
        return hash(urlEncoded, "SHA-256");
    }

    /**
     * ECPay 特定的 URL 編碼
     */
    private static String ecpayUrlEncode(String source) {
        try {
            String encoded = URLEncoder.encode(source, StandardCharsets.UTF_8.toString());

            // 【修正】將 .replace() 中的比對字串從「小寫」的 %xx 改為「大寫」的 %XX
            // 以符合 Java URLEncoder 的輸出
            return encoded
                    .replace("%2D", "-")
                    .replace("%5F", "_")
                    .replace("%2E", ".")
                    .replace("%21", "!")
                    .replace("%2A", "*")
                    .replace("%28", "(")
                    .replace("%29", ")");
        } catch (Exception e) {
            throw new RuntimeException("URL encoding failed", e);
        }
    }

    /**
     * 進行 SHA-256 雜湊
     */
    private static String hash(String data, String algorithm) {
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            byte[] hashedBytes = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(2 * hashedBytes.length);
            for (byte b : hashedBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString().toUpperCase();
        } catch (Exception e) {
            throw new RuntimeException("Hashing failed", e);
        }
    }
}