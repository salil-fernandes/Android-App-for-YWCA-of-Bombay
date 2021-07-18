package com.sevatech.ywca.helper;

public class DetailsHelperClass {
    String Name, DOB, Email, PhoneNumber, Gender;

    public DetailsHelperClass(String name, String DOB, String email, String phoneNumber, String gender) {
        Name = name;
        this.DOB = DOB;
        Email = email;
        PhoneNumber = phoneNumber;
        Gender = gender;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDOB() {
        return DOB;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }
}
