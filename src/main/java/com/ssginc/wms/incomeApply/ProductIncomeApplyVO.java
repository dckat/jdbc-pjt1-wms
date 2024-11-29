package com.ssginc.wms.incomeApply;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;


@Setter
@Getter
@ToString
public class ProductIncomeApplyVO {
    private int applyId;
    private int productId;
    private String productName;
    private String categoryName;
    private String userId;
    private LocalDateTime applyTime;
    private Process applyStatus;

    public enum Process {
        pending,
        completed
    }
}
