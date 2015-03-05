package jin.tree;

import java.math.BigDecimal;
import java.math.BigInteger;

public class JsonBigInt extends JsonNode {

    private BigInteger value;

    public JsonBigInt() {

    }

    public JsonBigInt(BigInteger value) {
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
        return true;
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
        return false;
    }

    @Override
    public BigInteger bigIntValue() {
        return value;
    }

    @Override
    public BigDecimal bigDecimalValue() {
        return null;
    }

    @Override
    public String toJsonString() {
        return (new StringBuilder()).append(value).toString();
    }
}
