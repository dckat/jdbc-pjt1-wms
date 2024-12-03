package com.ssginc.wms.product;

import javax.swing.table.TableModel;

public class ProductService {
    public static int[] getProductIds(int[] rows, TableModel model) {
        int[] result = new int[rows.length];

        for (int i = 0; i < rows.length; i++) {
            result[i] = Integer.parseInt(String.valueOf(model.getValueAt(rows[i], 1)));
        }

        return result;
    }
}
