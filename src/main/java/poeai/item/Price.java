package poeai.item;

import java.util.Optional;
import java.util.regex.Pattern;

public record Price(String quantity,
                    String currency) {

    private static final Pattern PRICE_PATTERN = Pattern.compile("~(b/o|price) ([0-9.,]+) (\\w+).*");

    public static Optional<Price> of(String price) {
        if(price == null) {
            return Optional.empty();
        }
        var matcher = PRICE_PATTERN.matcher(price);
        if (matcher.find()) {
            return Optional.of(new Price(matcher.group(2), matcher.group(3)));
        } else {
            return Optional.empty();
        }
    }
}
