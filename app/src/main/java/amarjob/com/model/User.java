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
    private double latitude;
    private double longitude;
    private String nationalID;
    private String skillType;


    public User() {
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getNationalID() {
        return nationalID;
    }

    public User(String id, String name, String profileImage, String mobileNumber, String email, String address, String gender, String birthday, double latitude, double longitude, String nationalID, String skillType) {
        this.id = id;
        this.name = name;
        this.profileImage = profileImage;
        this.mobileNumber = mobileNumber;
        this.email = email;
        this.address = address;
        this.gender = gender;
        this.birthday = birthday;
        this.latitude = latitude;
        this.longitude = longitude;
        this.nationalID = nationalID;
        this.skillType = skillType;
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

    public String getSkillType() {
        return skillType;
    }
}
