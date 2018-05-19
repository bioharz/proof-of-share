package models.dto;

public class ChangePw {
    protected String passwordOld;
    protected String passwordNew;
    protected String passwordNewCheck;

    public String getPasswordOld() {
        return passwordOld;
    }

    public void setPasswordOld(String passwordOld) {
        this.passwordOld = passwordOld;
    }

    public String getPasswordNew() {
        return passwordNew;
    }

    public void setPasswordNew(String passwordNew) {
        this.passwordNew = passwordNew;
    }

    public String getPasswordNewCheck() {
        return passwordNewCheck;
    }

    public void setPasswordNewCheck(String passwordNewCheck) {
        this.passwordNewCheck = passwordNewCheck;
    }
}



