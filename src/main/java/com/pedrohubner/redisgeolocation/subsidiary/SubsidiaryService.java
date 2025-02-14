package com.pedrohubner.redisgeolocation.subsidiary;

import com.pedrohubner.redisgeolocation.subsidiary.mapper.SubsidiaryResponseMapper;
import com.pedrohubner.redisgeolocation.subsidiary.model.request.SubsidiaryRequest;
import com.pedrohubner.redisgeolocation.subsidiary.model.response.SubsidiaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class SubsidiaryService {
    private final ReactiveRedisTemplate<String, SubsidiaryResponse> subsidiaryTemplate;

    public Mono<SubsidiaryResponse> createAndGetSubsidiary(SubsidiaryRequest request) {
        final var subsidiary = SubsidiaryResponseMapper.mapFrom(request);
        return this.createSubsidiary(subsidiary)
                .then(findSubsidiaryByCode(subsidiary.code()));
    }

    public Mono<Void> createSubsidiary(SubsidiaryResponse subsidiary) {
        final var key = String.valueOf(subsidiary.code());
        return subsidiaryTemplate
                .opsForValue()
                .set(key, subsidiary)
                .then();
    }

    public Mono<SubsidiaryResponse> findSubsidiaryByCode(Long subsidiaryCode) {
        return subsidiaryTemplate
                .opsForValue()
                .get(String.valueOf(subsidiaryCode));
    }
}
