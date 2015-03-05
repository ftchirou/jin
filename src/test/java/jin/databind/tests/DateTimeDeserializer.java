package jin.databind.tests;

import jin.databind.JsonDeserializer;
import jin.io.JsonReader;
import jin.io.JsonToken;
import jin.io.TokenType;
import jin.io.JsonProcessingException;
import org.joda.time.DateTime;

import java.io.IOException;

public class DateTimeDeserializer extends JsonDeserializer<DateTime> {

    public DateTime deserialize(JsonReader reader) throws IOException, JsonProcessingException {
        JsonToken token = reader.expect(TokenType.STRING);

        return DateTime.parse(token.getValue());
    }
}
