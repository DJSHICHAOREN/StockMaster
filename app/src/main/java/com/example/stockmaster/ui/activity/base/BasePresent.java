package com.example.stockmaster.ui.activity.base;

import androidx.appcompat.app.AppCompatActivity;

public class BasePresent<V extends AppCompatActivity> {
    protected V mView;

    public BasePresent(V view){
        mView = view;
    }

    /**
     * 解除绑定view，一般在onDestroy中调用
     */

    public void detachView() {
        this.mView = null;
    }

    /**
     * View是否绑定
     *
     * @return
     */
    public boolean isViewAttached() {
        return mView != null;
    }
}
