package com.example.crabfood_api.dto.response;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageResponse<T> {
    private List<T> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean last;

    // Constructor từ Page<T>
    public PageResponse(Page<T> page) {
        this.content = page.getContent();
        this.pageNumber = page.getNumber();
        this.pageSize = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.last = page.isLast();
    }

    public PageResponse() {
        this.content = List.of();
        this.pageNumber = 0;
        this.pageSize = 0;
        this.totalElements = 0;
        this.totalPages = 0;
        this.last = true;
    }
}