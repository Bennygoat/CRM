package com.example.demo.dto.erp;


import com.example.demo.enums.InventoryAdjustmentType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class InventoryAdjustmentCreateDTO {

    @NotNull(message = "調整日期不可為空")
    private LocalDate adjustmentDate;

    @NotNull(message = "調整類型不可為空")
    private InventoryAdjustmentType adjustmentType;

    private String remarks;

    @NotEmpty(message = "調整明細不可為空")
    @Valid
    private List<InventoryAdjustmentDetailCreateDTO> details;
}

