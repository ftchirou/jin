package com.github.ftchirou.yajl.json;

import java.math.BigDecimal;
import java.math.BigInteger;

public class JsonNull extends JsonNode {

    public JsonNull() {

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
        return true;
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
        return false;
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
        return 0.0;
    }

    @Override
    public BigInteger bigIntValue() {
        return null;
    }

    @Override
    public BigDecimal bigDecimalValue() {
        return null;
    }

    @Override
    public boolean booleanValue() {
        return false;
    }

    @Override
    public String toJsonString() {
        return "null";
    }
}
