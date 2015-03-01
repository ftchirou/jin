package com.github.ftchirou.yajl.deserializer;

import com.github.ftchirou.yajl.io.JsonReader;
import com.github.ftchirou.yajl.lexer.JsonToken;
import com.github.ftchirou.yajl.lexer.TokenType;
import com.github.ftchirou.yajl.lexer.UnrecognizedTokenException;
import com.github.ftchirou.yajl.parser.JsonParsingException;

import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;

public class JsonBaseDeserializer {

    private JsonToken token;

    public <T> T deserialize(JsonReader reader, Class<T> cls) throws IOException, JsonParsingException {
        return cls.cast(deserializeValue(reader, cls));
    }

    private <T> T deserializeObject(JsonReader reader, Class<T> cls) throws IOException, JsonParsingException {
        expect(TokenType.OBJECT_START, reader);

        if (accept(TokenType.OBJECT_END)) {
            expect(TokenType.OBJECT_END, reader);
        }

        try {
            T object = cls.newInstance();

            deserializeObjectFields(object, reader, cls);

            expect(TokenType.OBJECT_END, reader);

            return object;

        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();

            return null;
        }
    }

    private void deserializeObjectFields(Object object, JsonReader reader, Class<?> cls) throws IOException, JsonParsingException {
        JsonToken fieldNameToken = expect(TokenType.STRING, reader);
        expect(TokenType.COMMA, reader);

        try {
            Field field = cls.getField(fieldNameToken.getValue());
            
            Object value = deserializeValue(reader, field.getType());

            field.set(object, value);

            if (accept(TokenType.OBJECT_END)) {
                expect(TokenType.OBJECT_END, reader);

                return;
            }

            deserializeObjectFields(object, reader, cls);

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private Object[] deserializeArray(JsonReader reader, Class<?> componentType) throws IOException, JsonParsingException {
        expect(TokenType.ARRAY_START, reader);

        ArrayList<Object> list = new ArrayList<>();

        while (!accept(TokenType.ARRAY_END)) {
            list.add(deserializeValue(reader, componentType));

            if (accept(TokenType.ARRAY_END)) {
                break;
            }

            expect(TokenType.COMMA, reader);

            toNextToken(reader);
        }

        expect(TokenType.ARRAY_END, reader);

        return list.toArray();
    }

    private Collection deserializeCollection(JsonReader reader, Class<?> componentType) throws IOException, JsonParsingException {
        return null;
    }

    private Object deserializeValue(JsonReader reader, Class<?> valueType) throws IOException, JsonParsingException {
        if (valueType.getName().equals("java.lang.String")) {
            return valueType.cast(deserializeString(reader));

        } else if (valueType.getName().equals("java.lang.Integer")) {
            return valueType.cast(deserializeInteger(reader));

        } else if (valueType.getName().equals("java.lang.Boolean")) {
            return valueType.cast(deserializeBoolean(reader));

        } else if (valueType.getName().equals("java.lang.Long")) {
            return valueType.cast(deserializeLong(reader));

        } else if (valueType.getName().equals("java.lang.Double")) {
            return valueType.cast(deserializeDouble(reader));

        } else if (valueType.getName().equals("java.lang.Float")) {
            return valueType.cast(deserializeFloat(reader));

        } else if (valueType.getName().equals("java.math.BigInteger")) {
            return valueType.cast(deserializeBigInteger(reader));

        } else if (valueType.getName().equals("java.math.BigDecimal")) {
            return valueType.cast(deserializeBigDecimal(reader));

        } else if (valueType.isArray()) {
            return deserializeArray(reader, valueType.getComponentType());

        } else if (Collection.class.isAssignableFrom(valueType)) {
            return valueType.cast(deserializeCollection(reader, valueType));

        } else {
            return deserializeObject(reader, valueType);
        }
    }

    private String deserializeString(JsonReader reader) throws IOException, JsonParsingException {
        expect(TokenType.STRING, reader);

        return token.getValue();
    }

    private Integer deserializeInteger(JsonReader reader) throws IOException, JsonParsingException {
        expect(TokenType.NUMBER, reader);

        return Integer.parseInt(token.getValue());
    }

    private Long deserializeLong(JsonReader reader) throws IOException, JsonParsingException {
        expect(TokenType.NUMBER, reader);

        return Long.parseLong(token.getValue());
    }

    private Double deserializeDouble(JsonReader reader) throws IOException, JsonParsingException {
        expect(TokenType.NUMBER, reader);

        return Double.parseDouble(token.getValue());
    }

    private Float deserializeFloat(JsonReader reader) throws IOException, JsonParsingException {
        expect(TokenType.NUMBER, reader);

        return Float.parseFloat(token.getValue());
    }

    private BigInteger deserializeBigInteger(JsonReader reader) throws IOException, JsonParsingException {
        expect(TokenType.NUMBER, reader);

        return new BigInteger(token.getValue());
    }

    private BigDecimal deserializeBigDecimal(JsonReader reader) throws IOException, JsonParsingException {
        expect(TokenType.NUMBER, reader);

        return new BigDecimal(token.getValue());
    }

    private Boolean deserializeBoolean(JsonReader reader) throws IOException, JsonParsingException {
        if (!accept(TokenType.TRUE) || !accept(TokenType.FALSE)) {
            throw new JsonParsingException("expected 'true' or 'false' at position " + token.getPosition());
        }

        try {
            token = reader.nextToken();

            return Boolean.parseBoolean(token.getValue());

        } catch (UnrecognizedTokenException e) {
            throw new JsonParsingException(e);
        }
    }

    private JsonToken toNextToken(JsonReader reader) throws IOException, JsonParsingException {
        try {
            return reader.nextToken();

        } catch (UnrecognizedTokenException e) {
            throw new JsonParsingException(e);
        }
    }

    private JsonToken expect(TokenType type, JsonReader reader) throws IOException, JsonParsingException {
        if (token.getType() == TokenType.END_OF_STREAM) {
            throw new JsonParsingException("unexpected end of input.");
        }

        if (token.getType() != type) {
            throw new JsonParsingException("expected " + type.toString() + " at position " + token.getPosition());
        }

        JsonToken expected = token;

        try {
            token = reader.nextToken();

            return expected;

        } catch (UnrecognizedTokenException e) {
            throw new JsonParsingException(e);
        }
    }

    private boolean accept(TokenType type) {
        return token.getType() != TokenType.END_OF_STREAM && token.getType() == type;
    }
}
