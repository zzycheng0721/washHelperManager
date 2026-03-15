package com.washhelper.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
public class PageResponse<T> implements Serializable {
    private List<T> items;
    private int page;
    private int pageSize;
    private long total;
    
    public PageResponse(List<T> items, int page, int pageSize, long total) {
        this.items = items;
        this.page = page;
        this.pageSize = pageSize;
        this.total = total;
    }
}
