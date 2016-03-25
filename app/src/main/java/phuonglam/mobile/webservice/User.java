package phuonglam.mobile.webservice;

/**
 * Created by Phuo on 25/03/2016.
 */
public class User {
    public String name;
    public int status;
    public String username;
    public String email;
    public int userid;
    public User() {} // JAXB needs this

    public User(String u, String n, String e, int s, int i) {
        this.name = n;
        this.status = s;
        this.username = u;
        this.email = e;
        this.userid = i;
    }
}
