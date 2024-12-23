
package com.ssginc.wms.supply;

public class SupplyService {
    // int --> String
    public static String encodeSupplyId(int supplyId) {
        StringBuilder sTemp = new StringBuilder();
        sTemp.append("S");
        String convertedId = String.valueOf(supplyId);

        for (int i = 0; i < 4 - convertedId.length(); i++) {
            sTemp.append("0");
        }
        sTemp.append(convertedId);

        return sTemp.toString();
    }
}
