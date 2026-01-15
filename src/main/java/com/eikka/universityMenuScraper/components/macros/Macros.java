package com.eikka.universityMenuScraper.components.macros;

public class Macros {

    private MacroTuple<Float, String> calories;
    private MacroTuple<Float, String> fat;
    private MacroTuple<Float, String> carbs;
    private MacroTuple<Float, String> protein;
    private MacroTuple<Float, String> salt;
    private MacroTuple<Float, String> sugar;

    public Macros(){

    }

    public MacroTuple<Float, String> getCalories() {
        return calories;
    }

    public void setCalories(MacroTuple<Float, String> calories) {
        this.calories = calories;
    }

    public MacroTuple<Float, String> getFat() {
        return fat;
    }

    public void setFat(MacroTuple<Float, String> fat) {
        this.fat = fat;
    }

    public MacroTuple<Float, String> getCarbs() {
        return carbs;
    }

    public void setCarbs(MacroTuple<Float, String> carbs) {
        this.carbs = carbs;
    }

    public MacroTuple<Float, String> getProtein() {
        return protein;
    }

    public void setProtein(MacroTuple<Float, String> protein) {
        this.protein = protein;
    }

    public MacroTuple<Float, String> getSalt() {
        return salt;
    }

    public void setSalt(MacroTuple<Float, String> salt) {
        this.salt = salt;
    }

    public MacroTuple<Float, String> getSugar() {
        return sugar;
    }

    public void setSugar(MacroTuple<Float, String> sugar) {
        this.sugar = sugar;
    }

    public void mapMacros(String unit, MacroTuple<Float, String> tuple){
        switch (unit){
            case "energy":
                this.calories = tuple;
                break;
            case "fat":
                this.fat = tuple;
                break;
            case "carbs":
                this.carbs = tuple;
                break;
            case "protein":
                this.protein = tuple;
                break;
            case "salt":
                this.salt = tuple;
                break;
            case "sugars":
                this.sugar = tuple;
                break;
        }
    }

    @Override
    public String toString() {
        return "Macros{" +
                "calories=" + calories +
                ", fat=" + fat +
                ", carbs=" + carbs +
                ", protein=" + protein +
                ", salt=" + salt +
                ", sugar=" + sugar +
                '}';
    }
}
