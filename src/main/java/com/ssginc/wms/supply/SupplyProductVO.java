package com.ssginc.wms.supply;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString

public class SupplyProductVO {  // KKH-04, KKH-05
    private int supplyId;
    private int productId;
    private String productName;
    private String categoryName;
    private int supplyPrice;
    private int supplyAmount;
    private int totalPrice;
    private LocalDateTime supplyTime;
}
