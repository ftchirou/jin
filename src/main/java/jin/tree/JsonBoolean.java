package jin.tree;

import java.math.BigDecimal;
import java.math.BigInteger;

public class JsonBoolean extends JsonNode {

    private boolean value;

    public JsonBoolean() {

    }

    public JsonBoolean(boolean value) {
        this.value = value;
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
        return true;
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
    public boolean booleanValue() {
        return value;
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
    public String toJsonString() {
        return value ? "true" : "false";
    }
}
