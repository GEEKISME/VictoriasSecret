package bean;

import com.google.gson.Gson;

/**
 * Created by Lxh on 2017/10/24.
 */

public class AddInfoSucBean {

    /**
     * IsSuccess : true
     * Values : 添加成功
     */

    private boolean IsSuccess;
    private String Values;

    public static AddInfoSucBean objectFromData(String str) {

        return new Gson().fromJson(str, AddInfoSucBean.class);
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
