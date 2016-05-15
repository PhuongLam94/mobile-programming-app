package phuonglam.mobile.webservice.helper;

import org.json.JSONObject;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import phuonglam.mobile.webservice.pojo.Comment;
import phuonglam.mobile.webservice.pojo.ImageResponse;
import phuonglam.mobile.webservice.pojo.Message;
import phuonglam.mobile.webservice.pojo.Password;
import phuonglam.mobile.webservice.pojo.Picture;
import phuonglam.mobile.webservice.pojo.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

/**
 * Created by Phuo on 4/23/2016.
 */
public interface MyApiEndpointInterface {
    @POST("postservice/user/add")
    Call<Message> createUser(@Body User use);

    @PUT("putservice/user/edit")
    Call<Message> editUser(@Body User user);

    @GET("getservice/checkuser/0/0")
    Call<ResponseBody> checkUser();

    @GET("getservice/checkusername/{username}")
    Call<Message> checkUserName(@Path("username") String username);

    @GET("getservice/checkuseremail/{email}")
    Call<Message> checkEmail(@Path("email") String email);

    @GET("getservice/getpicture/{userId}/all")
    Call<List<Picture>> getListPicture(@Path("userId") String userId);

    @GET("getservice/getpicture/{userId}/all")
    Call<ResponseBody> getListPictureTemp(@Path("userId") String userId);


    @GET("getservice/getpicture/{userId}/{pictureId}")
    Call<Picture> getPicture(@Path("userId") String userId, @Path("pictureId") String pictureId);

    @GET("getservice/getfriend/{userId}")
    Call<List<User>> getFriend(@Path("userId") String userId);

    @GET("getservice/searchuser/{userId}/{name}")
    Call<List<User>> searchName(@Path("userId") String userId, @Path("name") String name);

    @GET("getservice/searchuserbyage/{userId}/{fromAge}/{toAge}")
    Call<List<User>> searchAge(@Path("userId") String userId, @Path("fromAge") String fromAge, @Path("toAge") String toAge);

    @POST("postservice/comment/add")
    Call<List<Comment>> addComment(@Body Comment comment);

    @GET("getservice/getfriendpicture/{userId}/{offset}")
    Call<List<Picture>> getFriendPicture(@Path("userId") String userId, @Path("offset") String offset);

    @PUT("putservice/setfriendstatus/{user1Id}/{user2Id}/{friendStatus}")
    Call<Message> setFriendStatus(@Path("user1Id") String user1, @Path("user2Id") String user2, @Path("friendStatus") String friendStatus);

    @GET("getservice/getnear/{userId}/{lon}/{lat}")
    Call<List<User>> getNear(@Path("userId") String userId, @Path("lon") double lon, @Path("lat") double lat);

    @DELETE("/deleteservice/deletepicture/{userId}/{pictureId}")
    Call<Message> deletePicture(@Path("userId") String userId, @Path("pictureId") String pictureId);

    @Multipart
    @POST("3/image")
    Call<ImageResponse> postImage(@Header("Authorization") String auth,@Part("image") RequestBody body);

    @POST("postservice/picture/add")
    Call<Message> addPicture(@Body Picture picture);

    @PUT("putservice/setPassword")
    Call<Message> setPassword(@Body Password password);


}
