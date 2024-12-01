package com.ssginc.wms.supply.VO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Setter
@Getter
@ToString

public class SupplyVO {  // KKH-02, KKH-08
    private int supply_id;
    private int supply_amount;
    private int product_id;
    private LocalDateTime supply_time;
}
