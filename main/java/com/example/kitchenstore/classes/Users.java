package com.example.kitchenstore.classes;

public class Users {
    public static Users current_user=new Users();

    String email;
    String user_name;
    String password;
    String kitchen_id;
    String phone_number;

    public Users(String email, String user_name, String password, String kitchen_id, String phone_number) {
        this.email = email;
        this.user_name = user_name;
        this.password = password;
        this.kitchen_id = kitchen_id;
        this.phone_number = phone_number;
    }

    public Users() {
    }



    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getKitchen_id() {
        return kitchen_id;
    }

    public void setKitchen_id(String kitchen_id) {
        this.kitchen_id = kitchen_id;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }
}
