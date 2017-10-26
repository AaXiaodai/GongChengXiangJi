package example.com.gongchengxiangji.glide;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * Created by Administrator on 2017/7/27.
 */
public class GlideImageMassage {
    /**
     * Glide的管理者对象
     * @param url
     * @param erroImg
     * @param emptyImg
     * @param iv
     * @param tag
     */
    public static void glideLoader(Context context, String url, int erroImg, int emptyImg, ImageView iv, int tag) {
        if (1 == tag) {
            Glide.with(context).load(url).placeholder(emptyImg).error(erroImg).transform(new RoundImageaa(context,10)).into(iv);
        }
    }
}
