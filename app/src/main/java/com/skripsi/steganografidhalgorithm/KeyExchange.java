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

    public static long decToHex (long[] dec){
        long bilangan1 = 0;
        int i=9;
        for(int y = 0; y < 10; y++){
            long bilangan = (long) (dec[i] * Math.pow(16,y));
            bilangan1 = bilangan + bilangan1;
            i--;
        }
        return bilangan1;
    }

    public static long[] hextoDecLast (String decimal) {
        long[] decimal1 = new long[10];
        int i = 0;
        while (i < 10) {
            char a = decimal.charAt(i);
            if (a == '0') {
                decimal1[i] = 0;
            } else if (a == '1') {
                decimal1[i] = 1;
            } else if (a == '2') {
                decimal1[i] = 2;
            } else if (a == '3') {
                decimal1[i] = 3;
            } else if (a == '4') {
                decimal1[i] = 4;
            } else if (a == '5') {
                decimal1[i] = 5;
            } else if (a == '6') {
                decimal1[i] = 6;
            } else if (a == '7') {
                decimal1[i] = 7;
            } else if (a == '8') {
                decimal1[i] = 8;
            } else if (a == '9') {
                decimal1[i] = 9;
            } else if (a == 'a') {
                decimal1[i] = 10;
            } else if (a == 'b') {
                decimal1[i] = 11;
            } else if (a == 'c') {
                decimal1[i] = 12;
            } else if (a == 'd') {
                decimal1[i] = 13;
            } else if (a == 'e') {
                decimal1[i] = 14;
            } else if (a == 'f') {
                decimal1[i] = 15;
            }
            i++;
        }
        return decimal1;
    }
}
