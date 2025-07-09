import React, { useState, useEffect } from "react";
import { Button, Modal, message, Descriptions } from "antd";
import { PlusOutlined } from "@ant-design/icons";
import axios from "../../api/axiosBackend";
import CRMOpportunityForm from "./CRMOpportunityForm";
import SalesFunnelBoard from "../../backcomponents/crm/SalesFunnelBoard.jsx";
import ContractGenerator from "../../backcomponents/crm/ContractGenerator.jsx";

export default function SalesFunnel() {
  const [columns, setColumns] = useState({});
  const [modalOpen, setModalOpen] = useState(false);
  const [contractModalOpen, setContractModalOpen] = useState(false);
  const [contractData, setContractData] = useState(null);
  const [detailModalOpen, setDetailModalOpen] = useState(false);
  const [selectedOpportunity, setSelectedOpportunity] = useState(null);

  // 輔助函數：智能選擇主標籤類型和顏色
  const mapTagDataToFrontend = (tags) => {
    const defaultTagType = "default";
    const defaultColor = "#D3D3D3"; // 灰色

    // 定義優先級和映射關係
    // 數字越小，優先級越高
    const tagPriority = {
      // 階段標籤 (低優先級，用於 fallback 或當沒有高優先級標籤時)
      "初步接洽": 50, "需求分析": 50, "提案": 50, "談判": 50,
      "已成交": 50, "已丟失": 50,

      // 非階段性標籤 (高優先級)
      "高價值": 1,
      "緊急": 2,
      "新客戶": 3,
      "重點追蹤": 4,
      "已擱置": 0, // 已擱置可能會是一個主導顏色
    };

    // 映射到 Ant Design 的 type 字符串 (如果 SalesFunnelBoard 使用此作 class 名稱)
    const typeMap = {
      "初步接洽": "processing",
      "需求分析": "success",
      "提案": "warning",
      "談判": "error",
      "已成交": "success",
      "已丟失": "default",
      "高價值": "gold",    // 新增
      "緊急": "red",      // 新增
      "新客戶": "blue",    // 新增
      "重點追蹤": "purple",  // 新增
      "已擱置": "gray",     // 新增
    };

    let selectedType = defaultTagType;
    let selectedColor = defaultColor;
    let highestPriority = Infinity; // 數字越小，優先級越高

    if (tags && tags.length > 0) {
      tags.forEach(tag => {
        const priority = tagPriority[tag.tagName] !== undefined ? tagPriority[tag.tagName] : Infinity;
        if (priority < highestPriority) {
          highestPriority = priority;
          selectedType = typeMap[tag.tagName] || defaultTagType;
          selectedColor = tag.color || defaultColor; // 直接從 tag 對象獲取顏色
        }
      });
    }

    return { type: selectedType, color: selectedColor };
  };

  useEffect(() => {
    const fetchFunnelData = async () => {
      try {
        const res = await axios.get("/opportunities/funnel");

        const funnelData = {};
        res.data.forEach((stageItem) => {
          const key = stageItem.stageDisplayName; // 這是後端返回的階段顯示名稱

          funnelData[key] = stageItem.opportunities.map((op) => {
            const tags = op.tags ?? [];
            // 確保 averageRating 是數字，即使後端傳來 null 或 undefined
            const rating = Math.round(op.averageRating ?? 0);

            const { type, color } = mapTagDataToFrontend(tags); // 使用輔助函數獲取類型和顏色

            return {
              id: `c${op.opportunityId}`,
              title: op.opportunityName,
              rating: rating,  // 直接傳遞計算好的評分數字
              type,            // 用於 ProTable 的 type 篩選或特定樣式
              color,           // 直接傳遞顏色代碼給 SalesFunnelBoard
              tags,            // 傳遞完整的 tags 陣列，可能包含多個標籤
              ...op,           // 展開原始商機數據
            };
          });
        });

        setColumns(funnelData);
      } catch (error) {
        console.error("載入商機漏斗失敗：", error);
        message.error("無法載入商機資料");
      }
    };

    fetchFunnelData();
  }, []); // 僅在組件掛載時執行一次

  const handleCreate = (formValues) => {
    // 這裡的邏輯只處理前端的即時顯示，不發送API請求
    const id = `c${Date.now()}`;
    const stageKey = formValues.stage ?? "初步接洽"; // 確保 stageKey 與後端 stageDisplayName 一致

    const tags = formValues.tags ?? [];
    const { type, color } = mapTagDataToFrontend(tags); // 對新增的商機也應用相同的映射

    const newOpportunity = {
      id,
      title: formValues.opportunityName,
      rating: Math.round(formValues.averageRating ?? 1),
      type,
      color,
      tags,
      ...formValues,
    };

    setColumns((prev) => ({
      ...prev,
      [stageKey]: [...(prev[stageKey] || []), newOpportunity],
    }));

    setModalOpen(false);
  };

  const handleCardDoubleClick = (opportunity) => {
    console.log("雙擊商機卡片:", opportunity);
    setSelectedOpportunity(opportunity);
    setDetailModalOpen(true);
  };

  const handleContractGenerated = (data) => {
    setContractData(data);
    setContractModalOpen(true);
  };

  return (
    <div className="p-4">
      <div className="flex justify-end items-center mb-4">
        <Button
          type="primary"
          size="large"
          icon={<PlusOutlined />}
          onClick={() => setModalOpen(true)}
        >
          新增商機
        </Button>
      </div>

      <SalesFunnelBoard
        columns={columns}
        setColumns={setColumns}
        onCardDoubleClick={handleCardDoubleClick}
        onContractGenerated={handleContractGenerated}
      />

      <Modal
        open={modalOpen}
        onCancel={() => setModalOpen(false)}
        footer={null}
        title="新增商機"
        destroyOnClose
      >
        <CRMOpportunityForm onSubmit={handleCreate} />
      </Modal>

      <Modal
        title="商機詳情"
        open={detailModalOpen}
        onCancel={() => setDetailModalOpen(false)}
        footer={null}
      >
        {selectedOpportunity && (
          <Descriptions column={1} bordered size="small">
            <Descriptions.Item label="商機名稱">
              {selectedOpportunity.opportunityName}
            </Descriptions.Item>
            <Descriptions.Item label="預估金額">
              ${selectedOpportunity.expectedValue?.toLocaleString()}
            </Descriptions.Item>
            <Descriptions.Item label="說明">
              {selectedOpportunity.description || "-"}
            </Descriptions.Item>
            <Descriptions.Item label="成交日">
              {selectedOpportunity.closeDate}
            </Descriptions.Item>
            <Descriptions.Item label="階段">
              {selectedOpportunity.stage}
            </Descriptions.Item>
            <Descriptions.Item label="客戶">
              {selectedOpportunity.customerName}
            </Descriptions.Item>
            <Descriptions.Item label="聯絡人">
              {selectedOpportunity.contactName || "-"}
            </Descriptions.Item>
            {/* 這裡顯示所有標籤和顏色 */}
            {selectedOpportunity.tags && selectedOpportunity.tags.length > 0 && (
              <Descriptions.Item label="標籤">
                {selectedOpportunity.tags.map((tag, index) => (
                  <span key={index} style={{
                    backgroundColor: tag.color,
                    padding: '4px 8px',
                    borderRadius: '4px',
                    marginRight: '4px',
                    color: 'black' // 根據背景色調整文字顏色
                  }}>
                    {tag.tagName}
                  </span>
                ))}
              </Descriptions.Item>
            )}
            {/* 顯示評分星星 */}
            {selectedOpportunity.rating !== undefined && (
              <Descriptions.Item label="評分">
                {"⭐".repeat(selectedOpportunity.rating)}
              </Descriptions.Item>
            )}
          </Descriptions>
        )}
      </Modal>

      <Modal
        open={contractModalOpen}
        onCancel={() => setContractModalOpen(false)}
        footer={null}
        title="合約已建立"
        destroyOnClose
      >
        {contractData ? (
          <ContractGenerator
            influencerName={contractData.title}
            onFinish={() => setContractModalOpen(false)}
          />
        ) : null}
      </Modal>
    </div>
  );
}
// import React, { useState, useEffect } from "react";
// import { Button, Modal, message, Descriptions } from "antd";
// import { PlusOutlined } from "@ant-design/icons";
// import axios from "../../api/axiosBackend";
// import CRMOpportunityForm from "./CRMOpportunityForm";
// import SalesFunnelBoard from "../../backcomponents/crm/SalesFunnelBoard.jsx";
//
// export default function SalesFunnel() {
//   const [columns, setColumns] = useState({});
//   const [modalOpen, setModalOpen] = useState(false);
//   const [detailModalOpen, setDetailModalOpen] = useState(false);
//   const [selectedOpportunity, setSelectedOpportunity] = useState(null);
//
//   useEffect(() => {
//     const fetchFunnelData = async () => {
//       try {
//         const res = await axios.get("/opportunities/funnel");
//
//         const funnelData = {};
//         res.data.forEach((stageItem) => {
//           const key = stageItem.stageDisplayName;
//
//           funnelData[key] = stageItem.opportunities.map((op) => {
//             const tags = op.tags ?? [];
//             const type = tags[0]?.tagName || "default"; // ✅ 取第一個 tagName 為 type
//
//             return {
//               id: `c${op.opportunityId}`,
//               title: op.opportunityName,
//               rating: Math.round(op.averageRating ?? 0),
//               type,
//               tags,
//               ...op,
//             };
//           });
//         });
//
//         setColumns(funnelData);
//       } catch (error) {
//         console.error("載入商機漏斗失敗：", error);
//         message.error("無法載入商機資料");
//       }
//     };
//
//     fetchFunnelData();
//   }, []);
//
//   const handleCreate = (formValues) => {
//     const id = `c${Date.now()}`;
//     const stageKey = formValues.stage ?? "INITIAL_CONTACT";
//
//     const tags = formValues.tags ?? [];
//     const type = tags[0]?.tagName || "default";
//
//     const newOpportunity = {
//       id,
//       title: formValues.opportunityName,
//       rating: Math.round(formValues.averageRating ?? 1),
//       type,
//       tags,
//       ...formValues,
//     };
//
//     setColumns((prev) => ({
//       ...prev,
//       [stageKey]: [...(prev[stageKey] || []), newOpportunity],
//     }));
//
//     setModalOpen(false);
//   };
//
//   const handleCardDoubleClick = (opportunity) => {
//     console.log("雙擊商機卡片:", opportunity);
//     setSelectedOpportunity(opportunity);
//     setDetailModalOpen(true);
//   };
//
//   return (
//     <div className="p-4">
//       <div className="flex justify-end items-center mb-4">
//         <Button
//           type="primary"
//           size="large"
//           icon={<PlusOutlined />}
//           onClick={() => setModalOpen(true)}
//         >
//           新增商機
//         </Button>
//       </div>
//
//       <SalesFunnelBoard
//         columns={columns}
//         setColumns={setColumns}
//         onCardDoubleClick={handleCardDoubleClick}
//       />
//
//       <Modal
//         open={modalOpen}
//         onCancel={() => setModalOpen(false)}
//         footer={null}
//         title="新增商機"
//         destroyOnClose
//       >
//         <CRMOpportunityForm onSubmit={handleCreate} />
//       </Modal>
//
//       <Modal
//         title="商機詳情"
//         open={detailModalOpen}
//         onCancel={() => setDetailModalOpen(false)}
//         footer={null}
//       >
//         {selectedOpportunity && (
//           <Descriptions column={1} bordered size="small">
//             <Descriptions.Item label="商機名稱">
//               {selectedOpportunity.opportunityName}
//             </Descriptions.Item>
//             <Descriptions.Item label="預估金額">
//               ${selectedOpportunity.expectedValue?.toLocaleString()}
//             </Descriptions.Item>
//             <Descriptions.Item label="說明">
//               {selectedOpportunity.description || "-"}
//             </Descriptions.Item>
//             <Descriptions.Item label="成交日">
//               {selectedOpportunity.closeDate}
//             </Descriptions.Item>
//             <Descriptions.Item label="階段">
//               {selectedOpportunity.stage}
//             </Descriptions.Item>
//             <Descriptions.Item label="客戶">
//               {selectedOpportunity.customerName}
//             </Descriptions.Item>
//             <Descriptions.Item label="聯絡人">
//               {selectedOpportunity.contactName || "-"}
//             </Descriptions.Item>
//           </Descriptions>
//         )}
//       </Modal>
//     </div>
//   );
// }
