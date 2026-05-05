package com.example.productcatalog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductWithReviewsDTO {

    private String name;
    private String description;
    private Double price;
    private Integer quantity;
    private List<ReviewDTO> reviews;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ReviewDTO {
        private String comment;
    }
}
