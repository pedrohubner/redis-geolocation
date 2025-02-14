package com.pedrohubner.redisgeolocation.deliveryarea.model.dto;

import com.pedrohubner.redisgeolocation.enums.GeoShapeEnum;
import lombok.Builder;

@Builder
//@ParameterObject
public record CoordinatesDTO(
//        @Parameter(
//                in = ParameterIn.QUERY,
//                example = "661d27347ad982132df08c40",
//                description = "Ids da áreas de atendimento",
//                array = @ArraySchema(schema = @Schema(example = "661d27347ad982132df08c40"))
//        )
        String key,
        Double longitude,
        Double latitude,
        Double width,
        Double height,
        Double distance,
        GeoShapeEnum shape
) {
}
