package com.example.crabfood_api.model.entity;

import com.example.crabfood_api.dto.request.OptionChoiceDTO;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;


import java.util.Date;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@AllArgsConstructor
@Entity
@Table(name = "cart_items")
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "food_id", nullable = false)
    private Long foodId;

    @Column(name = "food_name", nullable = false)
    private String foodName;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "vendor_id", nullable = false)
    private Long vendorId;

    @Column(name = "selected_options", columnDefinition = "json")
    @Type(JsonType.class)
    private List<OptionChoiceDTO> selectedOptions;

    @Column(name = "last_updated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdated;

    // Constructors
    public CartItem() {
        this.lastUpdated = new Date();
    }
}