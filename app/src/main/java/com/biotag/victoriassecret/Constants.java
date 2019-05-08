package com.biotag.victoriassecret;

/**
 * Created by Lxh on 2017/10/13.
 */

public class Constants {

    public static final String HELP                          =                          "http://211.152.45.196:8036/help";

    public static final String  BOUNDARY                     =                          "*****asdfagdfjhhg*******";
    //正式服务器
    public static final String  MAINHOSTFORMAL               =                          "https://yl.shyule.org/webapi/";
    //测试服务器
//    public static final String  MAINHOST                     =                          "http://211.152.45.196:8036/";
    public static final String  MAINHOST                     =                          "http://196.128.102.68:80/webapi/";
    public static final String  TAG                          =                          "tks";
    public static final String  URL_GETSTAFFPHOTO            =   MAINHOST          +    "api/photo/getstaffphoto/{staffid}";
    public static final String  URL_GETSTAFFPHOTO2           =   MAINHOST          +    "uploadImage/{path}";
    public static final String  URL_POSTPHOTO                =   MAINHOST          +    "api/Photo/LogPhoto";
    public static final String  URL_GETALLHEADNUM            =   MAINHOST          +    "api/Photo/SyncPhoto/{id}";
    public static final String  URL_GETSTAFFTHUMBPHOTO       =   MAINHOST          +    "api/Photo/GetStaffThumbPhoto/{id}";
    public static final String  URL_POSTINOUT_EMPLOYCARD     =   MAINHOST          +    "api/InOut/Add";
    public static final String  URL_POSTINOUT_TICKET         =   MAINHOST          +    "";
    /**
     * 获取app的版本号，以决定是否下载新的版本
     */
    public static final String APP_VERSION                   =   MAINHOST          +    "api/Version/Get/desktop";

    public static final String APP_FILE                      =   MAINHOST          +    "/setup/VictoriaSecret.apk";
    public static final String ISCANCEL_URL                  =   MAINHOST          +    "/api/Invitation/GetLast/{id}?chip={chip}";


    public static final String KEYA                          =                          "keyA";
    public static final String KEYB = "keyB";
    public static final int IDGROUPIDCARDTYPE_SECTOR = 1;
    public static final int IDCARD_SECTOR = 2;
    public static final int STAFFNO_SECTOR = 3;
    public static final int STAFFNAME_SECTORA = 4;
    public static final int STAFFNAME_SECTORB = 5;
    public static final int COMPANYNAME_SECTORA = 6;
    public static final int COMPANYNAME_SECTORB = 7;
    public static final int AREANOW_SECTOR = 8;
    public static final int LASTMODIFIEDTIME_SECTOR = 9;
    public static final int AREANO_SECTOR = 10;
    public static final int SEATNO_SECTOR = 10;
    public static final int IMAGEURL_SECTOR = 11;

    public static final int ERROR_CODE_SUCCESS = 0;
    public static final int ERROR_CODE_FAULT_AREA = 1;
    public static final int ERROR_CODE_HAS_ENTERED = 2;
    public static final int ERROR_CODE_NOT_TICKET_EMPLEECARD = 5;

    public static final int CHECK_EMPLOYEECARD_OK = 3;
    public static final int CHECK_EMPLOYEECARD_FAIL = 4;

    public static final int GROUPID_ALLPASS = 9;


    public static final int CHIP_INVITATION             = 1; //邀请函
    public static final int CHIP_EMPLOYEECARD           = 2; //工作证
    public static final int CHIP_TICKET                 = 3; //票
    public static final int CHIP_WRISTSTRAP             = 4; //手环
}
