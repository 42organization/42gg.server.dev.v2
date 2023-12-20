package com.gg.server.domain.tournament.data;

import com.gg.server.domain.user.data.User;
import com.gg.server.utils.annotation.UnitTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;

@UnitTest
@DisplayName("TournamentUserUnitTest")
public class TournamentUserUnitTest {
    Tournament tournament;
    TournamentUser tournamentUser;
    User user;

    @Nested
    @DisplayName("DeleteTournament")
    class DeleteTournament {
        @Test
        @DisplayName("토너먼트 삭제 성공")
        void deleteSuccess() {
            //given
            user = Mockito.mock(User.class);
            tournament = new Tournament();
            tournamentUser = new TournamentUser(user, tournament, false, LocalDateTime.now());

            //when
            tournamentUser.deleteTournament();

            //then
            Assertions.assertNull(tournamentUser.getTournament());
        }
    }

    @Nested
    @DisplayName("UpdateIsJoined")
    class UpdateIsJoined {
        @Test
        @DisplayName("참가 정보 업데이트 성공")
        void updateSuccess() {
            //given
            tournamentUser = new TournamentUser();
            boolean isjoined = true;

            //when
            tournamentUser.updateIsJoined(isjoined);

            //then
            Assertions.assertEquals(isjoined, tournamentUser.getIsJoined());
        }
    }
}
