package com.ssginc.wms.inoutcomeManagement;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Setter
@Getter
@ToString
public class OutComeProductVO {
    private int ordId; // 출고 코드
    private int ordAmount; // 출고 수량
    private int productId; // 상품 코드
    private LocalDateTime ordCompleteTime; // 출고 완료 시간
    private String productName; // 상품 이름
    private int ordPrice; // 출고 단가
    private String categoryName; // 카테고리 이름
}