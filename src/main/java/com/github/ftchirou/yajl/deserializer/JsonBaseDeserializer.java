package com.github.ftchirou.yajl.deserializer;

import com.github.ftchirou.yajl.io.JsonReader;
import com.github.ftchirou.yajl.lexer.JsonToken;
import com.github.ftchirou.yajl.lexer.TokenType;
import com.github.ftchirou.yajl.parser.JsonProcessingException;
import com.github.ftchirou.yajl.type.CollectionType;
import com.github.ftchirou.yajl.type.GuessType;
import com.github.ftchirou.yajl.type.MapType;
import com.github.ftchirou.yajl.type.TypeLiteral;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public class JsonBaseDeserializer {

    private JsonToken token;

    @SuppressWarnings("unchecked")
    public <T> T deserialize(JsonReader reader) throws IOException, JsonProcessingException {
        reader.readToken();

        try {
            return (T) deserializeUnknownTypeValue(reader);

        } catch (ClassCastException e) {
            throw new JsonProcessingException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T deserialize(JsonReader reader, Type type) throws IOException, JsonProcessingException {
        reader.readToken();

        try {
            return (T) deserializeValue(reader, type);

        } catch (ClassCastException e) {
            throw new JsonProcessingException(e);
        }
    }

    private <T> T deserializeObject(JsonReader reader, Class<T> cls) throws IOException, JsonProcessingException {
        try {
            T object = cls.newInstance();

            reader.expect(TokenType.OBJECT_START);

            if (reader.accept(TokenType.OBJECT_END)) {
                reader.expect(TokenType.OBJECT_END);

                return object;
            }

            deserializeObjectFields(object, reader, cls);

            return object;

        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();

            throw new JsonProcessingException(e);
        }
    }

    private void deserializeObjectFields(Object object, JsonReader reader, Class<?> cls) throws IOException, JsonProcessingException {
        JsonToken fieldNameToken = reader.expect(TokenType.STRING);

        reader.expect(TokenType.COLON);

        try {
            Field field = cls.getDeclaredField(fieldNameToken.getValue());
            field.setAccessible(true);

            if (Collection.class.isAssignableFrom(field.getType())) {
                deserializeCollection((Collection) field.get(object), reader, getCollectionTypeParameter(field));
                
            } else if (Map.class.isAssignableFrom(field.getType())) {
                List<Type> mapTypeParameters = getMapTypeParameters(field);

                deserializeMap((Map) field.get(object), reader, mapTypeParameters.get(0), mapTypeParameters.get(1));

            } else {
                Object value = deserializeValue(reader, field.getType());

                field.set(object, value);
            }

            if (reader.accept(TokenType.OBJECT_END)) {
                reader.expect(TokenType.OBJECT_END);

                return;
            }

            reader.expect(TokenType.COMMA);

            deserializeObjectFields(object, reader, cls);

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();

            throw new JsonProcessingException(e);
        }
    }

    private Object deserializeArray(JsonReader reader, Class<?> componentType) throws IOException, JsonProcessingException {
        ArrayList<Object> list = new ArrayList<>();

        deserializeCollection(list, reader, componentType);

        int size = list.size();

        Object array = Array.newInstance(componentType, size);

        for (int i = 0; i < size; ++i) {
            Array.set(array, i, list.get(i));
        }

        return array;
    }

    @SuppressWarnings("unchecked")
    private void deserializeCollection(Collection collection, JsonReader reader, Type componentType) throws IOException, JsonProcessingException {
        reader.expect(TokenType.ARRAY_START);

        while (!reader.accept(TokenType.ARRAY_END)) {
            collection.add(deserializeValue(reader, componentType));

            if (reader.accept(TokenType.ARRAY_END)) {
                break;
            }

            reader.expect(TokenType.COMMA);
        }

        reader.expect(TokenType.ARRAY_END);
    }

    private void deserializeMap(Map map, JsonReader reader, Type keyType, Type valueType) throws IOException, JsonProcessingException {
        reader.expect(TokenType.OBJECT_START);

        if (reader.accept(TokenType.OBJECT_END)) {
            reader.expect(TokenType.OBJECT_END);

            return;
        }

        deserializeMapEntries(map, reader, keyType, valueType);
    }

    @SuppressWarnings("unchecked")
    private void deserializeMapEntries(Map map, JsonReader reader, Type keyType, Type valueType) throws IOException, JsonProcessingException {
        Object key = deserializeValue(reader, keyType);

        reader.expect(TokenType.COLON);

        Object value = deserializeValue(reader, valueType);

        map.put(key, value);

        if (reader.accept(TokenType.OBJECT_END)) {
            reader.expect(TokenType.OBJECT_END);

            return;
        }

        reader.expect(TokenType.COMMA);

        deserializeMapEntries(map, reader, keyType, valueType);
    }

    private Object deserializeValue(JsonReader reader, Type valueType) throws IOException, JsonProcessingException {
        if (reader.accept(TokenType.NULL)) {
            return null;
        }

        try {
            if (valueType instanceof TypeLiteral) {
                return deserializeComplexValue(reader, (TypeLiteral) valueType);

            } else if (valueType instanceof GuessType) {
                return deserializeUnknownTypeValue(reader);

            } else if (valueType instanceof ParameterizedType) {
                return deserializeParameterizedTypeValue(reader, (ParameterizedType) valueType);

            } else if (valueType instanceof Class<?>) {
                Class<?> cls = (Class<?>) valueType;

                String type = cls.getName();

                if (type.equals("java.lang.String")) {
                    return deserializeString(reader);

                } else if (type.equals("int") || type.equals("java.lang.Integer")
                        || type.equals("short") || type.equals("java.lang.Short")
                        || type.equals("byte") || type.equals("java.lang.Byte")) {

                    return deserializeInteger(reader);

                } else if (type.equals("boolean") || type.equals("java.lang.Boolean")) {
                    return deserializeBoolean(reader);

                } else if (type.equals("long") || type.equals("java.lang.Long")) {
                    return deserializeLong(reader);

                } else if (type.equals("double") || type.equals("java.lang.Double")) {
                    return deserializeDouble(reader);

                } else if (type.equals("float") || type.equals("java.lang.Float")) {
                    return deserializeFloat(reader);

                } else if (cls.getName().equals("java.math.BigInteger")) {
                    return deserializeBigInteger(reader);

                } else if (cls.getName().equals("java.math.BigDecimal")) {
                    return deserializeBigDecimal(reader);

                } else if (cls.isArray()) {
                    return deserializeArray(reader, cls.getComponentType());

                } else if (Collection.class.isAssignableFrom(cls)) {
                    Collection collection = (Collection) cls.newInstance();

                    deserializeCollection(collection, reader, new GuessType());

                    return collection;

                } else if (Map.class.isAssignableFrom(cls)) {
                    Map map = (Map) cls.newInstance();

                    deserializeMap(map, reader, new GuessType(), new GuessType());

                    return map;

                } else {
                    return deserializeObject(reader, cls);
                }
            }

        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();

            throw new JsonProcessingException(e);
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    private <T> T deserializeComplexValue(JsonReader reader, TypeLiteral typeLiteral) throws IOException, JsonProcessingException {
        try {
            if (typeLiteral instanceof CollectionType) {
                CollectionType collectionType = (CollectionType) typeLiteral;

                Type concreteType = collectionType.getConcreteType();

                if (concreteType instanceof Class<?>) {
                    Class<?> containerClass = (Class<?>) concreteType;

                    Collection collection = (Collection) containerClass.newInstance();

                    deserializeCollection(collection, reader, collectionType.getElementType());

                    return (T) collection;
                }

            } else if (typeLiteral instanceof MapType) {
                MapType mapType = (MapType) typeLiteral;

                Type concreteType = mapType.getConcreteType();

                if (concreteType instanceof Class<?>) {
                    Class<?> containerClass = (Class<?>) concreteType;

                    Map map = (Map) containerClass.newInstance();

                    deserializeMap(map, reader, mapType.getKeyType(), mapType.getValueType());

                    return (T) map;
                }
            }

        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();

            throw new JsonProcessingException(e);
        }

        return null;
    }

    private Object deserializeParameterizedTypeValue(JsonReader reader, ParameterizedType type) throws IOException, JsonProcessingException {
        Type[] actualTypes = type.getActualTypeArguments();
        Type rawType = type.getRawType();

        try {
            if (rawType instanceof Class<?>) {
                Class<?> containerClass = (Class<?>) rawType;

                if (Collection.class.isAssignableFrom(containerClass)) {
                    Collection collection = (Collection) containerClass.newInstance();

                    deserializeCollection(collection, reader, actualTypes[0]);

                    return collection;

                } else if (Map.class.isAssignableFrom(containerClass)) {
                    Map map = (Map) containerClass.newInstance();

                    deserializeMap(map, reader, actualTypes[0], actualTypes[1]);

                    return map;
                }
            }

            return null;

        } catch (InstantiationException | IllegalAccessException e) {
            throw new JsonProcessingException(e);
        }
    }

    private Object deserializeUnknownTypeValue(JsonReader reader) throws IOException, JsonProcessingException {
        if (reader.accept(TokenType.NULL)) {
            return null;
        }

        if (reader.accept(TokenType.STRING)) {
            return deserializeString(reader);
        }

        if (reader.accept(TokenType.TRUE) || reader.accept(TokenType.FALSE)) {
            return deserializeBoolean(reader);
        }

        if (reader.accept(TokenType.NUMBER)) {
            String value = reader.currentToken().getValue();

            if (value.contains(".") || value.contains("e") || value.contains("E")) {

                if (!Double.isInfinite(Double.parseDouble(value))) {
                    return deserializeDouble(reader);
                } else {
                    return deserializeBigDecimal(reader);
                }

            } else {
                try {
                    Integer.parseInt(value);
                    return deserializeInteger(reader);

                } catch (NumberFormatException e) {
                    try {
                        Long.parseLong(value);
                        return deserializeLong(reader);

                    } catch (NumberFormatException ex) {
                        return deserializeBigInteger(reader);
                    }
                }
            }
        }

        if (reader.accept(TokenType.ARRAY_START)) {
            List<Object> list = new ArrayList<>();

            deserializeCollection(list, reader, new GuessType());

            return list;
        }

        if (reader.accept(TokenType.OBJECT_START)) {
            Map<Object, Object> map = new LinkedHashMap<>();

            deserializeMap(map, reader, new GuessType(), new GuessType());

            return map;
        }

        return null;
    }

    private String deserializeString(JsonReader reader) throws IOException, JsonProcessingException {
        JsonToken expected = reader.expect(TokenType.STRING);

        return expected.getValue();
    }

    private Integer deserializeInteger(JsonReader reader) throws IOException, JsonProcessingException {
        JsonToken expected = reader.expect(TokenType.NUMBER);

        return Integer.parseInt(expected.getValue());
    }

    private Long deserializeLong(JsonReader reader) throws IOException, JsonProcessingException {
        JsonToken expected = reader.expect(TokenType.NUMBER);

        return Long.parseLong(expected.getValue());
    }

    private Double deserializeDouble(JsonReader reader) throws IOException, JsonProcessingException {
        JsonToken expected = reader.expect(TokenType.NUMBER);

        return Double.parseDouble(expected.getValue());
    }

    private Float deserializeFloat(JsonReader reader) throws IOException, JsonProcessingException {
        JsonToken expected = reader.expect(TokenType.NUMBER);

        return Float.parseFloat(expected.getValue());
    }

    private BigInteger deserializeBigInteger(JsonReader reader) throws IOException, JsonProcessingException {
        JsonToken expected = reader.expect(TokenType.NUMBER);

        return new BigInteger(expected.getValue());
    }

    private BigDecimal deserializeBigDecimal(JsonReader reader) throws IOException, JsonProcessingException {
        JsonToken expected = reader.expect(TokenType.NUMBER);

        return new BigDecimal(expected.getValue());
    }

    private Boolean deserializeBoolean(JsonReader reader) throws IOException, JsonProcessingException {
        if (!(reader.accept(TokenType.TRUE) || reader.accept(TokenType.FALSE))) {
            throw new JsonProcessingException("expected 'true' or 'false' at position " + reader.currentToken().getPosition());
        }

        JsonToken booleanToken = reader.currentToken();

        reader.readToken();

        return Boolean.parseBoolean(booleanToken.getValue());
    }

    private Type getCollectionTypeParameter(Field field) {
        return getFieldTypeParameters(field).get(0);
    }

    private List<Type> getMapTypeParameters(Field field) {
        return getFieldTypeParameters(field);
    }

    private List<Type> getFieldTypeParameters(Field field) {
        Type genericType = field.getGenericType();

        if (genericType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) genericType;

            Type[] types = parameterizedType.getActualTypeArguments();

            List<Type> list = new ArrayList<>();
            Collections.addAll(list, types);

            return list;
        }

        return new ArrayList<>();
    }
}
