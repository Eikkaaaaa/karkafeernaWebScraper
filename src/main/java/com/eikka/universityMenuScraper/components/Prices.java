package com.eikka.universityMenuScraper.components;

public class Prices {

    private float students;
    private float researcher_students;
    private float staff;
    private float others;

    public Prices(){}

    public void setStuderande(float students) {
        this.students = students;
    }

    public void setForskarstuderande(float researcher_students) {
        this.researcher_students = researcher_students;
    }

    public void setPersonal(float staff) {
        this.staff = staff;
    }

    public void setAndra(float others) {
        this.others = others;
    }

    public float getStuderande() {
        return students;
    }

    public float getForskarstuderande() {
        return researcher_students;
    }

    public float getPersonal() {
        return staff;
    }

    public float getAndra() {
        return others;
    }

    public void mapPrices(String clientele, float price){
        switch (clientele){
            case "students":
                this.students = price;
                break;
            case "researcher_students":
                this.researcher_students = price;
                break;
            case "staff":
                this.staff = price;
                break;
            case "others":
                this.others = price;
                break;
        }
    }

    @Override
    public String toString() {
        return "Prices{" +
                "students=" + students +
                ", researcher_students=" + researcher_students +
                ", personal=" + staff +
                ", others=" + others +
                '}';
    }
}
