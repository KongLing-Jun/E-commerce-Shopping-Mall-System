package com.thinking.backendmall.common;

import java.util.List;

public class PageResult<T> {
    private List<T> content;
    private long totalElements;
    private long totalPages;
    private int number;
    private int size;
    private boolean last;

    // 功能：处理分页result
    public PageResult() {
    }

    // 功能：处理分页result
    public PageResult(List<T> content, long totalElements, long totalPages, int number, int size, boolean last) {
        this.content = content;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.number = number;
        this.size = size;
        this.last = last;
    }

    // 功能：获取content
    public List<T> getContent() {
        return content;
    }

    // 功能：设置content
    public void setContent(List<T> content) {
        this.content = content;
    }

    // 功能：获取总计elements
    public long getTotalElements() {
        return totalElements;
    }

    // 功能：设置总计elements
    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    // 功能：获取总计pages
    public long getTotalPages() {
        return totalPages;
    }

    // 功能：设置总计pages
    public void setTotalPages(long totalPages) {
        this.totalPages = totalPages;
    }

    // 功能：获取number
    public int getNumber() {
        return number;
    }

    // 功能：设置number
    public void setNumber(int number) {
        this.number = number;
    }

    // 功能：获取分页大小
    public int getSize() {
        return size;
    }

    // 功能：设置分页大小
    public void setSize(int size) {
        this.size = size;
    }

    // 功能：判断last
    public boolean isLast() {
        return last;
    }

    // 功能：设置last
    public void setLast(boolean last) {
        this.last = last;
    }
}
