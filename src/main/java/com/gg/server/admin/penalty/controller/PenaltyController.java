package com.gg.server.admin.penalty.controller;

import com.gg.server.admin.penalty.dto.PenaltyListResponseDto;
import com.gg.server.admin.penalty.dto.PenaltyParamDto;
import com.gg.server.admin.penalty.dto.PenaltyRequestDto;
import com.gg.server.admin.penalty.service.PenaltyService;
import com.gg.server.global.dto.PageRequestDto;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/pingpong/admin/")
public class PenaltyController {
    private final PenaltyService penaltyService;

    @PostMapping("penalty")
    public ResponseEntity givePenaltyToUser(@RequestBody @Valid PenaltyRequestDto requestDto) {
        penaltyService.givePenalty(requestDto.getIntraId(), requestDto.getPenaltyTime(), requestDto.getReason());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("penalty")
    public PenaltyListResponseDto getAllPenaltyUser(@ModelAttribute @Valid PageRequestDto pageRequestDto,
                                                    @RequestParam(required = false) String intraId, @RequestParam Boolean current) {
        Pageable pageable = PageRequest.of(pageRequestDto.getPage() - 1, pageRequestDto.getSize(),
                Sort.by("startTime").descending());
        if (intraId == null)
            return penaltyService.getAllPenalties(pageable, current);
        return penaltyService.getAllPenaltiesByIntraId(pageable, intraId, current);
    }

    @DeleteMapping("penalty/{penaltyId}")
    public ResponseEntity releasePenaltyUser(@PathVariable @Min(1) Long penaltyId) {
        penaltyService.deletePenalty(penaltyId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
