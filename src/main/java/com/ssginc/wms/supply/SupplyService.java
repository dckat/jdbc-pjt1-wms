
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
    // String --> int
    public static int decodeSupplyId(String supplyId) {
        // 앞에서 부터 0이 나오지 않는 시점을 찾음
        // 주문 번호는 1부터 auto_increment 하므로 0이 아닌 문자가 포함됨
        // 아닌 위치부터 끝까지 잘라서 int로 변환하여야 함.
        for (int i = 0; i < supplyId.length(); i++) {
            char c = supplyId.charAt(i);
            //
            if (Character.isDigit(c) && c > '0' && c <= '9') {
                return Integer.parseInt(supplyId.substring(i));
            }
        }
        return 0;
    }
}
