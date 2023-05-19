package com.gg.server.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    // error code 등록
    /** Common **/
    INTERNAL_SERVER_ERR(500, "CM001","INTERNAL SERVER ERROR"),
    NOT_FOUND(404, "CM002", "NOT FOUND"),
    BAD_REQUEST(400, "CM003", "BAD REQUEST"),
    UNAUTHORIZED(401, "CM004", "UNAUTHORIZED"),
    METHOD_NOT_ALLOWED(405, "CM005", "METHOD NOT ALLOWED"),
    VALID_FAILED(400, "GAME-ERR-400" , "Valid Test Failed."),
    BAD_ARGU(400, "ARGUMENT-ERR-400", "잘못된 argument 입니다."),
    SN001(400, "SN001", "요청하신 값은 현 null 입니다"),

    FB_NOT_FOUND(404, "FB100", "FB NOT FOUND"),
    AWS_S3_ERR(500, "CL001", "AWS S3 Error"),
    AWS_SERVER_ERR(500, "CL002", "AWS Error"),
    // SENDER
    SLACK_USER_NOT_FOUND(404, "SL001", "fail to get slack user info"),
    SLACK_CH_NOT_FOUND(404, "SL002", "fail to get user dm channel id"),
    SLACK_JSON_PARSE_ERR(400, "SL002", "json parse error"),
    SLACK_SEND_FAIL(400, "SL003","fail to send notification" ),
    ;
    private int status;
    private String errCode;
    private String message;

    public void setMessage(String msg) {
        this.message = msg;
    }
}
