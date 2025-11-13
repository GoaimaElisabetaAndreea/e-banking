package ro.ppoo.banking.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
public class Client implements Serializable {
    private String firstname;
    private String lastname;
    private String email;
    private String phone;
    private List<BankAccount> accounts = new ArrayList<BankAccount>();
    private String CNP;
    private boolean gdprAccepted;

    public Client(String firstname, String lastname, String email, String phone, String CNP, boolean gdprAccepted) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.phone = phone;
        this.CNP = CNP;
        this.gdprAccepted = gdprAccepted;
    }

    // copy constructor
    public Client(Client other){
        this.firstname = other.firstname;
        this.lastname = other.lastname;
        this.email = other.email;
        this.phone = other.phone;
        this.CNP = other.CNP;
        this.gdprAccepted = other.gdprAccepted;
        this.accounts = new ArrayList<>();
        for(BankAccount account : other.accounts){
            this.accounts.add(new BankAccount(account));
        }
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<BankAccount> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<BankAccount> accounts) {
        this.accounts = accounts;
    }

    public String getCNP() {
        return CNP;
    }

    public void setCNP(String CNP) {
        this.CNP = CNP;
    }

    public boolean isGdprAccepted() {
        return gdprAccepted;
    }
}
