package com.sevatech.ywca;

public class NonMember {

    private String name;
    private String phone;
    private String dob;
    private String email;
    private String gender;
    private String institute;
    private String location;
    private String interest;
    private String profession;


    NonMember(){}

    public String getName()
    {
        return name;
    }

    public String getPhone()
    {
        return phone;
    }

    public String getDob()
    {
        return dob;
    }

    public String getEmail()
    {
        return email;
    }

    public String getGender()
    {
        return gender;
    }

    public String getInstitute()
    {
        return institute;
    }

    public String getLocation()
    {
        return location;
    }

    public String getInterest()
    {
        return interest;
    }

    public String getProfession()
    {
        return profession;
    }

    public void setName(String name)
    {
        this.name=name;
    }

    public void setPhone(String phone)
    {
        this.phone=phone;
    }

    public void setDob(String dob)
    {
        this.dob=dob;
    }

    public void setEmail(String email)
    {
        this.email=email;
    }
    public void setGender(String gender)
    {
        this.gender=gender;
    }

    public void setInstitute(String institute)
    {
        this.institute=institute;
    }

    public void setLocation(String location)
    {
        this.location=location;
    }

    public void setInterest(String interest)
    {
        this.interest=interest;
    }

    public void setProfession(String profession)
    {
        this.profession=profession;
    }

    public NonMember(String name, String phone, String dob, String email, String gender, String institute, String location, String interest, String profession)
    {
        this.name=name;
        this.phone=phone;
        this.dob=dob;
        this.email=email;
        this.gender=gender;
        this.institute=institute;
        this.location=location;
        this.interest=interest;
        this.profession=profession;
    }

    public String toString()
    {
        //String result= "name=" +name  + " phone= " + phone + " dob= " + dob + " email= " + email + " gender= " + gender + " institute/organisation=" + institute + " Closest YWCA location=" + location + " interest "= interest ;
        return "name=" +name  + " phone= " + phone + " dob= " + dob + " email= " + email + " gender= " + gender + " institute/organisation=" + institute + " Closest YWCA location=" + location + "Interest= " + interest + "Profession "+ profession;

    }





}
