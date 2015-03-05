package jin.databind;

import jin.io.JsonReader;
import jin.io.JsonProcessingException;

import java.io.IOException;

public abstract class JsonDeserializer<T> {

    public abstract T deserialize(JsonReader reader) throws IOException, JsonProcessingException;
}
