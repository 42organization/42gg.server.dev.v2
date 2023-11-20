package com.gg.server.admin.tournament.controller;

import com.gg.server.admin.tournament.dto.TournamentAdminUpdateRequestDto;
import com.gg.server.admin.tournament.service.TournamentAdminService;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.checkerframework.checker.index.qual.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/pingpong/admin/tournament")
@Validated
public class TournamentAdminController {
    private final TournamentAdminService tournamentAdminService;

    /**
     * 토너먼트 정보 수정
     * @param tournamentId 업데이트 하고자 하는 토너먼트 id
     * @param tournamentAdminUpdateRequestDto 요청 데이터
     */
    @PatchMapping("/{tournamentId}")
    public ResponseEntity<Void> updateTournamentInfo(@PathVariable @Positive Long tournamentId,
        @Valid @RequestBody TournamentAdminUpdateRequestDto tournamentAdminUpdateRequestDto) {
        tournamentAdminService.updateTournamentInfo(tournamentId, tournamentAdminUpdateRequestDto);

        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    /**
     * 토너먼트 정보 삭제
     * @param tournamentId 삭제 하고자 하는 토너먼트 id
     */
    @DeleteMapping("/{tournamentId}")
    public ResponseEntity<Void> deleteTournamentInfo(@PathVariable @Positive Long tournamentId) {
        tournamentAdminService.deleteTournamentInfo(tournamentId);

        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }
}
