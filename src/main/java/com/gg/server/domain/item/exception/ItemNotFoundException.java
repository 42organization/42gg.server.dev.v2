package com.gg.server.domain.item.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.NotExistException;

public class ItemNotFoundException extends NotExistException {
    public ItemNotFoundException() {
        super("해당 아이템이 없습니다.", ErrorCode.ITEM_NOT_FOUND);
    }
}