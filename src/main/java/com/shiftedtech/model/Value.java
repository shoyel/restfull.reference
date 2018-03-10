
package com.shiftedtech.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Value {

    @SerializedName("active")
    @Expose
    private Boolean active;
    @SerializedName("valueKey")
    @Expose
    private String valueKey;
    @SerializedName("quantity")
    @Expose
    private Integer quantity;
    @SerializedName("minValue")
    @Expose
    private Double minValue;
    @SerializedName("maxValue")
    @Expose
    private Double maxValue;
    @SerializedName("value")
    @Expose
    private String value;

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getValueKey() {
        return valueKey;
    }

    public void setValueKey(String valueKey) {
        this.valueKey = valueKey;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getMinValue() {
        return minValue;
    }

    public void setMinValue(Double minValue) {
        this.minValue = minValue;
    }

    public Double getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Double maxValue) {
        this.maxValue = maxValue;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
