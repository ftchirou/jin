public class NumericField extends Field {

    private int value;

    public NumericField() {
        this.type = "numeric";
    }

    public NumericField(int value) {
        this.type = "numeric";
        this.value = value;
    }
}