package com.ssginc.wms.product;

import com.ssginc.wms.util.DecodeId;

import javax.swing.table.TableModel;

public class ProductService {
    public static int[] getProductIds(int[] rows, TableModel model) {
        int[] result = new int[rows.length];

        for (int i = 0; i < rows.length; i++) {
            result[i] = DecodeId.decodeId(model.getValueAt(rows[i], 1).toString());
        }

        return result;
    }

    // int --> String
    public static String encodeProductId(int productId) {
        StringBuilder sTemp = new StringBuilder();
        sTemp.append("P");
        String convertedId = String.valueOf(productId);

        for (int i = 0; i < 4 - convertedId.length(); i++) {
            sTemp.append("0");
        }
        sTemp.append(convertedId);

        return sTemp.toString();
    }

    // int --> String
    public static String encodeCategoryId(int categoryId) {
        StringBuilder sTemp = new StringBuilder();
        sTemp.append("C");
        String convertedId = String.valueOf(categoryId);

        for (int i = 0; i < 4 - convertedId.length(); i++) {
            sTemp.append("0");
        }
        sTemp.append(convertedId);

        return sTemp.toString();
    }
}
