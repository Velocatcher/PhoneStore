package com.phonestore.catalog.domain;


import jakarta.persistence.*;
import java.math.BigDecimal;


@Entity
@Table(name = "products")
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false, unique=true)
    private String sku;
    @Column(nullable=false)
    private String name;
    private String brand;
    private BigDecimal price;
    private String currency;
    @Column(length=4000)
    private String description;


    // getters/setters omitted for brevity
    public Long getId() {return id;} public void setId(Long id){this.id=id;}
    public String getSku(){return sku;} public void setSku(String sku){this.sku=sku;}
    public String getName(){return name;} public void setName(String name){this.name=name;}
    public String getBrand(){return brand;} public void setBrand(String brand){this.brand=brand;}
    public BigDecimal getPrice(){return price;} public void setPrice(BigDecimal price){this.price=price;}
    public String getCurrency(){return currency;} public void setCurrency(String currency){this.currency=currency;}
    public String getDescription(){return description;} public void setDescription(String description){this.description=description;}
}