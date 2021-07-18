package com.sevatech.ywca.helper;

public class ProfileData {

    private String sr_no, name, phone, closest_ywca, email, profession, address;

    public ProfileData() {

    }

    public ProfileData(String sr_no, String name, String phone, String closest_ywca, String email, String address) {
        this.sr_no = sr_no;
        this.name = name ;
        this.phone = phone;
        this.closest_ywca = closest_ywca;
        this.email = email;
        this.address = address;
    }

    public String getSr_no() {
        return sr_no;
    }

    public void setSr_no(String sr_no) {
        this.sr_no = sr_no;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getClosest_ywca() {
        return closest_ywca;
    }

    public void setClosest_ywca(String closest_ywca) {
        this.closest_ywca = closest_ywca;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
