package appraisal;

import java.util.Vector;

public class AppraisalObject {

    private Integer ID;

    private String name;

    private String description;

    //private Vector<Strategy> strategies;

//    private Integer strategiesNumber;

//    public AppraisalObject(Integer ID, String name, String description/*, Vector<Strategy> strategies*/) {
//        this.ID = ID;
//        this.name = name;
//        this.description = description;
//        //this.strategies = strategies;
//       // this.strategiesNumber = strategies.size();
//    }

    public AppraisalObject(String name, String description/*, Vector<Strategy> strategies*/) {
        this.name = name;
        this.description = description;
        //this.strategies = strategies;
        //this.strategiesNumber = strategies.size();
    }

//    public AppraisalObject(Integer ID, String name, String description) {
//        this.ID = ID;
//        this.name = name;
//        this.description = description;
//    }

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

//    public Vector<Strategy> getStrategies() {
//        return strategies;
//    }
//
//    public void setStrategies(Vector<Strategy> strategies) {
//        this.strategies = strategies;
//        this.strategiesNumber = strategies.size();
//    }

//    public Integer getStrategiesNumber() {
//        return strategiesNumber;
//    }

    //public void setStrategiesNumber(Integer strategiesNumber) {
//        this.strategiesNumber = strategiesNumber;
//    }

    public AppraisalObject() { }
}
