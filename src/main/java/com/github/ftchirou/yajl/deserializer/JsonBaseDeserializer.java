package com.github.ftchirou.yajl.deserializer;

import com.github.ftchirou.yajl.io.JsonReader;
import com.github.ftchirou.yajl.lexer.JsonToken;
import com.github.ftchirou.yajl.lexer.TokenType;
import com.github.ftchirou.yajl.lexer.UnrecognizedTokenException;
import com.github.ftchirou.yajl.parser.JsonParsingException;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class JsonBaseDeserializer {

    private JsonToken token;

    public <T> T deserialize(JsonReader reader, Class<T> cls) throws IOException, JsonParsingException {
        token = toNextToken(reader);

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

            return object;

        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();

            return null;
        }
    }

    private void deserializeObjectFields(Object object, JsonReader reader, Class<?> cls) throws IOException, JsonParsingException {
        JsonToken fieldNameToken = expect(TokenType.STRING, reader);
        expect(TokenType.COLON, reader);

        try {
            Field field = cls.getDeclaredField(fieldNameToken.getValue());
            field.setAccessible(true);

            if (Collection.class.isAssignableFrom(field.getType())) {
                deserializeCollection((Collection) field.get(object), reader, getCollectionTypeParameter(field));
                
            } else if (Map.class.isAssignableFrom(field.getType())) {
                List<Class<?>> mapTypeParameters = getMapTypeParameters(field);

                deserializeMap((Map) field.get(object), reader, mapTypeParameters.get(0), mapTypeParameters.get(1));

            } else {
                Object value = deserializeValue(reader, field.getType());

                field.set(object, value);
            }

            if (accept(TokenType.OBJECT_END)) {
                expect(TokenType.OBJECT_END, reader);

                return;
            }

            expect(TokenType.COMMA, reader);

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
        }

        expect(TokenType.ARRAY_END, reader);

        return list.toArray();
    }

    @SuppressWarnings("unchecked")
    private void deserializeCollection(Collection collection, JsonReader reader, Class<?> componentType) throws IOException, JsonParsingException {
        expect(TokenType.ARRAY_START, reader);

        while (!accept(TokenType.ARRAY_END)) {
            collection.add(deserializeValue(reader, componentType));

            if (accept(TokenType.ARRAY_END)) {
                break;
            }

            expect(TokenType.COMMA, reader);
        }

        expect(TokenType.ARRAY_END, reader);
    }

    private void deserializeMap(Map map, JsonReader reader, Class<?> keyClass, Class<?> valueClass) throws IOException, JsonParsingException {
        expect(TokenType.OBJECT_START, reader);

        if (accept(TokenType.OBJECT_END)) {
            expect(TokenType.OBJECT_END, reader);
        }

        deserializeMapEntries(map, reader, keyClass, valueClass);
    }

    @SuppressWarnings("unchecked")
    private void deserializeMapEntries(Map map, JsonReader reader, Class<?> keyClass, Class<?> valueClass) throws IOException, JsonParsingException {
        Object key = deserializeValue(reader, keyClass);
        expect(TokenType.COLON, reader);
        Object value = deserializeValue(reader, valueClass);

        map.put(key, value);

        if (accept(TokenType.OBJECT_END)) {
            expect(TokenType.OBJECT_END, reader);

            return;
        }

        expect(TokenType.COMMA, reader);

        deserializeMapEntries(map, reader, keyClass, valueClass);
    }

    private Object deserializeValue(JsonReader reader, Class<?> valueType) throws IOException, JsonParsingException {
        String type = valueType.getName();

        if (type.equals("java.lang.String")) {
            return deserializeString(reader);

        } else if (type.equals("int") || type.equals("java.lang.Integer")) {
            return deserializeInteger(reader);

        } else if (type.equals("boolean") || type.equals("java.lang.Boolean")) {
            return deserializeBoolean(reader);

        } else if (type.equals("long") || type.equals("java.lang.Long")) {
            return deserializeLong(reader);

        } else if (type.equals("double") || type.equals("java.lang.Double")) {
            return deserializeDouble(reader);

        } else if (type.equals("float") || type.equals("java.lang.Float")) {
            return deserializeFloat(reader);

        } else if (valueType.getName().equals("java.math.BigInteger")) {
            return deserializeBigInteger(reader);

        } else if (valueType.getName().equals("java.math.BigDecimal")) {
            return deserializeBigDecimal(reader);

        } else if (valueType.isArray()) {
            return deserializeArray(reader, valueType.getComponentType());

        } else {
            return deserializeObject(reader, valueType);
        }
    }

    private String deserializeString(JsonReader reader) throws IOException, JsonParsingException {
        JsonToken expected = expect(TokenType.STRING, reader);

        return expected.getValue();
    }

    private Integer deserializeInteger(JsonReader reader) throws IOException, JsonParsingException {
        JsonToken expected = expect(TokenType.NUMBER, reader);

        return Integer.parseInt(expected.getValue());
    }

    private Long deserializeLong(JsonReader reader) throws IOException, JsonParsingException {
        JsonToken expected = expect(TokenType.NUMBER, reader);

        return Long.parseLong(expected.getValue());
    }

    private Double deserializeDouble(JsonReader reader) throws IOException, JsonParsingException {
        JsonToken expected = expect(TokenType.NUMBER, reader);

        return Double.parseDouble(expected.getValue());
    }

    private Float deserializeFloat(JsonReader reader) throws IOException, JsonParsingException {
        JsonToken expected = expect(TokenType.NUMBER, reader);

        return Float.parseFloat(expected.getValue());
    }

    private BigInteger deserializeBigInteger(JsonReader reader) throws IOException, JsonParsingException {
        JsonToken expected = expect(TokenType.NUMBER, reader);

        return new BigInteger(expected.getValue());
    }

    private BigDecimal deserializeBigDecimal(JsonReader reader) throws IOException, JsonParsingException {
        JsonToken expected = expect(TokenType.NUMBER, reader);

        return new BigDecimal(expected.getValue());
    }

    private Boolean deserializeBoolean(JsonReader reader) throws IOException, JsonParsingException {
        if (!(accept(TokenType.TRUE) || accept(TokenType.FALSE))) {
            throw new JsonParsingException("expected 'true' or 'false' at position " + token.getPosition());
        }

        JsonToken booleanToken = token;

        token = toNextToken(reader);

        return Boolean.parseBoolean(booleanToken.getValue());
    }

    private Class<?> getCollectionTypeParameter(Field field) {
        return getFieldTypeParameters(field).get(0);
    }

    private List<Class<?>> getMapTypeParameters(Field field) {
        return getFieldTypeParameters(field);
    }

    private List<Class<?>> getFieldTypeParameters(Field field) {
        Type genericType = field.getGenericType();

        if (genericType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) genericType;

            Type[] types = parameterizedType.getActualTypeArguments();

            List<Class<?>> classes = new ArrayList<>();
            for (Type type: types) {
                classes.add((Class<?>) type);
            }

            return classes;
        }

        return new ArrayList<>();
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
