package com.ssginc.wms.ord;

import com.mysql.cj.xdevapi.Table;

import javax.swing.table.TableModel;

public class OrdService {
    // 주문 내역 ID를 가져오기 위한 전처리 메소드
    public static int[] getOrdIds(int[] rows, TableModel data) {
        int[] result = new int[rows.length];
        for (int i = 0; i < rows.length; i++) {
            result[i] = decodeOrderId(data.getValueAt(rows[i], 0).toString());
        }
        return result;
    }

    public static boolean checkStatus(int[] rows, TableModel data) {
        for (int i = 0; i < rows.length; i++) {
            if (data.getValueAt(rows[i], 9).toString().equals("completed")) {
                return false;
            }
        }
        return true;
    }

    public static String encondOrderId(int ordId) {
        StringBuilder sTemp = new StringBuilder();
        sTemp.append("O");      // 주문코드는 알파벳 O로 시작
        String convertedId = String.valueOf(ordId);

        // 자릿수 통합을 위해 0추가 (코드는 4자리로 구성)
        for (int i = 0; i < 4 - convertedId.length(); i++) {
            sTemp.append("0");
        }
        sTemp.append(convertedId);

        return sTemp.toString();
    }

    public static int decodeOrderId(String orderId) {
        for (int i = 0; i < orderId.length(); i++) {
            char c = orderId.charAt(i);
            if (Character.isDigit(c) && c > '0' && c < '9') {
                return Integer.parseInt(orderId.substring(i));
            }
        }
        return 0;
    }
}
