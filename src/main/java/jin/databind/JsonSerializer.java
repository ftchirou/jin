package jin.databind;

import jin.io.JsonWriter;

import java.io.IOException;

public abstract class JsonSerializer<T> {

    public abstract void serialize(T t, JsonWriter writer) throws IOException;
}
