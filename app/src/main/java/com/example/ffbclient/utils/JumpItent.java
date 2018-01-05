/*
 * Created by 岱青海蓝信息系统(北京)有限公司 on 17-6-14 下午4:22
 * Copyright (c) 2017. All rights reserved.
 */

package com.example.ffbclient.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class JumpItent {
    public final static String REQUEST_CODE = "REQUEST_CODE";

    public static void jump(Activity activity, Class<?> cls) {
        Intent intent = new Intent(activity, cls);
        activity.startActivity(intent);
    }

    public static void jump(Activity activity, Class<?> cls, Bundle bundle) {
        Intent intent = new Intent(activity, cls);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    public static void jump(Activity activity, Class<?> cls, boolean isFinish) {
        Intent intent = new Intent(activity, cls);
        activity.startActivity(intent);
        if (isFinish) {
            activity.finish();
        }
    }

    public static void jump(Activity activity, Class<?> cls, int requestCode) {
        Intent intent = new Intent(activity, cls);
        intent.putExtra(REQUEST_CODE, requestCode);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void jump(Activity activity, Class<?> cls, Bundle bundle, int requestCode) {
        Intent intent = new Intent(activity, cls);
        if (bundle != null) {
            bundle.putInt(REQUEST_CODE, requestCode);
            intent.putExtras(bundle);
        }
        activity.startActivityForResult(intent, requestCode);
    }

    public static void jump(Activity activity, Class<?> cls, boolean isFinish, Bundle bundle) {
        Intent intent = new Intent(activity, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        activity.startActivity(intent);
        if (isFinish) {
            activity.finish();
        }
    }

    public static void jump(Activity activity, Class<?> cls, boolean isFinish, int requestCode) {
        Intent intent = new Intent(activity, cls);
        intent.putExtra(REQUEST_CODE, requestCode);
        activity.startActivityForResult(intent, requestCode);
        if (isFinish) {
            activity.finish();
        }
    }

    public static void jump(Activity activity, Class<?> cls, boolean isFinish, Bundle bundle, int requestCode) {
        Intent intent = new Intent(activity, cls);
        if (bundle != null) {
            bundle.putInt(REQUEST_CODE, requestCode);
            intent.putExtras(bundle);
        }
        activity.startActivityForResult(intent, requestCode);
        if (isFinish) {
            activity.finish();
        }
    }

    public static void jump(Activity activity, String action) {
        Intent intent = new Intent(action);
        activity.startActivity(intent);
    }

    public static void jump(Activity activity, String action, Bundle bundle) {
        Intent intent = new Intent(action);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    public static void jump(Activity activity, String action, int requestCode) {
        Intent intent = new Intent(action);
        intent.putExtra(REQUEST_CODE, requestCode);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void jump(Activity activity, String action, Bundle bundle, int requestCode) {
        Intent intent = new Intent(action);
        bundle.putInt(REQUEST_CODE, requestCode);
        intent.putExtras(bundle);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void jump(Activity activity, String action, Uri uri) {
        Intent intent = new Intent(action, uri);
        activity.startActivity(intent);
    }

    public static void jump(Activity activity, String action, Uri uri, int requestCode){
        Intent intent = new Intent(action, uri);
        intent.putExtra(REQUEST_CODE, requestCode);
        activity.startActivityForResult(intent, requestCode);
    }

}
