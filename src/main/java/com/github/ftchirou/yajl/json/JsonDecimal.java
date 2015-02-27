package com.github.ftchirou.yajl.json;

import java.math.BigDecimal;
import java.math.BigInteger;

public class JsonDecimal extends JsonNode {

    private BigDecimal value;

    public JsonDecimal() {

    }

    public JsonDecimal(BigDecimal value) {
        this.value = value;
    }

    public JsonDecimal(double value) {
        this.value = new BigDecimal(value);
    }

    @Override
    public boolean isObject() {
        return false;
    }

    @Override
    public boolean isArray() {
        return false;
    }

    @Override
    public boolean isNull() {
        return false;
    }

    @Override
    public boolean isString() {
        return false;
    }

    @Override
    public boolean isBoolean() {
        return false;
    }

    @Override
    public boolean isInt() {
        return false;
    }

    @Override
    public boolean isLong() {
        return false;
    }

    @Override
    public boolean isBigInt() {
        return false;
    }

    @Override
    public boolean isDecimal() {
        return true;
    }

    @Override
    public String stringValue() {
        return null;
    }

    @Override
    public int intValue() {
        return 0;
    }

    @Override
    public long longValue() {
        return 0L;
    }

    @Override
    public double doubleValue() {
        return value.doubleValue();
    }

    @Override
    public boolean booleanValue() {
        return false;
    }

    @Override
    public BigInteger bigIntValue() {
        return null;
    }

    @Override
    public BigDecimal bigDecimalValue() {
        return value;
    }

    @Override
    public String toJsonString() {
        return (new StringBuilder()).append(value).toString();
    }
}
