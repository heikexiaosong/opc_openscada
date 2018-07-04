package com.gavel.opcclient;

public class DataPointItem {

    private String itemId;

    private long timestamp;

    private Object value;

    public DataPointItem(String itemId, long timestamp, Object value) {
        this.itemId = itemId;
        this.timestamp = timestamp;
        this.value = value;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
