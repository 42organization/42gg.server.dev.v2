package com.gg.server.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    //user
    USER_NOT_FOUND(404, "UR100", "USER NOT FOUND"),

    //announcement
    AN_NOT_FOUND(404, "AN100", "ANNOUNCEMENT NOT FOUND"),
    AN_DUP(409, "AN300", "ANNOUNCEMENT DUPLICATION"),

    //season
    SEASON_NOT_FOUND(404, "SE100", "SEASON NOT FOUND"),
    SEASON_FORBIDDEN(403, "SE500", "SEASON FORBIDDEN ERROR"),
    SEASON_TIME_BEFORE(403, "SE501", "SEASON TIME BEFORE"),

    //slotmanagement
    SM_NOT_FOUND(404, "SM100", "SM NOT FOUND"),

    //rank
    RANK_NOT_FOUND(404, "RK100", "RANK NOT FOUND"),
    REDIS_RANK_NOT_FOUND(404, "RK101", "REDIS RANK NOT FOUND"),

    /** team **/
    TEAM_ID_NOT_MATCH(400, "TM201", "TEAM id 가 일치하지 않습니다."),

    /** game **/
    GAME_DB_NOT_VALID(500, "GM201", "GAME DB NOT CONSISTENCY"),
    SCORE_NOT_MATCHED(400, "GM202", "score 입력이 기존과 다릅니다."),
    GAME_NOT_FOUND(404, "GM101", "GAME 이 존재하지 않습니다."),

    /** Common **/
    INTERNAL_SERVER_ERR(500, "CM001","INTERNAL SERVER ERROR"),
    NOT_FOUND(404, "CM002", "NOT FOUND"),
    BAD_REQUEST(400, "CM003", "BAD REQUEST"),
    UNAUTHORIZED(401, "CM004", "UNAUTHORIZED"),
    METHOD_NOT_ALLOWED(405, "CM005", "METHOD NOT ALLOWED"),
    PAGE_NOT_FOUND(404, "CM006", "PAGE NOT FOUND"),
    VALID_FAILED(400, "GAME-ERR-400" , "Valid Test Failed."),
    BAD_ARGU(400, "ARGUMENT-ERR-400", "잘못된 argument 입니다."),

    //Feedback
    FB_NOT_FOUND(404, "FB100", "FB NOT FOUND"),

    /**
     * PChange
     **/
    PC_NOT_FOUND(404, "PC100", "PChange 가 존재하지 않습니다."),

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
