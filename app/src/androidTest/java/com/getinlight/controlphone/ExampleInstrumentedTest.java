package com.getinlight.controlphone;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import com.getinlight.controlphone.db.dao.BlackNumberDao;

import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getContext;
import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.getinlight.controlphone", appContext.getPackageName());
    }

    //这个地方的测试用例没有通过  目前我也不知道如何搜索这个问题
    @Test
    public void insert() {
        BlackNumberDao dao = BlackNumberDao.getInstance(getContext());
        dao.insert("110", "1");
    }

}
