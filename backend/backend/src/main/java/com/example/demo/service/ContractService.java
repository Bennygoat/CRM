package com.example.demo.service;

import com.example.demo.dto.request.ContractRequest;
import com.example.demo.entity.Contract;

import java.util.List;

/**
 * 合約服務的介面。
 * 定義了所有與合約相關的業務邏輯操作。
 */
public interface ContractService {

    /**
     * 根據給定的商機產生並儲存一份新合約。
     *
     * @param opportunityId 要從中產生合約的商機 ID。
     * @return 新建立並儲存的 Contract 實體。
     */
    Contract generateContractFromOpportunity(Long opportunityId);

    /**
     * 查詢所有合約。
     * @return 包含所有 Contract 實體的列表。
     */
    List<Contract> getAll();

    /**
     * 根據 ID 查詢單一合約。
     * @param id 合約 ID。
     * @return 找到的 Contract 實體。
     */
    Contract getById(Long id);

    /**
     * 更新現有合約的資訊。
     * @param id 要更新的合約 ID。
     * @param request 包含更新資訊的請求物件。
     * @return 更新後的 Contract 實體。
     */
    Contract update(Long id, ContractRequest request);

    /**
     * 軟刪除一份合約 (將其狀態標記為終止)。
     * @param id 要軟刪除的合約 ID。
     */
    void delete(Long id);
}
