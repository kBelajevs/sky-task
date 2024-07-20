package com.sky.kbelajevs.task.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PageDTO<T> {

    public PageDTO(Page page) {
        setPageNumber(page.getNumber());
        setItemsOnPage(page.getNumberOfElements());
        setTotalPages(page.getTotalPages());
        setTotalItems(page.getTotalElements());
    }

    private int pageNumber;
    private int itemsOnPage;
    private int totalPages;
    private long totalItems;
    private List<T> items;
}
