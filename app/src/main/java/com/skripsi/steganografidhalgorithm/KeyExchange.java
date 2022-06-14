package com.skripsi.steganografidhalgorithm;

import java.math.BigInteger;

public class KeyExchange {
    private static long power(long a, long b, long p) {
        if (b == 1) {
            return a;
        } else {
            return (long) (Math.pow(a, b) % p);
        }
    }
    public static long[] keyExchangeArray (long[] decimal){
        long g = 7;
        long mod = 13;
        int i = 0;
        long[] bilangan = new long[10];
        while(i < 10){
            String hasil = String.valueOf(power(g,decimal[i], mod));
            bilangan[i] = Long.parseLong(hasil);
            i++;
        }
        return bilangan;
    }

    public static long[] keyExchangeArrayShare (long[] decimal,long[] decimalKawan){
        long mod = 13;
        int i = 0;
        long[] bilangan = new long[10];
        while(i < decimal.length){
            String hasil = String.valueOf(power(decimal[i],decimalKawan[i], mod));
            bilangan[i] = Long.parseLong(hasil);
            i++;
        }
        return bilangan;
    }

    public static BigInteger GenerateRandom() {
        long min = 1000000000;
        long max = 4294967295L;
        long hasil = (long) Math.floor(Math.random()*(max-min+1)+min);
        return BigInteger.valueOf(hasil);
    }

    public static String decimalTohexadecimal (long[] decimal){
        String bilangan;
        StringBuilder bilangan1 = new StringBuilder();
        for (long l : decimal) {
            bilangan = Long.toHexString(l);
            bilangan1.append(bilangan);
        }
        return bilangan1.toString();
    }

    public static String decimalTohexadecimal1 (long decimal){
        return Long.toHexString(decimal);
    }

    public static BigInteger hexadecimaltoDecimal (String decimal) {
        String digits = "0123456789abcdef";
        BigInteger sixteen = new BigInteger("16");
        BigInteger bigVal = new BigInteger("0");
        for (int i = 0; i < decimal.length(); i++) {
            char c = decimal.charAt(i);
            int d = digits.indexOf(c);
            BigInteger bigD = new BigInteger(String.valueOf(d));
            bigVal = (bigVal.multiply(sixteen)).add(bigD);
        }
        return bigVal;
    }

    public static long[] longtoArray (BigInteger bigInteger){
        long decimal1 = bigInteger.longValue();
        String a = String.valueOf(decimal1);
        long[] decimalArray = new long[10];
        for (int i = 0; i < 10;i++){
            char c = a.charAt(i);
            long d = Long.parseLong(String.valueOf(c));
            decimalArray[i] = d;
        }
        return decimalArray;
    }
}
