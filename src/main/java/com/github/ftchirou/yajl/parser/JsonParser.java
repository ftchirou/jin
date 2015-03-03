package com.github.ftchirou.yajl.parser;

import com.github.ftchirou.yajl.json.*;
import com.github.ftchirou.yajl.lexer.JsonToken;
import com.github.ftchirou.yajl.lexer.JsonTokenizer;
import com.github.ftchirou.yajl.lexer.TokenType;
import com.github.ftchirou.yajl.lexer.UnrecognizedTokenException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public class JsonParser {

    private int cursor;

    private List<JsonToken> tokens;

    public JsonParser() {
        this.cursor = 0;
    }

    public JsonNode parse(String jsonString) throws JsonProcessingException {
        JsonTokenizer tokenizer = new JsonTokenizer();

        cursor = 0;

        try {
            tokens = tokenizer.tokenize(jsonString);

            return parseValue();

        } catch (UnrecognizedTokenException e) {
            throw new JsonProcessingException(e);
        }
    }

    private void expect(TokenType type) throws JsonProcessingException {
        if (cursor >= tokens.size()) {
            throw new JsonProcessingException("unexpected end of input.");
        }

        JsonToken token = tokens.get(cursor);

        if (token.getType() != type) {
            throw new JsonProcessingException("expected " + type.toString() + " at position " + token.getPosition());
        }

        cursor++;
    }

    public JsonNode parseObject() throws JsonProcessingException {
        expect(TokenType.OBJECT_START);

        if (tokens.get(cursor).getType() == TokenType.OBJECT_END) {
            expect(TokenType.OBJECT_END);

            return new JsonObject();
        }

        HashMap<String, JsonNode> members = parseMembers();

        expect(TokenType.OBJECT_END);

        JsonObject object = new JsonObject();
        object.addAll(members);

        return object;

    }

    public JsonNode parseArray() throws JsonProcessingException {
        expect(TokenType.ARRAY_START);

        JsonArray array = new JsonArray();

        JsonToken token = tokens.get(cursor);

        while (token.getType() != TokenType.ARRAY_END) {
            array.add(parseValue());

            if (tokens.get(cursor).getType() == TokenType.ARRAY_END) {
                break;
            }

            expect(TokenType.COMMA);

            token = tokens.get(cursor);
        }

        expect(TokenType.ARRAY_END);

        return array;
    }

    private HashMap<String, JsonNode> parseMembers() throws JsonProcessingException {
        LinkedHashMap<String, JsonNode> members = new LinkedHashMap<>();

        JsonPair pair = parsePair();

        members.put(pair.getKey(), pair.getValue());

        if (cursor < tokens.size() && tokens.get(cursor).getType() != TokenType.OBJECT_END) {
            expect(TokenType.COMMA);

            members.putAll(parseMembers());
        }

        return members;
    }

    private JsonPair parsePair() throws JsonProcessingException {
        int keyPosition = cursor;

        expect(TokenType.STRING);
        expect(TokenType.COLON);

        return new JsonPair(tokens.get(keyPosition).getValue(), parseValue());
    }

    private JsonNode parseValue() throws JsonProcessingException {
        JsonToken token = tokens.get(cursor);
        JsonNode node;

        switch (token.getType()) {
            case OBJECT_START:
                node = parseObject();
                break;

            case ARRAY_START:
                node = parseArray();
                break;

            case STRING:
                node = new JsonString(token.getValue());
                cursor++;
                break;

            case TRUE:
                node = new JsonBoolean(true);
                cursor++;
                break;

            case FALSE:
                node = new JsonBoolean(false);
                cursor++;
                break;

            case NULL:
                node = new JsonNull();
                cursor++;
                break;

            case NUMBER:
                String value = token.getValue();
                if (value.contains(".") || value.contains("e") || value.contains("E")) {
                    node = parseDecimalValue(value);

                } else {
                    node = parseIntValue(value);
                }

                cursor++;
                break;

            default:
                node = new JsonNull();
                cursor++;
                break;
        }

        return node;
    }

    private JsonNode parseDecimalValue(String value) {
       return new JsonDecimal(new BigDecimal(value));
    }

    private JsonNode parseIntValue(String value) {
        try {
            return new JsonInt(Integer.parseInt(value));
        } catch (NumberFormatException e) {
            try {
                return new JsonLong(Long.parseLong(value));
            } catch (NumberFormatException nfe) {
                return new JsonBigInt(new BigInteger(value));
            }
        }
    }

    static class JsonPair {

        String key;

        JsonNode value;

        JsonPair(String key, JsonNode value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public JsonNode getValue() {
            return value;
        }
    }
}
