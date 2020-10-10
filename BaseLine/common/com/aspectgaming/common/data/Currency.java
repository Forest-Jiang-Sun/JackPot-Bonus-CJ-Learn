package com.aspectgaming.common.data;

import java.text.NumberFormat;

/**
 * @author ligang.yao
 */
public enum Currency {
    USD("United States Dollar", "USD", 2, 100, "$"),
    CHY("Chinese Yuan", "CHY", 2, 100, "¥"),
    HKD("Hong Kong Dollar", "HKD", 2, 100, "$"),
    KRW("South Korean Won", "KRW", 2, 100, "₩"),
    MOP("Macanese Pataca", "MOP", 2, 100, "$"),
    PHP("Philippine Peso", "PHP", 2, 100, "₱"),
    THB("Thai Baht", "THB", 2, 100, "฿"),
    VND("Vietnamese Dong", "VND", 2, 100, "$"),
    CRC("Costa Rica Colonais", "CRC", 2, 100, "₡");

    private Currency(String name, String abbreviation, int digit, int conversionRate, String symbol) {
        this.name = name;
        this.abbreviation = abbreviation;
        this.digit = digit;
        this.conversionRate = conversionRate;
        this.symbol = symbol;
    }

    private final NumberFormat formatter = NumberFormat.getInstance();

    public String name;
    public String abbreviation;
    public int digit;
    public int conversionRate;
    public String symbol;

    @Override
    public String toString() {
        return abbreviation;
    }

    public String format(long val) {
        formatter.setMaximumIntegerDigits(30);
        formatter.setMinimumIntegerDigits(1);
        formatter.setGroupingUsed(true);
        formatter.setMinimumFractionDigits(digit);
        formatter.setMaximumFractionDigits(digit);

        return symbol + formatter.format(val / (double) conversionRate);
    }

    public String format(double val)
    {
        formatter.setMaximumIntegerDigits(30);
        formatter.setMinimumIntegerDigits(1);
        formatter.setGroupingUsed(true);
        formatter.setMinimumFractionDigits(digit);
        formatter.setMaximumFractionDigits(digit);
        return symbol+formatter.format(val);
    }

    public String denomFormat(long val) {
        long integer = val / conversionRate;
        long fraction = val % conversionRate;

        if (integer == 0) {
            // TODO: Need to consider Satang in future games
            return fraction + "￠";
        } else {
            return symbol + integer;
        }
    }
}
