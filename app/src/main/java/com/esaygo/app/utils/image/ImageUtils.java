package com.esaygo.app.utils.image;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.esaygo.app.R;

/**
 * @author qianxunke
 * @email  736567805@qq.com
 * @date 2019-11-28 16:42
 * @desc 图片处理工具类
 */
public class ImageUtils {


    public static void showImageByUrl(Context context, String imgUrl, ImageView imageView) {
        Glide
                .with(context)
                .load(imgUrl)
                .centerCrop()
                .placeholder(R.drawable.ic_launcher_background)
                .into(imageView);

    }


    public static void showImageByRaw(Context context, String imgName, ImageView imageView) {
        Glide.with(context)
                .load("android.resource://com.ftlexpress.faxiangpda/raw/" + imgName)
                .into(imageView);
    }


}

