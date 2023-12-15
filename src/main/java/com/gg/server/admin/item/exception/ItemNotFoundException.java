package com.gg.server.admin.item.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.NotExistException;

public class ItemNotFoundException extends NotExistException {
    public ItemNotFoundException() {
        super("아이템이 없습니다.", ErrorCode.ITEM_NOT_FOUND);
    }
}
