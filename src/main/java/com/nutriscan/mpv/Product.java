package com.nutriscan.mpv;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String barcode;
    private String name;
    private Double carbs;
    private Double energy;
    private Double fat;
    private Double protein;
    private Integer novaGroup;
    private Double sugar;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getCarbs() {
        return carbs;
    }

    public void setCarbs(Double carbs) {
        this.carbs = carbs;
    }

    public Double getEnergy() {
        return energy;
    }

    public void setEnergy(Double energy) {
        this.energy = energy;
    }

    public Double getFat() {
        return fat;
    }

    public void setFat(Double fat) {
        this.fat = fat;
    }

    public Double getProtein() {
        return protein;
    }

    public void setProtein(Double protein) {
        this.protein = protein;
    }

    public Integer getNovaGroup() {
        return novaGroup;
    }

    public void setNovaGroup(Integer novagroup) {
        this.novaGroup = novagroup;
    }

    public Double getSugar() {
        return sugar;
    }

    public void setSugar(Double sugar) {
        this.sugar = sugar;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof Product product)) return false;

        return Objects.equals(id, product.id) && Objects.equals(barcode, product.barcode) && Objects.equals(name, product.name) && Objects.equals(carbs, product.carbs) && Objects.equals(energy, product.energy) && Objects.equals(fat, product.fat) && Objects.equals(protein, product.protein) && Objects.equals(novaGroup, product.novaGroup) && Objects.equals(sugar, product.sugar);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(id);
        result = 31 * result + Objects.hashCode(barcode);
        result = 31 * result + Objects.hashCode(name);
        result = 31 * result + Objects.hashCode(carbs);
        result = 31 * result + Objects.hashCode(energy);
        result = 31 * result + Objects.hashCode(fat);
        result = 31 * result + Objects.hashCode(protein);
        result = 31 * result + Objects.hashCode(novaGroup);
        result = 31 * result + Objects.hashCode(sugar);
        return result;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", barcode='" + barcode + '\'' +
                ", name='" + name + '\'' +
                ", carbs=" + carbs +
                ", energy=" + energy +
                ", fat=" + fat +
                ", protein=" + protein +
                ", novagroup=" + novaGroup +
                ", sugar=" + sugar +
                '}';
    }
}
