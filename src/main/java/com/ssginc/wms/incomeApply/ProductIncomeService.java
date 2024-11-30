package com.ssginc.wms.incomeApply;

public class ProductIncomeService {

    public String getApplyStatusAsString(ProductIncomeApplyVO productIncomeApplyVO) {
        if (productIncomeApplyVO == null || productIncomeApplyVO.getApplyStatus() == null) {
            return "UNKNOWN";
        }
        return productIncomeApplyVO.getApplyStatus().name();
    }
}
