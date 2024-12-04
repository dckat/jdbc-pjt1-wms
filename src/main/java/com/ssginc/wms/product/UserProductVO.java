package com.ssginc.wms.product;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserProductVO {
    private int productId;
    private String productName;
    private int categoryId;
    private String categoryName;
    private int orderPrice;
    private int productAmount;
}
