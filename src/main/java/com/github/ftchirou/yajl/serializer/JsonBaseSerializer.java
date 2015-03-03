package com.github.ftchirou.yajl.serializer;

import com.github.ftchirou.yajl.annotations.JsonGetter;
import com.github.ftchirou.yajl.annotations.Json;
import com.github.ftchirou.yajl.annotations.JsonValue;
import com.github.ftchirou.yajl.io.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class JsonBaseSerializer extends JsonSerializer<Object> {

    @Override
    public void serialize(Object object, JsonWriter writer) throws IOException {
        if (object == null) {
            writer.writeNull();

            return;
        }

        Class<?> cls = object.getClass();

        if (cls.isPrimitive()) {
            serializePrimitive(object, writer);

        } else if (cls.getName().equals("java.lang.String")) {
            writer.writeString(object.toString());

        } else if (cls.getName().equals("java.lang.Integer")) {
            writer.writeInt((int) object);

        } else if (cls.getName().equals("java.lang.Short")) {
            writer.writeInt((short) object);

        } else if (cls.getName().equals("java.lang.Byte")) {
            writer.writeInt((byte) object);

        } else if (cls.getName().equals("java.lang.Boolean")) {
            writer.writeBoolean((boolean) object);

        } else if (cls.getName().equals("java.lang.Long")) {
                writer.writeLong((long) object);

        } else if (cls.getName().equals("java.lang.Double")) {
            writer.writeDouble((double) object);

        } else if (cls.getName().equals("java.lang.Float")) {
            writer.writeFloat((float) object);

        } else if (cls.getName().equals("java.math.BigInteger")) {
            writer.writeBigInteger((BigInteger) object);

        } else if (cls.getName().equals("java.math.BigDecimal")) {
            writer.writeBigDecimal((BigDecimal) object);

        } else if (cls.isArray()) {
            if (!cls.getComponentType().isPrimitive()) {
                serializeArray(Object[].class.cast(object), writer);
            } else {
                serializeArray(object, writer);
            }

        } else if (cls.isEnum()) {
            serializeEnumConstant(object, writer);

        } else if (object instanceof Collection) {
            serializeCollection((Collection) object, writer);

        } else if (object instanceof Map) {
            serializeMap((Map) object, writer);

        } else {
           serializeObject(object, writer);
        }
    }

    private void serializeObject(Object object, JsonWriter writer) throws IOException {
        Class<?> cls = object.getClass();

        writer.writeObjectStart();

        List<Field> fields = nonIgnorableFields(cls.getDeclaredFields());

        int length = fields.size();

        if (length > 0) {
            serializeField(object, fields.get(0), writer);

            for (int i = 1; i < length; ++i) {
                writer.writeComma();
                serializeField(object, fields.get(i), writer);
            }
        }

        serializeMethodsMarkedAsGetters(object, cls.getDeclaredMethods(), writer);

        writer.writeObjectEnd();
    }

    @SuppressWarnings("unchecked")
    private void serializeField(Object object, Field field, JsonWriter writer) throws IOException {
        field.setAccessible(true);

        String fieldName = field.getName();
        JsonSerializer serializer = this;

        if (field.isAnnotationPresent(Json.class)) {
            Json property = field.getAnnotation(Json.class);

            if (property.propertyName() != null && !property.propertyName().trim().equals("")) {
                fieldName = property.propertyName();
            }

            if (property.serializeWith() != null && property.serializeWith() != JsonSerializer.class) {
                Class<? extends JsonSerializer> serializerClass = property.serializeWith();

                try {
                    JsonSerializer ser = serializerClass.newInstance();
                    if (ser != null) {
                        serializer = ser;
                    }

                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        writer.writeFieldName(fieldName);

        try {
            serializer.serialize(field.get(object), writer);

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void serializeArray(Object[] array, JsonWriter writer) throws IOException {
        writer.writeArrayStart();

        int length = array.length;
        if (length > 0) {
            serialize(array[0], writer);

            for (int i = 1; i < length; ++i) {
                writer.writeComma();
                serialize(array[i], writer);
            }
        }

        writer.writeArrayEnd();
    }

    private void serializeArray(Object array, JsonWriter writer) throws IOException {
        writer.writeArrayStart();

        int length = Array.getLength(array);
        if (length > 0) {
            serialize(Array.get(array, 0), writer);

            for (int i = 1; i < length; ++i) {
                writer.writeComma();
                serialize(Array.get(array, i), writer);
            }
        }

        writer.writeArrayEnd();
    }

    private void serializeCollection(Collection collection, JsonWriter writer) throws IOException {
        serializeArray(collection.toArray(), writer);
    }

    private void serializeMap(Map map, JsonWriter writer) throws IOException {
        writer.writeObjectStart();

        Object[] keys = map.keySet().toArray();
        int length = keys.length;

        if (length > 0) {
            writer.writeFieldName(keys[0].toString());
            serialize(map.get(keys[0]), writer);

            for (int i = 1; i < length; ++i) {
                writer.writeComma();

                writer.writeFieldName(keys[i].toString());
                serialize(map.get(keys[i]), writer);
            }
        }

        writer.writeObjectEnd();
    }

    private void serializeEnumConstant(Object object, JsonWriter writer) throws IOException {
        Enum en = (Enum) object;
        String value = object.toString();

        try {
            Field enumConstant = en.getClass().getField(en.name());

            if (enumConstant.isAnnotationPresent(JsonValue.class)) {
                JsonValue constant = enumConstant.getAnnotation(JsonValue.class);

                if (constant != null && !constant.value().trim().equals("")) {
                    value = constant.value();
                }
            }

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        writer.writeString(value);

    }

    @SuppressWarnings("unchecked")
    private void serializeMethodsMarkedAsGetters(Object object, Method[] methods, JsonWriter writer) throws IOException {
        for (Method method : methods) {
            method.setAccessible(true);

            if (!method.isAnnotationPresent(JsonGetter.class)) {
                continue;
            }

            JsonGetter getter = method.getAnnotation(JsonGetter.class);
            if (getter == null) {
                continue;
            }

            String fieldName = method.getName();
            JsonSerializer serializer = this;

            if (getter.name() != null && !getter.name().trim().equals("")) {
                fieldName = getter.name();
            }

            if (getter.serializeWith() != null && getter.serializeWith() != JsonSerializer.class) {
                Class<? extends JsonSerializer> serializerClass = getter.serializeWith();

                try {
                    JsonSerializer ser = serializerClass.newInstance();
                    if (ser != null) {
                        serializer = ser;
                    }
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            writer.writeComma();

            writer.writeFieldName(fieldName);

            try {
                serializer.serialize(method.invoke(object), writer);

            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    private void serializePrimitive(Object element, JsonWriter writer) throws IOException {
        switch (element.getClass().getName()) {
            case "boolean":
                writer.writeBoolean((boolean) element);

            case "int":
                writer.writeInt((int) element);

            case "short":
                writer.writeInt((short) element);

            case "byte":
                writer.writeInt((byte) element);

            case "long":
                writer.writeLong((long) element);

            case "double":
                writer.writeDouble((double) element);

            case "float":
                writer.writeFloat((float) element);

            default:
                break;
        }
    }

    private List<Field> nonIgnorableFields(Field[] fields) {
        List<Field> list = new ArrayList<>();

        for (Field field : fields) {
            if (!isMarkedAsIgnorable(field)) {
                list.add(field);
            }
        }

        return list;
    }

    private boolean isMarkedAsIgnorable(Field field) {
        if (field.isAnnotationPresent(Json.class)) {
            Json json = field.getAnnotation(Json.class);

            if (json != null && json.ignore()) {
                return true;
            }
        }

        return false;
    }
}
