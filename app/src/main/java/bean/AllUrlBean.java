package bean;

import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by Lxh on 2017/10/22.
 */

public class AllUrlBean {


    private ArrayList<ValuesBean> Values;

    public static AllUrlBean objectFromData(String str) {

        return new Gson().fromJson(str, AllUrlBean.class);
    }

    public ArrayList<ValuesBean> getValues() {
        return Values;
    }

    public void setValues(ArrayList<ValuesBean> Values) {
        this.Values = Values;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public static class ValuesBean {
        /**
         * ID : D0D3112C-61CE-4C15-B053-E4AE694070E1
         * PhotoUrl : 11
         */

        private String ID;
        private String PhotoUrl;

        public static ValuesBean objectFromData(String str) {

            return new Gson().fromJson(str, ValuesBean.class);
        }

        public String getID() {
            return ID;
        }

        public void setID(String ID) {
            this.ID = ID;
        }

        public String getPhotoUrl() {
            return PhotoUrl;
        }

        public void setPhotoUrl(String PhotoUrl) {
            this.PhotoUrl = PhotoUrl;
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }
}
