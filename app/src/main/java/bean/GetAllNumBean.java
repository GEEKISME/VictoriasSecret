package bean;

import com.google.gson.Gson;

/**
 * Created by Lxh on 2017/10/22.
 */

public class GetAllNumBean {

    /**
     * IsSuccess : true
     * Values : [{"ID":"D0D3112C-61CE-4C15-B053-E4AE694070E1    ","PhotoUrl":"11"},{"ID":"8363372F-4BAF-4AF8-9884-1F68E07C22C9    ","PhotoUrl":"11"},{"ID":"40EC3905-BA43-4E9C-93F7-260D97676E69    ","PhotoUrl":"11"},{"ID":"B55DA994-C68A-43C2-87A2-36CAE26A0074    ","PhotoUrl":"20171012\\636434027810883233.gif"},{"ID":"7F676BE2-7A98-4921-B7BB-44A6A47DE9B3    ","PhotoUrl":"20171012\\636434112764643250.gif"},{"ID":"6387C107-635A-4295-84CF-3DA9E1E6F86A    ","PhotoUrl":"20171012\\636434124199374614.gif"},{"ID":"B91E31EA-FFEC-42AA-8F4E-C95E813A9A25    ","PhotoUrl":"20171012\\636434134071524597.gif"},{"ID":"01946CA8-2D5E-4706-BB27-8B7E3AD5EC66    ","PhotoUrl":"20171013\\636434846832793357.gif"},{"ID":"BB9E9C57-1541-4E18-9AE2-39E4E65B1888    ","PhotoUrl":"20171013\\636434881072056592.gif"},{"ID":"85AD994E-FCB0-4DA5-AD1D-C7AA5E079D11    ","PhotoUrl":"20171013\\636434911061670596.gif"},{"ID":"8DB29F44-FEF0-438D-A500-3F1984D2F511    ","PhotoUrl":"20171013\\636435096516043960.gif"},{"ID":"D9F139ED-23FF-413D-956C-7DC7F77125B1    ","PhotoUrl":"20171013\\636435107431089924.gif"},{"ID":"EA3D2674-4EE5-4C98-B7CC-31F25B0A6949    ","PhotoUrl":"20171013\\636435112055341886.gif"},{"ID":"E53A2DD6-2777-4FE3-A33B-8BAE9F0E891F    ","PhotoUrl":""},{"ID":"4D527C40-7056-4240-8DCB-7FBC3F33EE6A    ","PhotoUrl":"20171017\\636438444152669007.gif"},{"ID":"C7A5FCFB-DC91-4BDB-91DD-2D4A5E7A0361    ","PhotoUrl":"20171019\\636440011285901146.gif"},{"ID":"CD4CB7D4-A9BE-4CD4-AA4A-6ED6B351BDFF    ","PhotoUrl":"20171019\\636440014291100083.gif"},{"ID":"EAB06189-2313-43FB-8641-5F56E2006849    ","PhotoUrl":"20171019\\636440020303978519.gif"},{"ID":"92A157D5-3AB5-4BE3-87F1-7F09CFE9E925    ","PhotoUrl":"20171020\\636440929568242703.gif"},{"ID":"56DB0E46-9965-4A39-B30B-046488E05201    ","PhotoUrl":"20171020\\636440930875608029.gif"},{"ID":"6EC244FD-BB83-486D-8856-AF504F796111    ","PhotoUrl":"20171020\\636440935599192452.gif"},{"ID":"3280BA7B-8DC4-4132-A352-0EB45098ACA4    ","PhotoUrl":"20171019\\636440014291100083.gif"}]
     */

    private boolean IsSuccess;
    private String Values;

    public static GetAllNumBean objectFromData(String str) {

        return new Gson().fromJson(str, GetAllNumBean.class);
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
