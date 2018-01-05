package com.example.ffbclient.common.baseadapter;

import android.content.Context;

import java.util.List;

public abstract class SimpleAdapter<T> extends BaseRecyclerAdapter<T, BaseRecyclerViewHolder>  {

    public SimpleAdapter(Context context, int layoutResId) {
        super(context, layoutResId);
    }

    public SimpleAdapter(Context context, int layoutResId, List<T> datas) {
        super(context, layoutResId, datas);
    }

}
