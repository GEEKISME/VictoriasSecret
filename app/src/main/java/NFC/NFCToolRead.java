package NFC;

import android.util.Log;

import com.biotag.victoriassecret.Constants;
import com.mwcard.Reader;

import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Map;

/**
 * Created by Administrator on 2017-10-19.
 */

public class NFCToolRead {

    private final String TAG = this.getClass().getSimpleName();
    private Reader reader = null;
    private Map<String,String> mKeyMap = null;
    private String defaultKey = "FFFFFFFFFFFF";
    public NFCToolRead(Reader reader){
        this.reader = reader;
    }

    public CardInfo readCard(String id){
        CardInfo cardInfo = new CardInfo();
        boolean result = false;
        try {
            mKeyMap = M1Key.CalculateKey(id);
            Log.i(TAG, "readCard: id = " + id);

            for (String key : mKeyMap.keySet()) {
                Log.i(TAG, "readCard: " + String.format("%s = %s",key,mKeyMap.get(key)));
            }
            cardInfo.setCardID(id);
            result = readIDGroupID(cardInfo);
            if(!result){
                return null;
            }
            result = readIDCard(cardInfo);
            if(!result){
                return null;
            }
            result = readStaffNo(cardInfo);
            if(!result){
                return null;
            }
            result = readStaffName(cardInfo);
            if(!result){
                return null;
            }
            result = readCompanyName(cardInfo);
            if(!result){
                return null;
            }
            result = readAreaNow(cardInfo);
            if(!result){
                return null;
            }
            result = readLastModifiedTime(cardInfo);
            if(!result){
                return null;
            }
            result = readAreaNo(cardInfo);
            if(!result){
                return null;
            }
            result = readSeatNo(cardInfo);
            if(!result){
                return null;
            }
            result = readImageUrl(cardInfo);
            if(!result){
                return null;
            }
            return cardInfo;
        } catch (Exception e) {
            e.printStackTrace();
            return  null;
        }

    }

    private boolean doAuthenticate(int sector){
        Log.w(TAG, "doAuthenticate: sector = " + sector);
        boolean error = false;
        try {
            reader.mifareAuth(1,sector,mKeyMap.get(Constants.KEYB));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            error = true;
        }
        try {
            Log.w(TAG, "doAuthenticate: error = " + error );
            if(error){
                reader.openCard(0);
                reader.mifareAuth(1,sector,defaultKey);
                return true;
            }
        } catch (Exception e) {}
        return  false;
    }

