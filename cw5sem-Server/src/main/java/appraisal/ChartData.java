package appraisal;

public class ChartData<DataType1, DataType2> {
    private DataType1 value1;
    private DataType2 value2;

    public ChartData(DataType1 value1, DataType2 value2) {
        this.value1 = value1;
        this.value2 = value2;
    }

    public ChartData() {
        value1 = null;
        value2 = null;
    }

    public DataType1 getValue1() {
        return value1;
    }

    public void setValue1(DataType1 value1) {
        this.value1 = value1;
    }

    public DataType2 getValue2() {
        return value2;
    }

    public void setValue2(DataType2 value2) {
        this.value2 = value2;
    }
}
