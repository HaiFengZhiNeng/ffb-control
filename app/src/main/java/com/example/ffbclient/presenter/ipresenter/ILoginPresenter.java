package com.example.ffbclient.presenter.ipresenter;

import com.example.ffbclient.common.presenter.BasePresenter;
import com.example.ffbclient.common.presenter.BaseView;
import com.example.ffbclient.model.UserInfo;

/**
 * Created by zhangyuanyuan on 2017/9/22.
 */

public abstract class ILoginPresenter implements BasePresenter {

    private ILoginView mBaseView;

    public ILoginPresenter(ILoginView baseView) {
        mBaseView = baseView;
    }

    public abstract void doLogin(UserInfo info);

    public interface ILoginView extends BaseView {


        void loginSuccess();

        void loginFail(String errMsg);
    }
}
