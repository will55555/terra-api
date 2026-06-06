package com.terra.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotionQueryResponse {
    private List<Object> results;
    private String next_cursor;
    private boolean has_more;
}