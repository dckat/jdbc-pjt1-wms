package com.ssginc.wms.user;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor // 기본 생성자 추가
@AllArgsConstructor // 모든 필드를 초기화하는 생성자 추가
public class UserVO {
    private String userId;
    private String userPw;
    private String userName;
    private String userSsn;
    private String userEmail;
    private String userPhone;
    private String userAddress;
    private String userGrade; // 사용자 등급 (admin/customer)
}
