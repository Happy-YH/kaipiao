package com.example.invoice.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class IdGenerator {
    private static final Random random = new Random();

    public static String generateInvoiceNo() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String timestamp = sdf.format(new Date());
        String randomNum = String.format("%06d", random.nextInt(1000000));
        return timestamp + randomNum;
    }

    public static String generateRedConfirmationNo() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String date = sdf.format(new Date());
        String randomNum = String.format("%04d", random.nextInt(10000));
        return "RCN" + date + randomNum;
    }

    public static String generateBatchTaskNo() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String timestamp = sdf.format(new Date());
        String randomNum = String.format("%04d", random.nextInt(10000));
        return "BT" + timestamp + randomNum;
    }

    public static String generateRecordNo() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String date = sdf.format(new Date());
        String randomNum = String.format("%04d", random.nextInt(10000));
        return "RP" + date + randomNum;
    }
}