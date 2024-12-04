
package com.ssginc.wms.incomeApply;

public class IncomeApplyService {
    // int --> String
    public static String encodeApplyId(int incomeApplyId) {
        StringBuilder sTemp = new StringBuilder();
        sTemp.append("IA");
        String convertedId = String.valueOf(incomeApplyId);

        for (int i = 0; i < 4 - convertedId.length(); i++) {
            sTemp.append("0");
        }
        sTemp.append(convertedId);

        return sTemp.toString();
    }
}
