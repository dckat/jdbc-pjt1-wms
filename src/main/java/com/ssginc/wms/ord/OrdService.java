package com.ssginc.wms.ord;

import com.mysql.cj.xdevapi.Table;

import javax.swing.table.TableModel;

public class OrdService {
    // 주문 내역 ID를 가져오기 위한 전처리 메소드
    public static int[] getOrdIds(int[] rows, TableModel data) {
        int[] result = new int[rows.length];
        for (int i = 0; i < rows.length; i++) {
            result[i] = Integer.parseInt(data.getValueAt(rows[i], 0).toString());
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
}
