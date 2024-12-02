package com.ssginc.wms.ord;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class OrdVO {
    private int orderId;
    private int productId;
    private String userId;
    private int orderAmount;
    private String orderStatus;
    private LocalDateTime orderTime;
    private LocalDateTime orderCompleteTime;
}
