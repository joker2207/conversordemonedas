package ever;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class ApiService {
    private static final String API_KEY = "20f7c2036f63f7eb46eeb682";
    private static final String BASE_URL = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/pair/";
    
    private final HttpClient client;
    
    public ApiService() {
        this.client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .version(HttpClient.Version.HTTP_2)
            .build();
    }

    public double getExchangeRate(String from, String to) throws Exception {
        if (from == null || from.trim().isEmpty() || to == null || to.trim().isEmpty()) {
            throw new IllegalArgumentException("Los códigos de moneda no pueden estar vacíos");
        }
        
        from = from.trim().toUpperCase();
        to = to.trim().toUpperCase();
        
        String url = BASE_URL + from + "/" + to;
        
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("User-Agent", "ConversorMonedas/2.0")
            .timeout(Duration.ofSeconds(15))
            .GET()
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() != 200) {
            handleErrorResponse(response.statusCode());
        }
        
        JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
        
        if ("success".equals(json.get("result").getAsString())) {
            return json.get("conversion_rate").getAsDouble();
        } else {
            handleApiError(json);
        }
        return 0;
    }

    private void handleErrorResponse(int statusCode) throws Exception {
        switch (statusCode) {
            case 404: throw new Exception("Moneda no encontrada");
            case 429: throw new Exception("Límite de solicitudes alcanzado");
            case 401: throw new Exception("API key inválida");
            default: throw new Exception("Error HTTP: " + statusCode);
        }
    }

    private void handleApiError(JsonObject json) throws Exception {
        String errorType = json.has("error-type") ? json.get("error-type").getAsString() : "unknown";
        throw new Exception("Error API: " + errorType);
    }
}