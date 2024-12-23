package com.ssginc.wms.supply;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString

public class CategoryProductVO {  // // KKH-01, KKH-03 - 발주 등록을 위해 재고 현황을 가져오는 용도
    private int categoryId;      // 카테고리 ID
    private String categoryName; // 카테고리 이름
    private int productId;       // 제품 ID
    private String productName;  // 제품 이름
    private int productAmount;   // 제품 수량
    private int supplyPrice;     // 공급 가격
    private int ordPrice;        // 주문 가격
}
