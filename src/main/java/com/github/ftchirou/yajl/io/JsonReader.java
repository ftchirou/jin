package com.github.ftchirou.yajl.io;

import com.github.ftchirou.yajl.lexer.JsonToken;
import com.github.ftchirou.yajl.lexer.TokenType;
import com.github.ftchirou.yajl.lexer.UnrecognizedTokenException;
import com.github.ftchirou.yajl.lexer.fsm.FSM;

import java.io.*;
import java.nio.charset.Charset;

public class JsonReader {

    final char STRING_DELIMITER = '"';
    final char LEFT_BRACE = '{';
    final char RIGHT_BRACE = '}';
    final char LEFT_BRACKET = '[';
    final char RIGHT_BRACKET = ']';
    final char COLON = ':';
    final char COMMA = ',';
    final char T = 't';
    final char F = 'f';
    final char N = 'n';

    private int cursor;

    private Reader reader;

    public JsonReader(InputStream in) {
        cursor = 0;

        InputStreamReader isr = new InputStreamReader(in, Charset.forName("UTF-8"));
        reader = new BufferedReader(isr);
    }

    public JsonReader(String s) {
        cursor = 0;

        reader = new StringReader(s);
    }

    public boolean ready() throws IOException {
        return reader.ready();
    }

    public void close() throws IOException {
        reader.close();
    }

    public JsonToken nextToken() throws IOException, UnrecognizedTokenException {
        int current;
        char symbol = ' ';

        while ((current = reader.read()) > 0 && !Character.isWhitespace((symbol = (char) current))) {
            symbol = (char) reader.read();
        }

        if (current < 0) {
            return new JsonToken(TokenType.END_OF_STREAM);
        }

        switch (symbol) {
            case LEFT_BRACE:
                return new JsonToken(TokenType.OBJECT_START, "{", ++cursor);

            case RIGHT_BRACE:
                return new JsonToken(TokenType.OBJECT_END, "}", ++cursor);

            case LEFT_BRACKET:
                return new JsonToken(TokenType.ARRAY_START, "[", ++cursor);

            case RIGHT_BRACKET:
                return new JsonToken(TokenType.ARRAY_END, "]", ++cursor);

            case COLON:
                return new JsonToken(TokenType.COLON, ":", ++cursor);

            case COMMA:
                return new JsonToken(TokenType.COMMA, ",", ++cursor);

            case STRING_DELIMITER:
                return recognizeStringToken();

            case T:
                return recognizeTrueToken();

            case F:
                return recognizeFalseToken();

            case N:
                return recognizeNullToken();

            default:
                if (Character.isDigit(symbol) || symbol == '-' || symbol == '0') {
                    return recognizeNumberToken();
                }
                break;
        }

        throw new UnrecognizedTokenException("invalid character " + symbol + " at position " + cursor);
    }

    private JsonToken recognizeStringToken() throws IOException, UnrecognizedTokenException {
        int position = cursor;
        int current;
        char symbol = ' ';

        String value = "";

        while ((current = reader.read()) > 0 && (symbol = (char) current) != STRING_DELIMITER) {
            value += symbol;
            cursor++;
        }

        if (symbol != STRING_DELIMITER) {
            throw new UnrecognizedTokenException("unclosed string literal at position " + position);
        }

        return new JsonToken(TokenType.STRING, value, position);
    }

    private JsonToken recognizeTrueToken() throws IOException, UnrecognizedTokenException {
        return recognizeToken(TokenType.TRUE, "true");
    }

    private JsonToken recognizeFalseToken() throws IOException, UnrecognizedTokenException {
        return recognizeToken(TokenType.FALSE, "false");
    }

    private JsonToken recognizeNullToken() throws IOException, UnrecognizedTokenException {
        return recognizeToken(TokenType.NULL, "null");
    }

    private JsonToken recognizeNumberToken() throws IOException, UnrecognizedTokenException {
        int position = cursor;

        FSM recognizer = buildNumberRecognizer();

        FSM.Output output = recognizer.run(reader);

        if (!output.isRecognized()) {
            throw new UnrecognizedTokenException("unrecognized JSON token at position " + position);
        }

        cursor += output.getValue().length();

        return new JsonToken(TokenType.NUMBER, output.getValue(), position);
    }

    private JsonToken recognizeToken(TokenType type, String repr) throws IOException, UnrecognizedTokenException {
        int position = cursor;
        int length = repr.length();

        FSM fsm = new FSM(length + 1);
        fsm.setInitialState(0);
        fsm.setFinalStates(length);

        FSM.Output output = fsm.run(reader);

        if (!output.isRecognized()) {
            throw new UnrecognizedTokenException("unrecognized JSON token at position " + position);
        }

        cursor += output.getValue().length();

        return new JsonToken(type, output.getValue(), position);
    }

    private FSM buildNumberRecognizer() {
        final int NUMBER = 0;
        final int INT = 1;
        final int INT_FRAC = 2;
        final int INT_EXP = 3;
        final int START_FRAC = 4;
        final int START_EXP = 5;
        final int ZERO = 6;

        FSM fsm = new FSM(7);
        fsm.setInitialState(NUMBER);
        fsm.setFinalStates(ZERO, INT, INT_FRAC, INT_EXP);

        fsm.addTransition(NUMBER, '0', ZERO);
        fsm.addTransition(NUMBER, '-', INT);

        String digits = "123456789";
        int digitsLength = digits.length();

        for (int i = 0; i < digitsLength; ++i) {
            char digit = digits.charAt(i);

            fsm.addTransition(NUMBER, digit, INT);

            fsm.addTransition(INT, digit, INT);
            fsm.addTransition(INT, '0', INT);

            fsm.addTransition(START_FRAC, digit, INT_FRAC);
            fsm.addTransition(INT_FRAC, digit, INT_FRAC);
            fsm.addTransition(INT_FRAC, '0', INT_FRAC);

            fsm.addTransition(START_EXP, digit, INT_EXP);
            fsm.addTransition(INT_EXP, digit, INT_EXP);
            fsm.addTransition(INT_EXP, '0', INT_EXP);
        }

        fsm.addTransition(INT, '.', START_FRAC);

        fsm.addTransition(INT, 'e', START_EXP);
        fsm.addTransition(INT, 'E', START_EXP);

        fsm.addTransition(INT_FRAC, 'e', START_EXP);
        fsm.addTransition(INT_FRAC, 'E', START_EXP);

        fsm.addTransition(START_EXP, '+', START_EXP);
        fsm.addTransition(START_EXP, '-', START_EXP);

        return fsm;
    }
}
