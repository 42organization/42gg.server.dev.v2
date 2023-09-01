package com.gg.server.domain.pchange.service;

import com.gg.server.domain.game.data.Game;
import com.gg.server.domain.pchange.data.PChange;
import com.gg.server.domain.pchange.data.PChangeRepository;
import com.gg.server.domain.user.data.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.gg.server.domain.pchange.exception.PChangeNotExistException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PChangeService {
    private final PChangeRepository pChangeRepository;

    @Transactional
    public void addPChange(Game game, User user, Integer pppResult, Boolean isChecked) {
        pChangeRepository.save(new PChange(game, user, pppResult, isChecked));
    }

    public List<PChange> findExpChangeHistory(Long gameId, Long userId) {
        List<PChange> pChanges = pChangeRepository.findExpHistory(userId, gameId);
        if (pChanges.isEmpty()) {
            throw new PChangeNotExistException();
        }
        return pChanges;
    }

    public List<PChange> findPPPChangeHistory(Long gameId, Long userId, Long seasonId) {
        List<PChange> pChanges = pChangeRepository.findPPPHistory(userId, gameId, seasonId);
        if (pChanges.isEmpty()) {
            throw new PChangeNotExistException();
        }
        return pChanges;
    }
}
