package com.eikka.karkafeernaWebScraper.components.macros;

public class MacroTuple <Float, String>{

    private Float amount;
    private String quantity;

    public MacroTuple(Float amount, String quantity) {
        this.amount = amount;
        this.quantity = quantity;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    @Override
    public java.lang.String toString() {
        return "MacroTuple{" +
                "amount=" + amount +
                ", quantity=" + quantity +
                '}';
    }
}
