package com.kr.pineco;

public class Fruit {
    private String fruitUID;
    private String name;
    private String description;
    private String cost;
    private String image;
    private String validity;
    private String quantity=null;

    public Fruit(String fruitUID,String name,String description, String cost,String image,String validity){
        this.fruitUID=fruitUID;
        this.name=name;
        this.description=description;
        this.cost=cost;
        this.image=image;
        this.validity=validity;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getFruitUID() {
        return fruitUID;
    }

    public void setFruitUID(String fruitUID) {
        this.fruitUID = fruitUID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getValidity() {
        return validity;
    }

    public void setValidity(String validity) {
        this.validity = validity;
    }
}
