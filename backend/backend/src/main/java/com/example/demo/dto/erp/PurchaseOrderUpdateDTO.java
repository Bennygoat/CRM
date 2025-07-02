package com.example.demo.dto.erp;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class PurchaseOrderUpdateDTO {

    // Fields that can be updated.
    // Supplier ID might not be updatable for an existing PO, or it might signify a major change.
    // For now, including it.
    @NotNull(message = "供應商ID不可為空")
    private Long supplierId;

    @NotNull(message = "進貨日期不可為空")
    private LocalDate orderDate;

    // Currency might be fixed after creation or updatable. Including for now.
    private String currency;

    @Size(max = 200, message = "備註不可超過200字")
    private String remarks;

    // Assuming the entire list of details is provided for update,
    // existing details not in the list might be removed or handled as per business logic.
    @NotEmpty(message = "進貨明細不可為空")
    @Valid
    private List<PurchaseOrderDetailUpdateDTO> details;

    // Status might be updated through a separate workflow/API,
    // but including it here if direct status update is allowed.
    // private com.example.demo.enums.PurchaseOrderStatus status;
    // Decided to handle status update implicitly or via dedicated methods if complex logic.
}

