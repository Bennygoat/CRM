package com.example.demo.service.impl;

import com.example.demo.dto.request.ContractRequest;
import com.example.demo.entity.Contract;
import com.example.demo.entity.Opportunity;
import com.example.demo.enums.ContractStatus;
import com.example.demo.enums.ContractType;
import com.example.demo.repository.ContractRepository;
import com.example.demo.repository.OpportunityRepository;
import com.example.demo.service.ContractService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {

    private final ContractRepository contractRepository;
    private final OpportunityRepository opportunityRepository;

    @Override
    @Transactional
    public Contract generateContractFromOpportunity(Long opportunityId) {
        // 1. 檢查此商機的合約是否已存在
        if (contractRepository.existsByOpportunityOpportunityId(opportunityId)) {
            throw new IllegalStateException("商機 ID " + opportunityId + " 的合約已存在。");
        }

        // 2. 獲取商機資料，這是合約的來源
        Opportunity opportunity = opportunityRepository.findById(opportunityId)
                .orElseThrow(() -> new EntityNotFoundException("找不到 ID 為 " + opportunityId + " 的商機。"));

        // 3. 使用商機的資料建立新的 Contract 物件
        Contract contract = Contract.builder()
                .contractNumber(generateUniqueContractNumber())
                .title("關於 " + opportunity.getOpportunityName() + " 的合約")
                .contractType(ContractType.INFLUENCER_MARKETING)
                .status(ContractStatus.DRAFT)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusYears(1))
                .amount(opportunity.getExpectedValue())
                .termsAndConditions("標準條款與條件適用。")
                .opportunity(opportunity)
                .contact(opportunity.getContact())
                .build();

        // 4. 將新合約儲存到資料庫
        return contractRepository.save(contract);
    }

    /**
     * 產生唯一的合約編號。
     * 格式範例: "CON-YYYYMMDD-XXXXX"
     * @return 一個唯一的合約字串識別碼。
     */
    private String generateUniqueContractNumber() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String uniquePart = UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        return String.format("CON-%s-%s", datePart, uniquePart);
    }

    @Override
    @Transactional(readOnly = true)
    public Contract getById(Long id) {
        return contractRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("找不到 ID 為 " + id + " 的合約。"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Contract> getAll() {
        return contractRepository.findAll();
    }

    @Override
    @Transactional
    public Contract update(Long id, ContractRequest request) {
        Contract contract = getById(id);

        // 根據請求中的資訊更新合約欄位
        contract.setTitle(request.getTitle());
        contract.setEndDate(request.getEndDate());
        contract.setAmount(request.getAmount());
        contract.setTermsAndConditions(request.getTermsAndConditions());

        return contractRepository.save(contract);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Contract contract = getById(id);

        // 執行軟刪除：將狀態改為 TERMINATED
        contract.setStatus(ContractStatus.TERMINATED);
        contractRepository.save(contract);
    }
}
