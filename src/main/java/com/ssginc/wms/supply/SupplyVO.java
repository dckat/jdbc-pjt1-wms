package com.ssginc.wms.supply;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Setter
@Getter
@ToString

public class SupplyVO {  // KKH-02, KKH-08 - supply 테이블에 insert 하는 용도
    private int supply_id;
    private int supply_amount;
    private int product_id;
    private LocalDateTime supply_time;
}
