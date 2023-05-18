package com.gg.server.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    // error code 등록
    //404
    NOT_FOUND(404, "COMMON-ERR-404", "NOT FOUND"),
    //500
    //400 잘못된 요청 코드
    BAD_REQUEST(400, "COMMON-ERR-400", "BAD REQUEST"),
    VALID_FAILED(400, "GAME-ERR-400" , "Valid Test Failed."),
    BAD_ARGU(400, "ARGUMENT-ERR-400", "잘못된 argument 입니다."),

    SN001(400, "SN001", "요청하신 값은 현 null 입니다"),

    //user
    USER_NOT_FOUND(404, "UR100", "USER NOT FOUND"),

    //season
    SEASON_NOT_FOUND(404, "SE100", "SEASON NOT FOUND"),

    //rank
    RANK_NOT_FOUND(404, "RK100", "RANK NOT FOUND"),
    REDIS_RANK_NOT_FOUND(404, "RK101", "REDIS RANK NOT FOUND"),

    //authentication
    UNAUTHORIZED(401, "AU000", "UNAUTHORIZED"),

    //common
    INTERNAL_SERVER_ERR(500, "CM000","INTERNAL SERVER ERROR"),
    PAGE_NOT_FOUND(404, "CM001", "PAGE NOT FOUND"),


    ;
    private int status;
    private String errCode;
    private String message;

    public void setMessage(String msg) {
        this.message = msg;
    }
}
