package com.ssginc.wms.incomeApply;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class IncomeApplyVO {
    private int applyId;
    private int productId;
    private String userId;
    private LocalDateTime applyTime;
    private Process applyStatus;

    public enum Process {
        pending,
        completed
    }
}
