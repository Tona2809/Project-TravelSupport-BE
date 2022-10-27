package com.hcmute.hotel.model.payload.response;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class PagingResponse {
    private Map<String,Integer> pageable;
    private String message;
    private Map<String,Object> content;
    private boolean last;
    private int totalPages;
    private int totalElements;
    private int size;
    private boolean first;
    private int numberOfElements;
    private boolean empty;
    public PagingResponse()
    {
        this.content=new HashMap<>();
        this.pageable= new HashMap<>();
        this.pageable.put("pageNumber",0);
        this.pageable.put("pageSize",0);
    }

    public PagingResponse(String message, Map<String, Object> data, boolean last, int totalPages, int totalElements, int size, boolean first, boolean empty) {

        this.message = message;
        this.content = data;
        this.last = last;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.size = size;
        this.first = first;
        this.empty = empty;
    }
}
