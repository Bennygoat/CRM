package com.example.demo.dto.response;

import com.example.demo.enums.OpportunityStage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SalesFunnelDto {

    private OpportunityStage stage;
    private String stageDisplayName;
    private Long totalCount;
    private BigDecimal totalExpectedValue;
    private List<OpportunityDto> opportunities;

}
