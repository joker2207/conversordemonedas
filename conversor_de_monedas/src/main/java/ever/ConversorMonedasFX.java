package ever;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.Date;

public class ConversorMonedasFX extends Application {
    private final ApiService api = new ApiService();
    private final CurrencyFormatter formatter = new CurrencyFormatter();
    
    private ComboBox<String> fromCombo = new ComboBox<>();
    private ComboBox<String> toCombo = new ComboBox<>();
    private TextField amountField = new TextField();
    private TextField resultField = new TextField();
    private TextField rateField = new TextField();
    private TableView<ExchangeRecord> historyTable = new TableView<>();
    
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        configureUI(stage);
    }

    private void configureUI(Stage stage) {
        // Configurar comboboxes
        fromCombo.setItems(FXCollections.observableArrayList(
            "USD", "EUR", "JPY", "GBP", "MXN", "BRL", "CAD", "AUD"));
        toCombo.setItems(FXCollections.observableArrayList(
            "USD", "EUR", "JPY", "GBP", "MXN", "BRL", "CAD", "AUD"));
        fromCombo.getSelectionModel().selectFirst();
        toCombo.getSelectionModel().select(1);

        // Configurar campos
        amountField.setPromptText("Ingrese cantidad");
        resultField.setEditable(false);
        rateField.setEditable(false);
        
        // Configurar tabla de historial
        configureHistoryTable();
        
        // Botones
        Button convertBtn = createConvertButton();
        Button swapBtn = createSwapButton();
        Button clearBtn = createClearButton();
        
        // Layout
        GridPane grid = createMainGrid(convertBtn, swapBtn, clearBtn);
        VBox root = createRootLayout(grid);
        
        // Escena
        Scene scene = new Scene(root, 700, 550);
        stage.setTitle("Conversor de Monedas Avanzado");
        stage.setScene(scene);
        stage.show();
    }

    private Button createConvertButton() {
        Button btn = new Button("Convertir");
        btn.setStyle("-fx-font-weight: bold; -fx-base: #4CAF50;");
        btn.setOnAction(e -> convertCurrency());
        return btn;
    }

    private Button createSwapButton() {
        Button btn = new Button("⇄ Intercambiar");
        btn.setOnAction(e -> swapCurrencies());
        return btn;
    }

    private Button createClearButton() {
        Button btn = new Button("Limpiar");
        btn.setStyle("-fx-base: #f44336;");
        btn.setOnAction(e -> clearFields());
        return btn;
    }

    private GridPane createMainGrid(Button convertBtn, Button swapBtn, Button clearBtn) {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        grid.add(new Label("Moneda origen:"), 0, 0);
        grid.add(fromCombo, 1, 0);
        grid.add(swapBtn, 2, 0);
        grid.add(new Label("Moneda destino:"), 3, 0);
        grid.add(toCombo, 4, 0);
        
        grid.add(new Label("Cantidad:"), 0, 1);
        grid.add(amountField, 1, 1, 4, 1);
        
        grid.add(convertBtn, 4, 2);
        grid.add(clearBtn, 3, 2);
        
        grid.add(new Label("Resultado:"), 0, 3);
        grid.add(resultField, 1, 3, 2, 1);
        grid.add(new Label("Tasa:"), 3, 3);
        grid.add(rateField, 4, 3);
        
        return grid;
    }

    private VBox createRootLayout(GridPane grid) {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.getChildren().addAll(
            new Label("CONVERSOR DE MONEDAS AVANZADO"),
            grid,
            new Separator(),
            new Label("Historial de Conversiones:"),
            historyTable
        );
        return root;
    }

    private void configureHistoryTable() {
        TableColumn<ExchangeRecord, String> dateCol = new TableColumn<>("Fecha");
        dateCol.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
        
        TableColumn<ExchangeRecord, String> fromCol = new TableColumn<>("De");
        fromCol.setCellValueFactory(cellData -> cellData.getValue().fromAmountProperty());
        
        TableColumn<ExchangeRecord, String> toCol = new TableColumn<>("A");
        toCol.setCellValueFactory(cellData -> cellData.getValue().toAmountProperty());
        
        TableColumn<ExchangeRecord, String> rateCol = new TableColumn<>("Tasa");
        rateCol.setCellValueFactory(cellData -> cellData.getValue().rateProperty());
        
        historyTable.getColumns().add(dateCol);
        historyTable.getColumns().add(fromCol);
        historyTable.getColumns().add(toCol);
        historyTable.getColumns().add(rateCol);
        
        historyTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void convertCurrency() {
        try {
            String from = fromCombo.getValue();
            String to = toCombo.getValue();
            double amount = Double.parseDouble(amountField.getText());
            
            double rate = api.getExchangeRate(from, to);
            double result = amount * rate;
            
            resultField.setText(formatter.format(result, to));
            rateField.setText(String.format("1 %s = %.6f %s", from, rate, to));
            
            addToHistory(from, to, amount, result, rate);
            
        } catch (NumberFormatException e) {
            showAlert("Error", "Ingrese una cantidad válida");
        } catch (Exception e) {
            showAlert("Error", e.getMessage());
        }
    }

    private void addToHistory(String from, String to, double amount, double result, double rate) {
        ExchangeRecord record = new ExchangeRecord(
            new Date(),
            formatter.format(amount, from),
            formatter.format(result, to),
            String.format("%.6f", rate)
        );
        historyTable.getItems().add(0, record);
    }

    private void swapCurrencies() {
        String from = fromCombo.getValue();
        String to = toCombo.getValue();
        
        fromCombo.setValue(to);
        toCombo.setValue(from);
        
        if (!amountField.getText().isEmpty() && !resultField.getText().isEmpty()) {
            String temp = amountField.getText();
            amountField.setText(resultField.getText().replaceAll("[^\\d.]", ""));
            resultField.setText(temp);
        }
    }

    private void clearFields() {
        amountField.clear();
        resultField.clear();
        rateField.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}