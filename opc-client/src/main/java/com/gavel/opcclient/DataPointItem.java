package com.gavel.opcclient;

import com.google.common.base.MoreObjects;

import java.io.Serializable;

public class DataPointItem implements Serializable {

    private static final long serialVersionUID = 123L;

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

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("itemId", itemId)
                .add("timestamp", timestamp)
                .add("value", value)
                .toString();
    }
}
