package com.ssginc.wms.inoutcomeManagement;

public class InoutcomeManagementService {

    public static String encodeIncomeId(int ordId) {
        StringBuilder sTemp = new StringBuilder();
        sTemp.append("I");
        String convertedId = String.valueOf(ordId);

        for (int i = 0; i < 4 - convertedId.length(); i++) {
            sTemp.append("0");
        }
        sTemp.append(convertedId);

        return sTemp.toString();
    }

    public static int decodeIncomeId(String orderId) {
        // 앞에서 부터 0이 나오지 않는 시점을 찾음
        // 주문 번호는 1부터 auto_increment 하므로 0이 아닌 문자가 포함됨
        // 아닌 위치부터 끝까지 잘라서 int로 변환하여야 함.
        for (int i = 0; i < orderId.length(); i++) {
            char c = orderId.charAt(i);
            //
            if (Character.isDigit(c) && c > '0' && c < '9') {
                return Integer.parseInt(orderId.substring(i));
            }
        }
        return 0;
    }

    public static String encodeOutcomeId(int ordId) {
        StringBuilder sTemp = new StringBuilder();
        sTemp.append("O");
        String convertedId = String.valueOf(ordId);

        for (int i = 0; i < 4 - convertedId.length(); i++) {
            sTemp.append("0");
        }
        sTemp.append(convertedId);

        return sTemp.toString();
    }

    public static int decodeOutcomeId(String orderId) {
        // 앞에서 부터 0이 나오지 않는 시점을 찾음
        // 주문 번호는 1부터 auto_increment 하므로 0이 아닌 문자가 포함됨
        // 아닌 위치부터 끝까지 잘라서 int로 변환하여야 함.
        for (int i = 0; i < orderId.length(); i++) {
            char c = orderId.charAt(i);
            //
            if (Character.isDigit(c) && c > '0' && c < '9') {
                return Integer.parseInt(orderId.substring(i));
            }
        }
        return 0;
    }

}
