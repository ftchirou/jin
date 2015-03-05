package jin.pojo;

public class NumericField2 extends FieldWithCustomTypeInfo {
    private int value;

    public NumericField2() {
        this.type = "numeric";
    }

    public NumericField2(int value) {
        this.type = "numeric";
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
