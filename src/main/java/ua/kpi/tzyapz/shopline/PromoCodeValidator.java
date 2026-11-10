package ua.kpi.tzyapz.shopline;

public class PromoCodeValidator {

    public boolean isValid(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Промокод не може бути порожнім");
        }
        
        if (!code.matches("^[A-Z0-9]{4,10}$")) {
            throw new IllegalArgumentException("Некоректний формат промокоду");
        }
        
        return true;
    }
}
