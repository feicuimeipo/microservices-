package com.nx.logger.model.api;

import com.nx.logger.model.api.dto.BuryingPointLogDTO;

import javax.validation.Valid;

/**
 *
 * 埋点
 */
public interface BuryingPointLogApi {
    void createBuryingPoint(@Valid BuryingPointLogDTO createDTO);
}
