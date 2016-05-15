package phuonglam.mobile.webservice.helper;

import android.content.Context;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * Created by Phuo on 4/24/2016.
 */
public class ConstantHelper {
    public static final String LOCALHOST = "http://192.168.1.96:8080/datingapp/";
    public static final String GLOBALHOST = "https://takeadate-ws.herokuapp.com/";
    public static final DisplayImageOptions DISPLAY_IMAGE_OPTIONS = new DisplayImageOptions.Builder()
            //.showStubImage(R.drawable.stub_image)
            .cacheInMemory()
            .cacheOnDisc()
                    //.imageScaleType(ImageScaleType.EXACT)
            .build();
    public static final ImageLoaderConfiguration getImgLoaderConfig(Context context){
        ImageLoaderConfiguration config= new ImageLoaderConfiguration.Builder(context)
                .memoryCacheSize(41943040)
                .discCacheSize(104857600)
                .threadPoolSize(10)
                .build();
        return config;
    }
}
