package com.nutriscan.mpv;

import jakarta.persistence.*;

@Entity
@Table(name = "user_product")
public class UserProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private int score;

    public UserProduct() {}

    public UserProduct(User user, Product product, int score) {
        this.user = user;
        this.product = product;
        this.score = score;
    }

    public Long getId() { return id; }

    public User getUser() { return user; }

    public void setUser(User user) { this.user = user; }

    public Product getProduct() { return product; }

    public void setProduct(Product product) { this.product = product; }

    public int getScore() { return score; }

    public void setScore(int score) { this.score = score; }
}

