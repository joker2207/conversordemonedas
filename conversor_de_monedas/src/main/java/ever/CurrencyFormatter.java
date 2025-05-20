package ever;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

public class CurrencyFormatter {
    private static final Map<String, Locale> LOCALES = Map.ofEntries(
        Map.entry("USD", Locale.US),
        Map.entry("EUR", Locale.GERMANY),
        Map.entry("JPY", Locale.JAPAN),
        Map.entry("GBP", Locale.UK),
        Map.entry("MXN", new Locale("es", "MX")),
        Map.entry("BRL", new Locale("pt", "BR")),
        Map.entry("CAD", Locale.CANADA),
        Map.entry("AUD", new Locale("en", "AU"))
    );

    public String format(double amount, String currencyCode) {
        Locale locale = LOCALES.getOrDefault(currencyCode, Locale.US);
        NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);
        formatter.setMaximumFractionDigits(2);
        return formatter.format(amount);
    }

    public static String[] getAvailableCurrencies() {
        return LOCALES.keySet().toArray(new String[0]);
    }
}