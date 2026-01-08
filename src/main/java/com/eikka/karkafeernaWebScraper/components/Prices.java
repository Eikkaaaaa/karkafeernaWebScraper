package com.eikka.karkafeernaWebScraper.components;

public class Prices {

    private float studerande;
    private float forskarstuderande;
    private float personal;
    private float andra;

    public Prices(){}

    public void setStuderande(float studerande) {
        this.studerande = studerande;
    }

    public void setForskarstuderande(float forskarstuderande) {
        this.forskarstuderande = forskarstuderande;
    }

    public void setPersonal(float personal) {
        this.personal = personal;
    }

    public void setAndra(float andra) {
        this.andra = andra;
    }

    public float getStuderande() {
        return studerande;
    }

    public float getForskarstuderande() {
        return forskarstuderande;
    }

    public float getPersonal() {
        return personal;
    }

    public float getAndra() {
        return andra;
    }

    public void mapPrices(String clientele, float price){
        switch (clientele){
            case "studerande":
                this.studerande = price;
                break;
            case "forskarstuderande":
                this.forskarstuderande = price;
                break;
            case "personal":
                this.personal = price;
                break;
            case "andra":
                this.andra = price;
                break;
        }
    }

    @Override
    public String toString() {
        return "Prices{" +
                "studerande=" + studerande +
                ", forskarstuderande=" + forskarstuderande +
                ", personal=" + personal +
                ", andra=" + andra +
                '}';
    }
}
