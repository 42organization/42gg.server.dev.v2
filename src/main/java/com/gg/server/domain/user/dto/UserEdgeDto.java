package com.gg.server.domain.user.dto;

import com.gg.server.domain.user.type.EdgeType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserEdgeDto {
    private EdgeType edgeType;
}
