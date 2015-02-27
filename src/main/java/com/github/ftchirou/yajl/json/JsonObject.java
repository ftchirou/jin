package com.github.ftchirou.yajl.json;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class JsonObject extends JsonNode {

    private HashMap<String, JsonNode> fields;

    public JsonObject() {
        this.fields = new LinkedHashMap<String, JsonNode>();
    }

    public JsonObject add(String key, String value) {
        if (value != null) {
            return add(key, new JsonString(value));
        }

        return add(key, new JsonNull());
    }

    public JsonObject add(String key, boolean value) {
        return add(key, new JsonBoolean(value));
    }

    public JsonObject add(String key, int value) {
        return add(key, new JsonInt(value));
    }

    public JsonObject add(String key, long value) {
        return add(key, new JsonLong(value));
    }

    public JsonObject add(String key, double value) {
        return add(key, new JsonDecimal(value));
    }

    public JsonObject add(String key, BigInteger value) {
        return add(key, new JsonBigInt(value));
    }

    public JsonObject add(String key, BigDecimal value) {
        return add(key, new JsonDecimal(value));
    }

    public JsonObject add(String key, JsonNode value) {
        if (this.fields.containsKey(key)) {
            this.fields.remove(key);
        }

        this.fields.put(key, value);

        return this;
    }

    public JsonObject addAll(HashMap<String, JsonNode> pairs) {
        fields.putAll(pairs);

        return this;
    }

    public boolean containsKey(String key) {
        return fields.containsKey(key);
    }

    public JsonNode get(String key) {
        return this.fields.get(key);
    }

    public JsonObject remove(String key) {
        this.fields.remove(key);

        return this;
    }

    public int size() {
        return fields.size();
    }

    @Override
    public boolean isObject() {
        return true;
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
        return null;
    }

    @Override
    public BigDecimal bigDecimalValue() {
        return null;
    }

    @Override
    public String toJsonString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");

        if (fields.size() > 0) {
            for (Map.Entry<String, JsonNode> entry : fields.entrySet()) {
                builder.append("\"" + entry.getKey() + "\"");
                builder.append(":");
                builder.append(entry.getValue().toJsonString());
                builder.append(",");
            }

            builder.deleteCharAt(builder.length() - 1);
        }

        builder.append("}");

        return builder.toString();
    }
}
