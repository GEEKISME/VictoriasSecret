package bean;

import com.google.gson.Gson;

/**
 * Created by Lxh on 2017/11/7.
 */

public class AppVersionBean {

    /**
     * IsSuccess : true
     * Values : 1.0
     */

    private boolean IsSuccess;
    private String Values;

    public static AppVersionBean objectFromData(String str) {

        return new Gson().fromJson(str, AppVersionBean.class);
    }

    public boolean isIsSuccess() {
        return IsSuccess;
    }

    public void setIsSuccess(boolean IsSuccess) {
        this.IsSuccess = IsSuccess;
    }

    public String getValues() {
        return Values;
    }

    public void setValues(String Values) {
        this.Values = Values;
    }
}
