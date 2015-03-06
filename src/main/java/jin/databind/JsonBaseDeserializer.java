package jin.databind;

import jin.annotations.*;
import jin.io.JsonReader;
import jin.io.JsonToken;
import jin.io.TokenType;
import jin.io.JsonProcessingException;
import jin.type.CollectionType;
import jin.type.GuessType;
import jin.type.MapType;
import jin.type.TypeLiteral;

import java.io.IOException;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public class JsonBaseDeserializer {

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

    @SuppressWarnings("unchecked")
    private <T> T deserializeObject(JsonReader reader, Class<T> cls) throws IOException, JsonProcessingException {
        reader.expect(TokenType.OBJECT_START);

        T object = instantiateObject(cls, reader);

        if (reader.accept(TokenType.OBJECT_END)) {
            reader.expect(TokenType.OBJECT_END);

            return object;
        }

        HashMap<Class<?>, HashMap<String, String>> classHierarchyMap = buildClassHierarchyMap(object.getClass());

        deserializeObjectFields(object, classHierarchyMap, reader);

        return object;
    }

    private void deserializeObjectFields(Object object, HashMap<Class<?>, HashMap<String, String>> classHierarchyMap, JsonReader reader) throws IOException, JsonProcessingException {
        JsonToken fieldNameToken = reader.expect(TokenType.STRING);

        reader.expect(TokenType.COLON);

        String propertyName = fieldNameToken.getValue();

        Class<?> cls = getDeclaringClass(propertyName, classHierarchyMap);

        HashMap<String, String> map = classHierarchyMap.get(cls);

        if (map == null) {
            reader.skip();

        } else {

            String fieldName = map.get(propertyName);

            if (cls == null || fieldName == null) {
                reader.skip();

            } else {

                deserializeField(reader, cls.cast(object), cls, fieldName);

                if (reader.accept(TokenType.OBJECT_END)) {
                    reader.expect(TokenType.OBJECT_END);

                    return;
                }
            }
        }

        reader.expect(TokenType.COMMA);

        deserializeObjectFields(object, classHierarchyMap, reader);
    }

    private void deserializeField(JsonReader reader, Object object, Class<?> cls, String fieldName) throws IOException, JsonProcessingException {
        try {
            Field field = cls.getDeclaredField(fieldName);
            field.setAccessible(true);

            if (!deserializeFieldWithCustomDeserializer(reader, object, field)) {

                if (Collection.class.isAssignableFrom(field.getType())) {
                    deserializeCollection((Collection) field.get(object), reader, getCollectionTypeParameter(field));

                } else if (Map.class.isAssignableFrom(field.getType())) {
                    List<Type> mapTypeParameters = getMapTypeParameters(field);

                    deserializeMap((Map) field.get(object), reader, mapTypeParameters.get(0), mapTypeParameters.get(1));

                } else {
                    Object value = deserializeValue(reader, field.getType());

                    field.set(object, value);
                }
            }

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();

            throw new JsonProcessingException(e);
        }
    }

    private boolean deserializeFieldWithCustomDeserializer(JsonReader reader, Object object, Field field) throws IOException, JsonProcessingException {
        if (field.isAnnotationPresent(Json.class)) {
            Json json = field.getAnnotation(Json.class);

            if (json != null && json.deserializeWith() != null && json.deserializeWith() != JsonDeserializer.class) {
                Class<? extends JsonDeserializer> deserializerClass = json.deserializeWith();

                try {
                    JsonDeserializer deserializer = deserializerClass.newInstance();
                    if (deserializer != null) {
                        Object value = deserializer.deserialize(reader);

                        field.set(object, value);

                        return true;
                    }
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                    throw new JsonProcessingException(e);
                }
            }
        }

        return false;
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

                } else if (cls.isEnum()) {
                    return deserializeEnumConstant(reader, cls);

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

    private Object deserializeEnumConstant(JsonReader reader, Class<?> cls) throws IOException, JsonProcessingException {
        Object[] constants = cls.getEnumConstants();
        String value = deserializeString(reader);

        try {
            for (Object constant : constants) {
                String constantValue = constant.toString();

                Enum en = (Enum) constant;
                Field enumConstant = en.getClass().getDeclaredField(en.name());

                if (enumConstant.isAnnotationPresent(JsonValue.class)) {
                    JsonValue jsonValue = enumConstant.getAnnotation(JsonValue.class);

                    if (jsonValue != null && !jsonValue.value().trim().equals("")) {
                        constantValue = jsonValue.value();
                    }
                }

                if (value.equals(constantValue)) {
                    return constant;
                }
            }
        } catch (NoSuchFieldException e) {
            throw new JsonProcessingException(e);
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

    private HashMap<Class<?>, HashMap<String, String>> buildClassHierarchyMap(Class<?> cls) {
        HashMap<Class<?>, HashMap<String, String>> map = new HashMap<>();

        HashMap<String, String> fieldNames = new HashMap<>();

        Class<?> superClass = cls.getSuperclass();

        if (superClass != Object.class && superClass != null) {
            map.putAll(buildClassHierarchyMap(superClass));
        }

        Field[] fields = cls.getDeclaredFields();

        for (Field field: fields) {
            field.setAccessible(true);

            if (!field.isAnnotationPresent(Json.class)) {
                fieldNames.put(field.getName(), field.getName());

            } else {
                Json json = field.getAnnotation(Json.class);

                if (json.property() != null && !json.property().trim().equals("")) {
                    fieldNames.put(json.property(), field.getName());
                } else {
                    fieldNames.put(field.getName(), field.getName());
                }
            }
        }

        map.put(cls, fieldNames);

        return map;
    }

    @SuppressWarnings("unchecked")
    private <T> T instantiateObject(Class<?> cls, JsonReader reader) throws IOException, JsonProcessingException {
        try {
            int mod = cls.getModifiers();

            if (!(Modifier.isAbstract(mod) || Modifier.isInterface(mod))) {
                return (T) cls.newInstance();
            }

            if (cls.isAnnotationPresent(JsonTypeInfo.class)) {
                T object = null;

                JsonTypeInfo typeInfo = cls.getAnnotation(JsonTypeInfo.class);

                String typeProperty = deserializeString(reader);
                reader.expect(TokenType.COLON);
                String typeId = deserializeString(reader);

                if (typeInfo.use() == JsonTypeInfo.Id.CLASS) {
                    object = (T) Class.forName(typeId).newInstance();

                } else if (typeInfo.use() == JsonTypeInfo.Id.CUSTOM) {
                    if (!typeProperty.equals(typeInfo.property())) {
                        throw new JsonProcessingException("cannot find type info property '" + typeInfo.property() + "' at the beginning of the object.");
                    }

                    JsonTypes types = getTypes(cls);

                    if (types != null) {
                        Class<?> c = getTypeValue(types, typeId);

                        if (c == null) {
                            throw new JsonProcessingException("cannot instantiate object of type '" + cls.getName() + "'");
                        }

                        object = (T) c.newInstance();
                    }
                }

                if (reader.accept(TokenType.COMMA)) {
                    reader.expect(TokenType.COMMA);
                }

                return object;
            }

            throw new JsonProcessingException("cannot instantiate object of type '" + cls.getName() + "'");

        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();

            throw new JsonProcessingException(e);
        }
    }

    private Class<?> getDeclaringClass(String fieldName, HashMap<Class<?>, HashMap<String, String>> map) {
        Set<Class<?>> classes = map.keySet();

        for (Class<?> cls: classes) {
            HashMap<String, String> fieldNames = map.get(cls);
            if (fieldNames.containsKey(fieldName)) {
                return cls;
            }
        }

        return null;
    }

    private JsonTypes getTypes(Class<?> cls) {
        Class<?> parentClass = cls;
        Class<?> superClass = cls;

        while (parentClass != Object.class && parentClass != null) {
            superClass = parentClass;
            parentClass = parentClass.getSuperclass();
        }

        if (superClass.isAnnotationPresent(JsonTypes.class)) {
            return superClass.getAnnotation(JsonTypes.class);
        }

        return null;
    }

    private Class<?> getTypeValue(JsonTypes types, String typeId) {
        for (JsonType type: types.value()) {
            if (type.id().equals(typeId)) {
                return type.value();
            }
        }

        return null;
    }
}
