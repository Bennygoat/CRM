package com.example.demo.dto.erp;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PurchaseOrderCreateDTO {

    @NotNull(message = "供應商ID不可為空") //TODO(joshkuei): delete
    private Long supplierId;

    @NotNull(message = "進貨日期不可為空")
    private LocalDate orderDate;
    
    private String currency = "TWD"; //TODO(joshkuei): delete


    @Size(max = 200, message = "備註不可超過200字")
    private String remarks;

    @NotEmpty(message = "進貨明細不可為空")
    @Valid 
    private List<PurchaseOrderDetailCreateDTO> details;

}
