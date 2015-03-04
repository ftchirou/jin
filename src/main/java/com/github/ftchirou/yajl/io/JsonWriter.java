package com.github.ftchirou.yajl.io;

import com.github.ftchirou.yajl.json.JsonNode;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;

public class JsonWriter extends OutputStreamWriter {

    final String OBJECT_START = "{";
    final String OBJECT_END = "}";
    final String ARRAY_START = "[";
    final String ARRAY_END = "]";
    final String COLON = ":";
    final String COMMA = ",";
    final String NULL = "null";

    public JsonWriter(OutputStream out) throws UnsupportedEncodingException {
        super(out, Charset.forName("UTF-8"));
    }

    public JsonWriter writeObjectStart() throws IOException {
        return writeToken(OBJECT_START);
    }

    public JsonWriter writeObjectEnd() throws IOException {
        return writeToken(OBJECT_END);
    }

    public JsonWriter writeArrayStart() throws IOException {
        return writeToken(ARRAY_START);
    }

    public JsonWriter writeArrayEnd() throws IOException {
        return writeToken(ARRAY_END);
    }

    public JsonWriter writeFieldName(String name) throws IOException {
        return writeToken(quote(name) + COLON);
    }

    public JsonWriter writeField(String name, String value) throws IOException {
        return writeToken(quote(name) + COLON + quote(value));
    }

    public JsonWriter writeField(String name, int value) throws IOException {
        return writeToken(quote(name) + COLON + String.valueOf(value));
    }

    public JsonWriter writeField(String name, long value) throws IOException {
        return writeToken(quote(name) + COLON + String.valueOf(value));
    }

    public JsonWriter writeField(String name, double value) throws IOException {
        return writeToken(quote(name) + COLON + String.valueOf(value));
    }

    public JsonWriter writeField(String name, float value) throws IOException {
        return writeToken(quote(name) + COLON + String.valueOf(value));
    }

    public JsonWriter writeField(String name, boolean value) throws IOException {
        return writeToken(quote(name) + COLON + String.valueOf(value));
    }

    public JsonWriter writeField(String name, BigInteger value) throws IOException {
        return writeToken(quote(name) + COLON + value.toString());
    }

    public JsonWriter writeField(String name, BigDecimal value) throws IOException {
        return writeToken(quote(name) + COLON + value.toString());
    }

    public JsonWriter writeString(String str) throws IOException {
        return writeToken(quote(str));
    }

    public JsonWriter writeInt(int integer) throws IOException {
        return writeToken(String.valueOf(integer));
    }

    public JsonWriter writeLong(long l) throws IOException {
        return writeToken(String.valueOf(l));
    }

    public JsonWriter writeDouble(double d) throws IOException {
        return writeToken(String.valueOf(d));
    }

    public JsonWriter writeFloat(float f) throws IOException {
        return writeToken(String.valueOf(f));
    }

    public JsonWriter writeBoolean(boolean b) throws IOException {
        return writeToken(String.valueOf(b));
    }

    public JsonWriter writeBigInteger(BigInteger bi) throws IOException {
        return writeToken(bi.toString());
    }

    public JsonWriter writeBigDecimal(BigDecimal bd) throws IOException {
        return writeToken(bd.toString());
    }

    public JsonWriter writeNull() throws IOException {
        return writeToken(NULL);
    }

    public JsonWriter writeComma() throws IOException {
        return writeToken(COMMA);
    }

    public JsonWriter writeRaw(String raw) throws IOException {
        return writeToken(raw);
    }

    public JsonWriter writeJsonNode(JsonNode node) throws IOException {
        return writeToken(node.toJsonString());
    }

    private JsonWriter writeToken(String token) throws IOException {
        write(token, 0, token.length());

        return this;
    }

    private String quote(String str) {
        return "\"" + str + "\"";
    }
}
