package com.ssginc.wms.supply;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString

public class ProductSupplyVO {
    private int supply_id;
    private int product_id;
    private String product_name;
    private String category_name;
    private int supply_price;
    private int supply_amount;
    private LocalDateTime supply_time;
}