    private boolean readIDGroupID(CardInfo cardInfo){
        int sector = Constants.IDGROUPIDCARDTYPE_SECTOR;
        ByteArrayInputStream bais = null;
        try {

            if (doAuthenticate(sector)){
                String IDtmp = reader.mifareRead(reader.mifareBlockAbs(sector));
                String ID = IDtmp.substring(0, 8) + "-" + IDtmp.substring(8, 12) + "-" + IDtmp.substring(12, 16) + "-" + IDtmp.substring(16, 20) + "-" + IDtmp.substring(20, 32);
                Log.i(TAG, "readIDGroupID: ID = " + ID);
                cardInfo.setID(ID);

                String data = reader.mifareRead(reader.mifareBlockAbs(sector) + 1);

                byte[] databyte = Utils.hexStringToBytes(data);
                bais = new ByteArrayInputStream(databyte);

                byte[] GroupIDByte = new byte[4];
                bais.read(GroupIDByte);
                int GroupID = (int)Utils.byteArrayToLong(GroupIDByte);
                cardInfo.setGroupID(GroupID);

                int CardType = bais.read();
                cardInfo.setCardType(CardType);

//                String GroupIDtmp = reader.mifareRead(reader.mifareBlockAbs(sector) + 1).substring(0,8);
//                int GroupID = Integer.valueOf(GroupIDtmp,16);
//                Log.i(TAG, "readIDGroupID: GroupIDtmp = " + GroupIDtmp);
//                Log.i(TAG, "readIDGroupID: GroupID = " + GroupID);
//                cardInfo.setGroupID(GroupID);


                return  true;
            }else{
                return  false;
            }
        }catch (Exception e){
            return  false;
        }finally {
            try {
                if(bais != null)bais.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean readIDCard(CardInfo cardInfo){
        int sector = Constants.IDCARD_SECTOR;
        ByteArrayInputStream bais = null;
        try{
            if(doAuthenticate(sector)){
                int start = reader.mifareBlockAbs(sector);
                int end = reader.mifareBlockAbs(sector) + 3;

                String data = "";
                for(int i = start;i < end; i++) {
                    data += reader.mifareRead(i);
                }

                byte[] databyte = Utils.hexStringToBytes(data);
                bais = new ByteArrayInputStream(databyte);

                int IdCard_type = bais.read();
                cardInfo.setIdCard_type(IdCard_type);
                int IdCardByteLen = bais.read();
                byte[] IdCardByte = new byte[IdCardByteLen];
                bais.read(IdCardByte);
                String IdCard = new String(IdCardByte,"utf-8");
                Log.i(TAG, "readIDCard: IdCard = " + IdCard);
                cardInfo.setIdCard(IdCard);


                return true;
            }else {
                return  false;
            }
        }catch(Exception e){
            return false;
        }finally {
            try {
                if(bais != null)bais.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean readStaffNo(CardInfo cardInfo){
        int sector = Constants.STAFFNO_SECTOR;
        ByteArrayInputStream bais = null;
        try{
            if(doAuthenticate(sector)){
                int start = reader.mifareBlockAbs(sector);
                int end = reader.mifareBlockAbs(sector) + 3;

                String data = "";
                for(int i = start;i < end; i++) {
                    data += reader.mifareRead(i);
                }

                byte[] databyte = Utils.hexStringToBytes(data);
                bais = new ByteArrayInputStream(databyte);

                int StaffNoLen = bais.read();
                byte[] StaffNoByte = new byte[StaffNoLen];
                bais.read(StaffNoByte);
                String StaffNo = new String(StaffNoByte,"utf-8");
                cardInfo.setStaffNo(StaffNo);


                return true;
            }else {
                return  false;
            }
        }catch(Exception e){
            return false;
        }finally {
            try {
                if(bais != null)bais.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private boolean readStaffName(CardInfo cardInfo){
        ByteArrayInputStream bais = null;
        try{
            int sector = Constants.STAFFNAME_SECTORA;
            String data = "";
            for (;sector <=  Constants.STAFFNAME_SECTORB;sector ++){
                if(doAuthenticate(sector) ){
                    int start = reader.mifareBlockAbs(sector);
                    int end = reader.mifareBlockAbs(sector) + 3;

                    for (int i = start;i < end;i++) {
                        data += reader.mifareRead(i);
                    }
                }else{
                    Log.i(TAG, "readStaffName: doAuthenticate fail");
                    return false;
                }
            }
            byte[] databyte = Utils.hexStringToBytes(data);
            bais = new ByteArrayInputStream(databyte);

            int StaffNameByteLen = bais.read();
            byte[] StaffNameByte = new byte[StaffNameByteLen];
            bais.read(StaffNameByte);
            String StaffName = new String(StaffNameByte,"utf-8");
            cardInfo.setStaffName(StaffName);

            return true;
        }catch(Exception e){
            return false;
        }finally {
            try {
                if(bais != null)bais.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean readCompanyName(CardInfo cardInfo){
        ByteArrayInputStream bais = null;
        try{
            int sector = Constants.COMPANYNAME_SECTORA;
            String data = "";
            for (;sector <=  Constants.COMPANYNAME_SECTORB;sector ++){
                if(doAuthenticate(sector) ){
                    int start = reader.mifareBlockAbs(sector);
                    int end = reader.mifareBlockAbs(sector) + 3;

                    for (int i = start;i < end;i++) {
                        data += reader.mifareRead(i);
                    }
                }else{
                    Log.i(TAG, "readCompanyName: doAuthenticate fail");
                    return false;
                }
            }
            byte[] databyte = Utils.hexStringToBytes(data);
            bais = new ByteArrayInputStream(databyte);

            int CompanyNameByteLen = bais.read();
            byte[] CompanyNameByte = new byte[CompanyNameByteLen];
            bais.read(CompanyNameByte);
            String CompanyName = new String(CompanyNameByte,"utf-8");
            cardInfo.setCompanyName(CompanyName);

            return true;
        }catch(Exception e){
            return false;
        }finally {
            try {
                if(bais != null)bais.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private boolean readAreaNow(CardInfo cardInfo){
        int sector = Constants.AREANOW_SECTOR;
        ByteArrayInputStream bais = null;
        try{
            if(doAuthenticate(sector) ){
                int start = reader.mifareBlockAbs(sector);
                int end = reader.mifareBlockAbs(sector) + 3;

                String data = "";
                for (int i = start;i < end;i++) {
                    data += reader.mifareRead(i);
                }
                byte[] databyte = Utils.hexStringToBytes(data);
                bais = new ByteArrayInputStream(databyte);

                int AreaNowByteLen = bais.read();
                byte[] AreaNowByte = new byte[AreaNowByteLen];
                bais.read(AreaNowByte);
                String AreaNow = new String(AreaNowByte,"utf-8");
                cardInfo.setAreaNow(AreaNow);

                return true;
            }else{
                Log.i(TAG, "readStaffNo: doAuthenticate fail");
                return false;
            }


        }catch(Exception e){
            return false;
        }finally {
            try {
                if(bais != null)bais.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean readLastModifiedTime(CardInfo cardInfo){
        int sector = Constants.LASTMODIFIEDTIME_SECTOR;
        ByteArrayInputStream bais = null;
        try{
            if(doAuthenticate(sector) ){
                int start = reader.mifareBlockAbs(sector);
                int end = reader.mifareBlockAbs(sector) + 3;

                String data = "";
                for (int i = start;i < end;i++) {
                    data += reader.mifareRead(i);
                }
                byte[] databyte = Utils.hexStringToBytes(data);
                bais = new ByteArrayInputStream(databyte);

                int LastModifiedTimeByteLen = bais.read();
                byte[] LastModifiedTimeByte = new byte[LastModifiedTimeByteLen];
                bais.read(LastModifiedTimeByte);
                String LastModifiedTime = new String(LastModifiedTimeByte,"utf-8");
                cardInfo.setLastModifiedTime(LastModifiedTime);
                return true;
            }else{
                Log.i(TAG, "readStaffNo: doAuthenticate fail");
                return false;
            }


        }catch(Exception e){
            return false;
        }finally {
            try {
                if(bais != null)bais.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean readAreaNo(CardInfo cardInfo){
        int sector = Constants.AREANO_SECTOR;
        ByteArrayInputStream bais = null;
        try{
            if(doAuthenticate(sector) ){
                int start = reader.mifareBlockAbs(sector);
                int end = reader.mifareBlockAbs(sector) + 1;

                String data = "";
                for (int i = start;i < end;i++) {
                    data += reader.mifareRead(i);
                }
                byte[] databyte = Utils.hexStringToBytes(data);
                bais = new ByteArrayInputStream(databyte);

                int AreaNoByteLen = bais.read();
                byte[] AreaNoByte = new byte[AreaNoByteLen];
                bais.read(AreaNoByte);
                String AreaNo = new String(AreaNoByte,"utf-8");
                AreaNo = AreaNo.replaceAll("[^A-Z]","");
                String[] strArray = AreaNo.split("");
                AreaNo = StringUtils.join(strArray," ");
                Log.i(TAG, "readAreaNo: AreaNo = " + AreaNo);

                cardInfo.setAreaNo(AreaNo.trim());
                return true;
            }else{
                Log.i(TAG, "readStaffNo: doAuthenticate fail");
                return false;
            }


        }catch(Exception e){
            return false;
        }finally {
            try {
                if(bais != null)bais.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean readSeatNo(CardInfo cardInfo){
        int sector = Constants.SEATNO_SECTOR;
        ByteArrayInputStream bais = null;
        try{
            if(doAuthenticate(sector) ){
                int start = reader.mifareBlockAbs(sector) + 1;
                int end = reader.mifareBlockAbs(sector) + 3;

                String data = "";
                for (int i = start;i < end;i++) {
                    data += reader.mifareRead(i);
                }
                byte[] databyte = Utils.hexStringToBytes(data);
                bais = new ByteArrayInputStream(databyte);

                int SeatNoByteLen = bais.read();
                byte[] SeatNoByte = new byte[SeatNoByteLen];
                bais.read(SeatNoByte);
                String SeatNo = new String(SeatNoByte,"utf-8");
                Log.i(TAG, "readAreaNo: SeatNo = " + SeatNo);

                cardInfo.setSeatNo(SeatNo.trim());
                return true;
            }else{
                Log.i(TAG, "readStaffNo: doAuthenticate fail");
                return false;
            }


        }catch(Exception e){
            return false;
        }finally {
            try {
                if(bais != null)bais.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private boolean readImageUrl(CardInfo cardInfo){
        int sector = Constants.IMAGEURL_SECTOR;
        ByteArrayInputStream bais = null;
        try{
            if(doAuthenticate(sector) ){
                int start = reader.mifareBlockAbs(sector);
                int end = reader.mifareBlockAbs(sector) + 3;

                String data = "";
                for (int i = start;i < end;i++) {
                    data += reader.mifareRead(i);
                }
                byte[] databyte = Utils.hexStringToBytes(data);
                bais = new ByteArrayInputStream(databyte);

                int ImageUrlByteLen = bais.read();
                byte[] ImageUrlByte = new byte[ImageUrlByteLen];
                bais.read(ImageUrlByte);
                String ImageUrl = new String(ImageUrlByte,"utf-8");
                ImageUrl = ImageUrl.replaceAll(" ","");
                Log.i(TAG, "readAreaNo: AreaNo = " + ImageUrlByte);

                cardInfo.setImageUrl(ImageUrl);
                return true;
            }else{
                Log.i(TAG, "readStaffNo: doAuthenticate fail");
                return false;
            }


        }catch(Exception e){
            return false;
        }finally {
            try {
                if(bais != null)bais.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public boolean writeAreaNow(String AreaNow){
        int sector = Constants.AREANOW_SECTOR;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try{
            if(doAuthenticate(sector) ){
                byte[] AreaNowByte = AreaNow.getBytes("utf-8");

                baos.write(AreaNowByte.length);
                baos.write(AreaNowByte);

                byte[] data = baos.toByteArray();
                String strData = Utils.bytesToHexString(data);

                int block = reader.mifareBlockAbs(sector);
                reader.mifareWrite(block,strData);

                return  true;
            }else{
                Log.i(TAG, "readStaffNo: doAuthenticate fail");
                return false;
            }
        }catch(Exception e){
            Log.i(TAG, "xxx writeAreaNow: error + " + e.getMessage());
            e.printStackTrace();
            return false;
        }finally {
            try {
                if(baos != null)baos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
