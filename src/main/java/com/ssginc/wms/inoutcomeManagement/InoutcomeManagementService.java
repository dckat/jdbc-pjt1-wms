package com.ssginc.wms.inoutcomeManagement;

public class InoutcomeManagementService {

    public static String encodeIncomeId(int ordId) {
        StringBuilder sTemp = new StringBuilder();
        sTemp.append("IC");
        String convertedId = String.valueOf(ordId);

        for (int i = 0; i < 4 - convertedId.length(); i++) {
            sTemp.append("0");
        }
        sTemp.append(convertedId);

        return sTemp.toString();
    }

    public static String encodeOutcomeId(int ordId) {
        StringBuilder sTemp = new StringBuilder();
        sTemp.append("OC");
        String convertedId = String.valueOf(ordId);

        for (int i = 0; i < 4 - convertedId.length(); i++) {
            sTemp.append("0");
        }
        sTemp.append(convertedId);

        return sTemp.toString();
    }


}
