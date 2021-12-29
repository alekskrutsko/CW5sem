package personalInfo;

public class Appraiser extends User {

    private double salary;

    public Appraiser(){
        super();
        salary = 0;
    }

    public Appraiser(String login, String passwordMask, String firstName,
                     String secondName, String patronymic, String phone, Role role,
                     boolean blocked, double salary){
        super(login, passwordMask, firstName, secondName, patronymic, phone,
              role, blocked);
        this.salary = salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public double getSalary() {
        return salary;
    }
}
