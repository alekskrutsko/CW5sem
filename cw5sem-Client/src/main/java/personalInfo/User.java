package personalInfo;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String login;
    private String passwordMask;
    private String firstName;
    private String surname;
    private String patronymic;
    private String phone;
    private Role role;
    private boolean blocked;

    public enum Role{
        CONSUMER(0),
        APPRAISER(1),
        ADMINISTRATOR(2);

        private int value;
        private static Map map = new HashMap<>();

        private Role(int value) {
            this.value = value;
        }

        static {
            for (Role roleType : Role.values()) {
                map.put(roleType.value, roleType);
            }
        }

        public static Role valueOf(int roleType) {
            return (Role) map.get(roleType);
        }

        public int getValue() {
            return value;
        }
    }

    public User(){
        login = null;
        passwordMask = null;
        firstName = null;
        surname = null;
        patronymic = null;
        phone = null;
        role = null;
        blocked = false;
    };

    public User(String login, String passwordMask, String firstName, String surname, String patronymic,
                String phone, Role role, boolean blocked){
        this.login = login;
        this.passwordMask = passwordMask;
        this.firstName = firstName;
        this.surname = surname;
        this.patronymic = patronymic;
        this.phone = phone;
        this.role = role;
        this.blocked = blocked;
    }

    public void setUser(User user){
        this.login = user.login;
        this.passwordMask = user.passwordMask;
        this.firstName = user.firstName;
        this.surname = user.surname;
        this.patronymic = user.patronymic;
        this.phone = user.phone;
        this.role = user.role;
        this.blocked = user.blocked;
    }

    public String getLogin() {
        return login;
    }

    public String getPasswordMask() {
        return passwordMask;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getSurname() {
        return surname;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public String getPhone() {return phone;}

    public void setLogin(String login) {this.login = login;}

    public void setPasswordMask(String passwordMask) {
        this.passwordMask = passwordMask;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Role getRole() {
        return role;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public boolean isBlocked() {
        return blocked;
    }
}
