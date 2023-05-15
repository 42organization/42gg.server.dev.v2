package com.gg.server.game;

import com.gg.server.domain.game.data.GameRepository;
import com.gg.server.domain.game.dto.GameTeamUser;
import com.gg.server.domain.game.type.StatusType;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
@RequiredArgsConstructor
public class GameServiceTest {
    @Autowired
    private GameRepository gameRepository;

    @Test
    void repositoryTest() throws Exception{
//        List<GameTeamUser> list = gameRepository.findGameInUser(647l, 0, 10);
//        System.out.println(list);
        Pageable pageable = PageRequest.of(0, 10);
        System.out.println(gameRepository.findGamesByUser("jaehejun", Arrays.asList(StatusType.END.name(), StatusType.LIVE.name()),pageable).getContent());
    }
}
