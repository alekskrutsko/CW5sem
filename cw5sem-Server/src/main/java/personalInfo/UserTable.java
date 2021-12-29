package personalInfo;

public class UserTable {

    private String login;

    private String firstName;

    private String secondName;

    private String patronymic;

    private String phone;

    private String role;

    private String status;

    public UserTable(String login, String firstName, String secondName, String patronymic,String phone, String role,
                     String status) {
        this.login = login;
        this.firstName = firstName;
        this.secondName = secondName;
        this.patronymic = patronymic;
        this.phone = phone;
        this.role = role;
        this.status = status;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
