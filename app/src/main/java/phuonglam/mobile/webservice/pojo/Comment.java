package phuonglam.mobile.webservice.pojo;

/**
 * Created by Phuo on 4/24/2016.
 */
public class Comment {
    private int userid;
    private int pictureid;
    private int id;
    private String content;
    private String time;
    private String name;

    public int getUserid(){ return this.userid;}
    public void setUserid(int userid){this.userid = userid;}

    public int getPictureid(){ return this.pictureid;}
    public void setPictureid(int pictureid){this.pictureid = pictureid;}

    public int getId(){ return this.id;}
    public void setId(int id){this.id = id;}

    public String getContent(){return this.content;}
    public void setContent(String content){this.content = content;}

    public String getTime(){return this.time;}
    public void setTime(String time){this.time = time;}

    public String getName(){return this.name;}
    public void setName(String name){this.name = name;}
}
