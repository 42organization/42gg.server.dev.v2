package com.gg.server.domain.user.exception;

import com.gg.server.global.exception.ErrorCode;

import com.gg.server.global.exception.custom.NotExistException;

public class UserEdgeTypeNotFound extends NotExistException {
    public UserEdgeTypeNotFound() {
        super("user edge type is not valid", ErrorCode.USER_EDGE_TYPE_NOT_FOUND);
    }
}
