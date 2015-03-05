package jin.tree;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JsonArray extends JsonNode implements Iterable<JsonNode> {

    private List<JsonNode> nodes;

    public JsonArray() {
        this.nodes = new ArrayList<>();
    }

    public void add(JsonNode node) {
        nodes.add(node);
    }

    public void add(int index, JsonNode node) {
        nodes.add(index, node);
    }

    public JsonNode get(int index) {
        return nodes.get(index);
    }

    public JsonNode remove(int index) {
        return nodes.remove(index);
    }

    public int size() {
        return nodes.size();
    }

    public boolean isEmpty() {
        return nodes.isEmpty();
    }

    public boolean contains(Object o) {
        return nodes.contains(o);
    }

    public Iterator<JsonNode> iterator() {
        return nodes.iterator();
    }

    public Object[] toArray() {
        return nodes.toArray();
    }


    @Override
    public boolean isObject() {
        return false;
    }

    @Override
    public boolean isArray() {
        return true;
    }

    @Override
    public boolean isNull() {
        return false;
    }

    @Override
    public boolean isString() {
        return false;
    }

    @Override
    public boolean isBoolean() {
        return false;
    }

    @Override
    public boolean isInt() {
        return false;
    }

    @Override
    public boolean isLong() {
        return false;
    }

    @Override
    public boolean isBigInt() {
        return false;
    }

    @Override
    public boolean isDecimal() {
        return false;
    }

    @Override
    public String stringValue() {
        return null;
    }

    @Override
    public int intValue() {
        return 0;
    }

    @Override
    public long longValue() {
        return 0L;
    }

    @Override
    public double doubleValue() {
        return 0.0;
    }

    @Override
    public boolean booleanValue() {
        return false;
    }

    @Override
    public BigInteger bigIntValue() {
        return null;
    }

    @Override
    public BigDecimal bigDecimalValue() {
        return null;
    }

    @Override
    public String toJsonString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[");

        if (nodes.size() > 0) {
            for (JsonNode node: nodes) {
                builder.append(node.toJsonString());
                builder.append(",");
            }

            builder.deleteCharAt(builder.length() - 1);
        }

        builder.append("]");

        return builder.toString();
    }
}