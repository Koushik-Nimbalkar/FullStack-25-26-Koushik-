package com.jobportal.util;

import java.text.NumberFormat;
import java.util.Locale;

public final class SalaryFormat {

    private SalaryFormat() {
    }

    public static String format(Long amount) {
        if (amount == null) {
            return "—";
        }
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        return nf.format(amount);
    }

    /** Short range for tables, e.g. "$95,000 - $120,000" */
    public static String formatRange(Long min, Long max) {
        if (min == null || max == null) {
            return "—";
        }
        if (min.equals(max)) {
            return format(min);
        }
        return format(min) + " – " + format(max);
    }
}
