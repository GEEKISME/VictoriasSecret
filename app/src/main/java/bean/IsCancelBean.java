package bean;

import com.google.gson.Gson;

/**
 * Created by Lxh on 2017/11/19.
 */

public class IsCancelBean {

    /**
     * IsSuccess : true
     * Values : null
     */

    private boolean IsSuccess;
    private Object Values;

    public static IsCancelBean objectFromData(String str) {

        return new Gson().fromJson(str, IsCancelBean.class);
    }

    public boolean isIsSuccess() {
        return IsSuccess;
    }

    public void setIsSuccess(boolean IsSuccess) {
        this.IsSuccess = IsSuccess;
    }

    public Object getValues() {
        return Values;
    }

    public void setValues(Object Values) {
        this.Values = Values;
    }
}
