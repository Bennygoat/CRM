package com.example.demo.controller;

import com.example.demo.dto.request.GenerateContractRequest;
import com.example.demo.dto.response.ContractDto;
import com.example.demo.entity.Contract;
import com.example.demo.service.ContractService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
public class ContractController {

    private final ContractService contractService;

    /**
     * 自動從商機產生合約的 API 端點。
     *
     * @param request 包含 opportunityId 的請求主體。
     * @return 包含新建立合約詳細資訊的 ResponseEntity。
     */
    @PostMapping("/generate")
    public ResponseEntity<ContractDto> generateContract(@RequestBody GenerateContractRequest request) {
        try {
            Contract newContract = contractService.generateContractFromOpportunity(request.getOpportunityId());
            ContractDto contractDto = convertToDto(newContract);
            return ResponseEntity.status(HttpStatus.CREATED).body(contractDto);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage(), e);
        } catch (Exception e) {
            // 處理未預期錯誤的通用例外處理器
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "發生未預期的錯誤", e);
        }
    }

    /**
     * 將 Contract 實體轉換為 ContractDto。
     * @param contract 要轉換的實體。
     * @return 產生的 DTO。
     */
    private ContractDto convertToDto(Contract contract) {
        return new ContractDto(
                contract.getId(),
                contract.getContractNumber(),
                contract.getTitle(),
                contract.getContractType().toString(),
                contract.getStatus().toString(),
                contract.getStartDate(),
                contract.getEndDate(),
                contract.getAmount(),
                contract.getOpportunity().getOpportunityId(),
                contract.getContact() != null ? contract.getContact().getContactId() : null,
                contract.getCreatedAt(),
                contract.getUpdatedAt()
        );
    }
}
