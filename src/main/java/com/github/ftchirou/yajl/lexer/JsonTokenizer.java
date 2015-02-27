package com.github.ftchirou.yajl.lexer;

import com.github.ftchirou.yajl.lexer.fsm.FSM;

import java.util.ArrayList;
import java.util.List;

public class JsonTokenizer {

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

    public JsonTokenizer() {
        this.cursor = 0;
    }

    public List<JsonToken> tokenize(String stream) throws UnrecognizedTokenException {
        List<JsonToken> tokens = new ArrayList<JsonToken>();

        int l = stream.length();

        while (cursor < l) {
            char symbol = stream.charAt(cursor);

            switch (symbol) {
                case LEFT_BRACE:
                    tokens.add(new JsonToken(TokenType.OBJECT_START, "{", cursor));
                    cursor++;
                    break;

                case RIGHT_BRACE:
                    tokens.add(new JsonToken(TokenType.OBJECT_END, "}", cursor));
                    cursor++;
                    break;

                case LEFT_BRACKET:
                    tokens.add(new JsonToken(TokenType.ARRAY_START, "[", cursor));
                    cursor++;
                    break;

                case RIGHT_BRACKET:
                    tokens.add(new JsonToken(TokenType.ARRAY_END, "]", cursor));
                    cursor++;
                    break;

                case COLON:
                    tokens.add(new JsonToken(TokenType.COLON, ":", cursor));
                    cursor++;
                    break;

                case COMMA:
                    tokens.add(new JsonToken(TokenType.COMMA, ",", cursor));
                    cursor++;
                    break;

                case STRING_DELIMITER:
                    tokens.add(recognizeStringToken(stream));
                    break;

                case T:
                    tokens.add(recognizeTrueToken(stream));
                    break;

                case F:
                    tokens.add(recognizeFalseToken(stream));
                    break;

                case N:
                    tokens.add(recognizeNullToken(stream));
                    break;

                default:
                    if (Character.isWhitespace(symbol)) {
                        cursor++;
                    } else if (Character.isDigit(symbol) || symbol == '-' || symbol == '0') {
                        tokens.add(recognizeNumberToken(stream));
                    } else {
                        throw new UnrecognizedTokenException("invalid character " + symbol + " at position " + cursor + ".");
                    }
                    break;
            }
        }

        return tokens;
    }

    private JsonToken recognizeStringToken(String stream) throws UnrecognizedTokenException {
        int position = cursor;
        int l = stream.length();

        String value = "";
        char current = ' ';

        while (++cursor < l && (current = stream.charAt(cursor)) != STRING_DELIMITER) {
            value += current;
        }

        if (current != STRING_DELIMITER) {
            throw new UnrecognizedTokenException("unclosed string literal at position " + position);
        }

        cursor++;

        return new JsonToken(TokenType.STRING, value, position);
    }

    private JsonToken recognizeTrueToken(String stream) throws UnrecognizedTokenException {
        return recognizeToken(stream, TokenType.TRUE, "true");
    }

    private JsonToken recognizeFalseToken(String stream) throws UnrecognizedTokenException {
        return recognizeToken(stream, TokenType.FALSE, "false");
    }

    private JsonToken recognizeNullToken(String stream) throws UnrecognizedTokenException {
        return recognizeToken(stream, TokenType.NULL, "null");
    }

    private JsonToken recognizeNumberToken(String stream) throws UnrecognizedTokenException {
        int position = cursor;

        FSM recognizer = buildNumberRecognizer();

        FSM.Output output = recognizer.run(stream.substring(position));

        if (!output.isRecognized()) {
            throw new UnrecognizedTokenException("unrecognized JSON token at position " + position);
        }

        cursor += output.getValue().length();

        return new JsonToken(TokenType.NUMBER, output.getValue(), position);
    }

    private JsonToken recognizeToken(String stream, TokenType type, String repr) throws UnrecognizedTokenException {
        int position = cursor;
        int length = repr.length();

        FSM fsm = new FSM(length + 1);
        fsm.setInitialState(0);
        fsm.setFinalStates(length);

        for (int i = 0; i <= length - 1; ++i) {
            fsm.addTransition(i, repr.charAt(i), i + 1);
        }

        FSM.Output output = fsm.run(stream.substring(position));

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
