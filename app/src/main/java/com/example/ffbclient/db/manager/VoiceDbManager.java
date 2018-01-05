package com.example.ffbclient.db.manager;

import com.example.ffbclient.db.VoiceBeanDao;
import com.example.ffbclient.db.base.BaseManager;
import com.example.ffbclient.model.VoiceBean;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.query.Query;

import java.util.List;

/**
 * Created by zhangyuanyuan on 2017/9/28.
 */

public class VoiceDbManager extends BaseManager<VoiceBean, Long> {
    @Override
    public AbstractDao<VoiceBean, Long> getAbstractDao() {
        return daoSession.getVoiceBeanDao();
    }

    public List<VoiceBean> queryVoiceByQuestion(String question) {
        Query<VoiceBean> build = null;
        try {
            build = getAbstractDao().queryBuilder()
                    .where(VoiceBeanDao.Properties.VoiceQuestion.eq(question))
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return build.list();
    }

    public List<VoiceBean> queryVoiceById(long id) {
        Query<VoiceBean> build = null;
        try {
            build = getAbstractDao().queryBuilder()
                    .where(VoiceBeanDao.Properties.Id.eq(id))
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return build.list();
    }
}
