package com.gg.server.admin.tournament.controller;

import com.gg.server.admin.tournament.dto.TournamentAdminAddUserRequestDto;
import com.gg.server.admin.tournament.dto.TournamentAdminAddUserResponseDto;
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
import com.gg.server.admin.tournament.dto.TournamentAdminCreateRequestDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/pingpong/admin/tournaments")
@Validated
public class TournamentAdminController {
    private final TournamentAdminService tournamentAdminService;

    /***
     * 토너먼트 생성
     * @param tournamentAdminCreateRequestDto 생성에 필요한 데이터
     * @return "CREATED" 응답 코드
     */
    @PostMapping()
    public ResponseEntity<Void> createTournament(@RequestBody @Valid TournamentAdminCreateRequestDto tournamentAdminCreateRequestDto) {
        tournamentAdminService.createTournament(tournamentAdminCreateRequestDto);
        return new ResponseEntity<Void>(HttpStatus.CREATED);
    }

    /**
     * <p>토너먼트 정보 수정</p>
     * @param tournamentId 업데이트 하고자 하는 토너먼트 id
     * @param tournamentAdminUpdateRequestDto 요청 데이터
     * @return HttpStatus.NO_CONTENT
     */
    @PatchMapping("/{tournamentId}")
    public ResponseEntity<Void> updateTournamentInfo(@PathVariable @Positive Long tournamentId,
        @Valid @RequestBody TournamentAdminUpdateRequestDto tournamentAdminUpdateRequestDto) {
        tournamentAdminService.updateTournamentInfo(tournamentId, tournamentAdminUpdateRequestDto);

        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    /**
     * <p>토너먼트 정보 삭제</p>
     * @param tournamentId 삭제하고자 하는 토너먼트 id
     * @return HttpStatus.NO_CONTENT
     */
    @DeleteMapping("/{tournamentId}")
    public ResponseEntity<Void> deleteTournament(@PathVariable @Positive Long tournamentId) {
        tournamentAdminService.deleteTournament(tournamentId);

        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    /**
     * <p>토너먼트 유저 추가</p>
     * <p>이미 해당 토너먼트에 참여중이거나 대기자인 유저는 신청할 수 없다.</p>
     * @param tournamentId 유저를 추가할 토너먼트 id
     * @param tournamentAdminUserAddRequestDto 요청 데이터
     * @return TournamentAdminAddUserResponseDto, HttpStatus.CREATED
     */
    @PostMapping("/{tournamentId}/users")
    public ResponseEntity<TournamentAdminAddUserResponseDto> addTournamentUser(@PathVariable @Positive Long tournamentId,
        @Valid @RequestBody TournamentAdminAddUserRequestDto tournamentAdminUserAddRequestDto) {
        TournamentAdminAddUserResponseDto responseDto = tournamentAdminService.addTournamentUser(tournamentId, tournamentAdminUserAddRequestDto);

        return new ResponseEntity<TournamentAdminAddUserResponseDto>(responseDto, HttpStatus.CREATED);
    }
}
