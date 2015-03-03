package com.github.ftchirou.yajl.deserializer.tests;

import com.github.ftchirou.yajl.deserializer.JsonDeserializer;
import com.github.ftchirou.yajl.io.JsonReader;
import com.github.ftchirou.yajl.lexer.JsonToken;
import com.github.ftchirou.yajl.lexer.TokenType;
import com.github.ftchirou.yajl.parser.JsonProcessingException;
import org.joda.time.DateTime;

import java.io.IOException;

public class DateTimeDeserializer extends JsonDeserializer<DateTime> {

    public DateTime deserialize(JsonReader reader) throws IOException, JsonProcessingException {
        JsonToken token = reader.expect(TokenType.STRING);

        return DateTime.parse(token.getValue());
    }

}
