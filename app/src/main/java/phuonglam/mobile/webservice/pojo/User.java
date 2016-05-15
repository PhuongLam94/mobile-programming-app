package phuonglam.mobile.webservice.pojo;

/**
 * Created by Phuo on 25/03/2016.
 */
public class User {
    private String username1;
    private String test; //some problem with username, so use test instead
    private String test2; //some problem with phonenumber, so use test2 instead
    private String email;
    private String name;
    private int id;
    private int status;
    private String phonenumber;
    private String address;
    private int height;
    private int weight;
    private String avatar;
    private String password;

    private String message;
    private int gender;
    private String birthday;
    private int friendStatus; //-1: no relationship,0: current user, 1: friend with current user, 2: request sent, 3: request received
    private float longitude;
    private float lattitude;

    public User(){
        message="";
    }

    public String getUserName(){return this.username1;}
    public void setUserName(String username){this.username1 = username;}

    public String getTest(){return this.test;}
    public void setTest(String test){this.test = test;}

    public String getTest2(){return this.test2;}
    public void setTest2(String test2){this.test2 = test2;}

    public String getEmail(){return this.email;}
    public void setEmail(String email){this.email = email;}

    public String getName(){return this.name;}
    public void setName(String name){this.name = name;}

    public int getId(){return this.id;}
    public void setId(int id){this.id = id;}

    public int getStatus(){return this.status;}
    public void setStatus(int status){this.status = status;}

    public String getPhoneNumber(){return this.phonenumber;}
    public void setPhoneNumber(String phonenumber){this.phonenumber = phonenumber;}

    public String getAddress(){return this.address;}
    public void setAddress(String address){this.address = address;}

    public int getHeight(){return this.height;}
    public void setHeight(int height){this.height = height;}

    public int getWeight(){return this.weight;}
    public void setWeight(int weight){this.weight = weight;}

    public String getAvatar(){return this.avatar;}
    public void setAvatar(String avatar){this.avatar = avatar;}

    public String getPassword(){return this.password;}
    public void setPassword(String password){this.password = password;}

    public String getMessage(){return this.message;}
    public void setMessage(String message){this.message = message;}

    public int getGender(){return this.gender;}
    public void setGender(int gender){this.gender = gender;}

    public String getBirthday(){return this.birthday;}
    public void setBirthday(String birthday){this.birthday = birthday;}

    public int getFriendStatus(){return this.friendStatus;}
    public void setFriendStatus(int friendStatus){this.friendStatus = friendStatus;}

    public float getLongitude(){return this.longitude;}
    public void setLongitude(float longitude){this.longitude = longitude;}

    public float getLattitude(){return this.lattitude;}
    public void setLattitude(float lattitude){this.lattitude = lattitude;}
}