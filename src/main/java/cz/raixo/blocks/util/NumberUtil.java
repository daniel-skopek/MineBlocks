package cz.raixo.blocks.util;

import java.util.Optional;
import java.util.regex.Pattern;

public class NumberUtil {

    private static final Pattern INT_PATTERN = Pattern.compile("[0-9]+");
    private static final Pattern FLOAT_PATTERN = Pattern.compile("[0-9]+?(\\.[0-9]+)");


    public static String format(int number) {
        String raw = String.valueOf(number);
        StringBuilder formatted = new StringBuilder();
        int length = raw.length();
        for (int i = 0; i < length; i++) {
            if (i > 0 && (length - i) % 3 == 0) formatted.append(' ');
            formatted.append(raw.charAt(i));
        }
        return formatted.toString();
    }

    public static Optional<Integer> parseInt(String s) {
        if (!INT_PATTERN.matcher(s).matches()) {
            return Optional.empty();
        } else {
            try {
                return Optional.of(Integer.parseInt(s));
            } catch (Throwable t) {
                return Optional.empty();
            }
        }
    }

    public static Optional<Float> parseFloat(String s) {
        if (!FLOAT_PATTERN.matcher(s).matches()) {
            return Optional.empty();
        } else {
            try {
                return Optional.of(Float.parseFloat(s));
            } catch (Throwable t) {
                return Optional.empty();
            }
        }
    }

}
