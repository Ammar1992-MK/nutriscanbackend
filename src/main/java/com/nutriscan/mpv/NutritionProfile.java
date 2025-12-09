package com.nutriscan.mpv;

import jakarta.persistence.*;

@Entity
@Table(name = "nutrition_profile")
public class NutritionProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String goal;
    private String allergy;
    private String diet;
    private String otherPreferences;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public String getAllergy() {
        return allergy;
    }

    public void setAllergy(String allergy) {
        this.allergy = allergy;
    }

    public String getDiet() {
        return diet;
    }

    public void setDiet(String diet) {
        this.diet = diet;
    }

    public String getOtherPreferences() {
        return otherPreferences;
    }

    public void setOtherPreferences(String otherPreferences) {
        this.otherPreferences = otherPreferences;
    }

}
