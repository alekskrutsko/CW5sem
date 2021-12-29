package personalInfo;

public class Appraiser extends User {

    public Appraiser(){
        super();
        salary = 0;
    }

    public Appraiser(String login, String passwordMask, String firstName,
                     String surname, String patronymic, String phone, Role role,
                     boolean blocked, double salary){
        super(login, passwordMask, firstName, surname, patronymic, phone,
              role, blocked);
        this.salary = salary;
    }

    private double salary;

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public double getSalary() {
        return salary;
    }
}
