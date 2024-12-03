package com.ssginc.wms.product;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class ProductVO {
    private int ProductId;
    private String ProductName;
    private int orderPrice;
    private int supplyPrice;
    private int productAmount;
    private int categoryId;
}
