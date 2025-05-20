package ever;

import java.util.Scanner;

public class ConversorMonedas {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            ApiService api = new ApiService();
            CurrencyFormatter formatter = new CurrencyFormatter();
            
            System.out.println("\n=== CONVERSOR DE MONEDAS ===");
            
            while (true) {
                System.out.println("\n1. Convertir");
                System.out.println("2. Salir");
                System.out.print("Seleccione: ");
                
                int opcion = scanner.nextInt();
                scanner.nextLine(); // Limpiar buffer
                
                if (opcion == 2) break;
                
                System.out.print("Moneda origen (ej: USD): ");
                String from = scanner.nextLine().toUpperCase();
                
                System.out.print("Moneda destino (ej: EUR): ");
                String to = scanner.nextLine().toUpperCase();
                
                System.out.print("Cantidad a convertir: ");
                double amount = scanner.nextDouble();
                
                try {
                    double rate = api.getExchangeRate(from, to);
                    double result = amount * rate;
                    
                    System.out.println("\n" + formatter.format(amount, from) +
                            " = " + formatter.format(result, to));
                } catch (Exception e) {
                    System.out.println("❌ Error: " + e.getMessage());
                }
            }
        }
    }
}