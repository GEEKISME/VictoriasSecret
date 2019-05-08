package NFC;

import android.util.Log;

/**
 * Created by Administrator on 2017-10-18.
 */

public class CardInfo {
    private final String TAG = this.getClass().getSimpleName();
    private String CardID;
    private String ID;
    private int IdCard_type ;
    private String IdCard;
    private int GroupID;
    private String StaffNo;
    private int CardType;
    private String StaffName;
    private String CompanyName;
    private String AreaNow = "";
    private String LastModifiedTime;
    private String AreaNo;
    private String SeatNo;
    private String ImageUrl;

    public String getCardID() {
        return CardID;
    }

    public void setCardID(String cardID) {
        CardID = cardID;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public int getIdCard_type() {
        return IdCard_type;
    }

    public void setIdCard_type(int idCard_type) {
        IdCard_type = idCard_type;
    }

    public String getIdCard() {
        return IdCard;
    }

    public void setIdCard(String idCard) {
        IdCard = idCard;
    }

    public int getGroupID() {
        return GroupID;
    }

    public void setGroupID(int groupID) {
        GroupID = groupID;
    }

    public String getStaffNo() {
        return StaffNo;
    }

    public void setStaffNo(String staffNo) {
        StaffNo = staffNo;
    }

    public int getCardType() {
        return CardType;
    }

    public void setCardType(int cardType) {
        CardType = cardType;
    }

    public String getStaffName() {
        return StaffName;
    }

    public void setStaffName(String staffName) {
        StaffName = staffName;
    }

    public String getCompanyName() {
        return CompanyName;
    }

    public void setCompanyName(String companyName) {
        CompanyName = companyName;
    }

    public String getAreaNow() {
        return AreaNow;
    }

    public void setAreaNow(String areaNow) {
        AreaNow = areaNow;
    }

    public String getLastModifiedTime() {
        return LastModifiedTime;
    }

    public void setLastModifiedTime(String lastModifiedTime) {
        LastModifiedTime = lastModifiedTime;
    }

    public String getAreaNo() {
        return AreaNo;
    }

    public void setAreaNo(String areaNo) {
        AreaNo = areaNo;
    }

    public String getSeatNo() {
        return SeatNo;
    }

    public void setSeatNo(String seatNo) {
        SeatNo = seatNo;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public void printInfo(){
        Log.i(TAG, "printInfo: CardID = " + CardID);
        Log.i(TAG, "printInfo: ID = " + ID);
        Log.i(TAG, "printInfo: IdCard_type = " + IdCard_type);
        Log.i(TAG, "printInfo: IdCard = " + IdCard);
        Log.i(TAG, "printInfo: GroupID = " + GroupID);
        Log.i(TAG, "printInfo: StaffNo = " + StaffNo);
        Log.i(TAG, "printInfo: CardType = " + CardType);
        Log.i(TAG, "printInfo: StaffName = " + StaffName);
        Log.i(TAG, "printInfo: CompanyName = " + CompanyName);
        Log.i(TAG, "printInfo: AreaNow = " + AreaNow);
        Log.i(TAG, "printInfo: LastModifiedTime = " + LastModifiedTime);
        Log.i(TAG, "printInfo: AreaNo = " + AreaNo);
        Log.i(TAG, "printInfo: SeatNo = " + SeatNo);
        Log.i(TAG, "printInfo: ImageUrl = " + ImageUrl);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("CardID = " + CardID + "\n");
        sb.append("ID = " + ID + "\n");
        sb.append("IdCard_type = " + IdCard_type + "\n");
        sb.append("IdCard = " + IdCard + "\n");
        sb.append("GroupID = " + GroupID + "\n");
        sb.append("StaffNo = " + StaffNo + "\n");
        sb.append("CardType = " + CardType + "\n");
        sb.append("StaffName = " + StaffName + "\n");
        sb.append("CompanyName = " + CompanyName + "\n");
        sb.append("AreaNow = " + AreaNow + "\n");
        sb.append("LastModifiedTime = " + LastModifiedTime + "\n");
        sb.append("AreaNo = " + AreaNo + "\n");
        sb.append("SeatNo = " + SeatNo + "\n");
        sb.append("ImageUrl = " + ImageUrl + "\n");
        return sb.toString();
    }



}
