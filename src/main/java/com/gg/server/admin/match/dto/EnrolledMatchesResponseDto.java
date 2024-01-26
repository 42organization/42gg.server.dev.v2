package com.gg.server.admin.match.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * {
 * 	matches: [
 * 		  {
 * 			startTime: 'yyyy-mm-ddThh:mm',
 * 			endTime: 'yyyy-mm-ddThh:mm',
 * 			isMatched : boolean,
 * 			players: [{
 * 						userId: number,
 * 						intraId: string,
 * 						mode: string,   // BOTH || NORMAL || RANK
 *                   }, ...]
 *        },
 * 	      ...
 * 	]
 * }
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EnrolledMatchesResponseDto {
    private List<EnrolledMatch> matches;

    public EnrolledMatchesResponseDto(List<EnrolledMatch> matches) {
        this.matches = matches;
    }
}
