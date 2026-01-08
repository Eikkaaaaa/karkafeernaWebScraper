package com.eikka.karkafeernaWebScraper.components.macros;

public class Macros {

    private MacroTuple<Float, String> energi;
    private MacroTuple<Float, String> fett;
    private MacroTuple<Float, String> kolhydrater;
    private MacroTuple<Float, String> protein;
    private MacroTuple<Float, String> salt;

    public Macros(){

    }

    public MacroTuple<Float, String> getEnergi() {
        return energi;
    }

    public void setEnergi(MacroTuple<Float, String> energi) {
        this.energi = energi;
    }

    public MacroTuple<Float, String> getFett() {
        return fett;
    }

    public void setFett(MacroTuple<Float, String> fett) {
        this.fett = fett;
    }

    public MacroTuple<Float, String> getKolhydrater() {
        return kolhydrater;
    }

    public void setKolhydrater(MacroTuple<Float, String> kolhydrater) {
        this.kolhydrater = kolhydrater;
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

    public void mapMacros(String unit, MacroTuple<Float, String> tuple){
        switch (unit){
            case "energi":
                this.energi = tuple;
                break;
            case "fett":
                this.fett = tuple;
                break;
            case "kolhydrater":
                this.kolhydrater = tuple;
                break;
            case "protein":
                this.protein = tuple;
                break;
            case "salt":
                this.salt = tuple;
                break;
        }
    }

    @Override
    public String toString() {
        return "Macros{" +
                "energi=" + energi +
                ", fett=" + fett +
                ", kolhydrater=" + kolhydrater +
                ", protein=" + protein +
                ", salt=" + salt +
                '}';
    }
}
