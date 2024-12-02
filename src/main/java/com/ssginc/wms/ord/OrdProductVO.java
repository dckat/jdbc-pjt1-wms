package com.ssginc.wms.ord;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class OrdProductVO {
    private int orderId;
    private int productId;
    private String productName;
    private int categoryId;
    private String categoryName;
    private int orderPrice;
    private int orderAmount;
    private int totalPrice;
    private LocalDateTime orderTime;
    private String orderStatus;
}