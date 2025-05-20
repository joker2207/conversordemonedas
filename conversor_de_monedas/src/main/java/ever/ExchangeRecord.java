package ever;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExchangeRecord {
    private final Date date;
    private final String fromAmount;
    private final String toAmount;
    private final String rate;
    
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    
    public ExchangeRecord(Date date, String fromAmount, String toAmount, String rate) {
        this.date = date;
        this.fromAmount = fromAmount;
        this.toAmount = toAmount;
        this.rate = rate;
    }
    
    public StringProperty dateProperty() {
        return new SimpleStringProperty(DATE_FORMAT.format(date));
    }
    
    public StringProperty fromAmountProperty() {
        return new SimpleStringProperty(fromAmount);
    }
    
    public StringProperty toAmountProperty() {
        return new SimpleStringProperty(toAmount);
    }
    
    public StringProperty rateProperty() {
        return new SimpleStringProperty(rate);
    }
}