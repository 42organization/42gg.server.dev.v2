package com.gg.server.domain.user.dto;

import com.gg.server.domain.receipt.data.Receipt;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserReceiptDto {
    @NotNull
    private Long receiptId;
}
