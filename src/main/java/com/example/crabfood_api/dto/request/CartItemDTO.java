package com.example.crabfood_api.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO {

    private Long id;

    private Long foodId;

    private String foodName;

    private String imageUrl;

    private double price;

    private int quantity;

    private Long vendorId;

    @JsonProperty("selectedOptions")
    private List<OptionChoiceDTO> selectedOptions;

    @JsonProperty("lastUpdated")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMM dd, yyyy hh:mm:ss a", locale = "en_US")
    private Date lastUpdated;


    @JsonProperty("isSynced")
    private boolean isSynced;
}
