package appraisal;

public class AppraisalObject {

    private Integer ID;

    private Integer typeID;

    private String name;

    private String description;

    public AppraisalObject(Integer ID, Integer typeID,String name, String description) {
        this.ID = ID;
        this.typeID = typeID;
        this.name = name;
        this.description = description;
    }
    public AppraisalObject(Integer ID,String name, String description) {
        this.ID = ID;
        this.name = name;
        this.description = description;
    }
    public AppraisalObject(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public Integer getTypeID() {
        return typeID;
    }

    public void setTypeID(Integer typeID) {
        this.typeID = typeID;
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

    public AppraisalObject() { }
}
