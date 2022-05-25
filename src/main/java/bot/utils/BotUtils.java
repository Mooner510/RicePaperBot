package bot.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.List;

public class BotUtils {
    private static final List<String> suffix = List.of("", "k", "M", "B", "T", "Q");
    public static String numberTic(double value, int a) {
        if (value < 1) {
            return (int) Math.round(value) + "";
        }
        double amount = Math.floor(Math.floor(Math.log10(value)) / 3);
        if (amount != 0) {
            value = value * Math.pow(0.001, amount);
            return parseString(value, a, true) + suffix.get((int) Math.floor(amount));
        }
        return parseString(value, a, true);
    }

    public static String commaNumber(int number) {
        return NumberFormat.getInstance().format(number);
    }

    public static String commaNumber(double number) {
        return NumberFormat.getInstance().format(number);
    }

    public static String parseIfInt(double value, boolean comma) {
        if(value >= Integer.MAX_VALUE) {
            return numberTic(value, 3);
        }
        if(comma) {
            if (Math.floor(value) == value) {
                return commaNumber((int) Math.floor(value));
            }
            return commaNumber(value);
        } else {
            if (Math.floor(value) == value) {
                return ((int) Math.floor(value)) + "";
            }
            return BigDecimal.valueOf(value).toPlainString();
        }
    }

    public static String parseString(double value) {
        BigDecimal b = BigDecimal.valueOf(value);
        b = b.setScale(0, RoundingMode.DOWN);
        return parseIfInt(Double.parseDouble(b.toString()), false);
    }

    public static String parseString(double value, int amount) {
        BigDecimal b = BigDecimal.valueOf(value);
        b = b.setScale(amount, RoundingMode.DOWN);
        return parseIfInt(Double.parseDouble(b.toString()), false);
    }

    public static String parseString(double value, boolean comma) {
        BigDecimal b;
        try {
            b = BigDecimal.valueOf(value);
        } catch (Exception e) {
            return value + "";
        }
        b = b.setScale(0, RoundingMode.DOWN);
        return parseIfInt(Double.parseDouble(b.toString()), comma);
    }

    public static String parseString(double value, int amount, boolean comma) {
        BigDecimal b = BigDecimal.valueOf(value);
        b = b.setScale(amount, RoundingMode.DOWN);
        return parseIfInt(Double.parseDouble(b.toString()), comma);
    }
}
