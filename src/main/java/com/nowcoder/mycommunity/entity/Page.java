package com.nowcoder.mycommunity.entity;

// encapsulate function relate to paging
public class Page {
    // current page number
    private int current = 1;

    // max page number
    private int limit = 10;

    // data total number, use to calculate pa the number of pages
    private int rows;

    // search path (used to reuse pagination links）
    private String path;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        if(current > 1)
            this.current = current;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if(limit >= 1 && limit <= 100)
            this.limit = limit;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        if(limit >= 1 && limit <= 100)
            this.rows = rows;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * get the start line of the current page
     */
    public int getOffset(){
        return limit * (current - 1);
    }

    /**
     * get the total number of pages
     */
    public int getTotal(){
        if(rows % limit == 0) {
            return rows / limit;
        }else{
            return rows/limit + 1;
        }
    }

    /**
     * get the start page
     */
    public int getFrom(){
        int from = current - 2;
        return from < 1 ? 1 : from;
    }

    /**
     * get the stop page
     */
    public int getTo(){
        int to = current + 2;
        int total = getTotal();
        return to > total ? total : to;
    }
}
