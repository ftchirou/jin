package jin.tree;

import java.math.BigDecimal;
import java.math.BigInteger;

public abstract class JsonNode {

    public abstract boolean isObject();

    public abstract boolean isArray();

    public abstract boolean isNull();

    public abstract boolean isString();

    public abstract boolean isBoolean();

    public abstract boolean isInt();

    public abstract boolean isLong();

    public abstract boolean isBigInt();

    public abstract boolean isDecimal();

    public abstract String stringValue();

    public abstract boolean booleanValue();

    public abstract int intValue();

    public abstract long longValue();

    public abstract double doubleValue();

    public abstract BigInteger bigIntValue();

    public abstract BigDecimal bigDecimalValue();

    public abstract String toJsonString();
}
