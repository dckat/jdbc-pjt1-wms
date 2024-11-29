package com.ssginc.wms.supply.VO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString

public class SupplyProductVO {
    private int supply_id;
    private int product_id;
    private String product_name;
    private String category_name;
    private int supply_price;
    private int supply_amount;
    private LocalDateTime supply_time;
}
