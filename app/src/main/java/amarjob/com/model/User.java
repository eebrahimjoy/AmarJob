package amarjob.com.model;

public class User {

    private String id;
    private String name;
    private String profileImage;
    private String mobileNumber;
    private String email;
    private String address;
    private String gender;
    private String birthday;

    public User() {
    }


    public User(String id, String name, String profileImage, String mobileNumber, String email, String address, String gender, String birthday) {
        this.id = id;
        this.name = name;
        this.profileImage = profileImage;
        this.mobileNumber = mobileNumber;
        this.email = email;
        this.address = address;
        this.gender = gender;
        this.birthday = birthday;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public String getGender() {
        return gender;
    }

    public String getBirthday() {
        return birthday;
    }
}
