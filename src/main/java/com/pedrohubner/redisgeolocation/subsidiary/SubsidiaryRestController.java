package com.pedrohubner.redisgeolocation.subsidiary;

import com.pedrohubner.redisgeolocation.subsidiary.model.request.SubsidiaryRequest;
import com.pedrohubner.redisgeolocation.subsidiary.model.response.SubsidiaryResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@AllArgsConstructor
@RequestMapping("/v1/subsidiaries")
public class SubsidiaryRestController {
    private final SubsidiaryService subsidiaryService;

    @PostMapping
    public Mono<SubsidiaryResponse> createSubsidiary(@RequestBody SubsidiaryRequest request) {
        return subsidiaryService.createAndGetSubsidiary(request);
    }

//    @GetMapping("/{subsidiaryCode}")
//    public Mono<SubsidiaryResponse> findSubsidiaryByCode(@PathVariable("subsidiaryCode") Long subsidiaryCode) {
//        System.out.println(subsidiaryCode);
//        return Mono.empty();
//    }
//
//    @PatchMapping("/{subsidiaryId}")
//    public Flux<SubsidiaryResponse> updateSubsidiary(
//            @PathVariable("subsidiaryId") Long subsidiaryId, @RequestBody List<SubsidiaryRequest> request
//    ) {
//        System.out.println(subsidiaryId + " - " + request);
//        return Flux.empty();
//    }
//
//    @DeleteMapping("/{subsidiaryId}")
//    public Mono<Void> deleteSubsidiary(@PathVariable("subsidiaryId") Long subsidiaryId) {
//        System.out.println(subsidiaryId);
//        return Mono.empty();
//    }
}
