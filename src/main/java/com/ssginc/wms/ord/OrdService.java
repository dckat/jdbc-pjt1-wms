package com.ssginc.wms.ord;

import com.mysql.cj.xdevapi.Table;
import com.ssginc.wms.util.DecodeId;

import javax.swing.table.TableModel;
import java.util.HashMap;
import java.util.Map;

public class OrdService {
    // 주문 내역 ID를 가져오기 위한 전처리 메소드
    public static int[] getOrdIds(int[] rows, TableModel data) {
        int[] result = new int[rows.length];
        for (int i = 0; i < rows.length; i++) {
            result[i] = DecodeId.decodeId(data.getValueAt(rows[i], 0).toString());
        }
        return result;
    }

    public static String encodeOrderId(int ordId) {
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

    public static Map<Integer, Integer> getOrderInfo(int[] proIds, int[] ordAmounts) {
        Map<Integer, Integer> result = new HashMap<>();
        for (int i = 0; i < proIds.length; i++) {
            if (result.get(proIds[i]) != null) {
                result.put(proIds[i], result.get(proIds[i]) + ordAmounts[i]);
            }
            else {
                result.put(proIds[i], ordAmounts[i]);
            }
        }
        return result;
    }

    public static int[] getOrdAmounts(int[] rows, TableModel model) {
        int[] result = new int[rows.length];
        for (int i = 0; i < rows.length; i++) {
            result[i] = Integer.parseInt(String.valueOf(model.getValueAt(rows[i], 4)));
        }
        return result;
    }

    public static boolean checkOrdStatus(int[] rows, TableModel model) {
        for (int i = 0; i < rows.length; i++) {
            String status = model.getValueAt(rows[i], 7).toString();
            if (status.equals("completed")) {
                return false;
            }
        }
        return true;
    }
}
