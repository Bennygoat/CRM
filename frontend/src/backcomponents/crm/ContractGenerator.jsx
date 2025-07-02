import React from "react";
import jsPDF from "jspdf";
import font from "../../font/NotoSansTC-Regular-normal.js";

const ContractGenerator = ({ influencerName = "王小明", onFinish }) => {
  const generatePDF = () => {
    const doc = new jsPDF({
      orientation: "portrait",
      unit: "mm",
      format: "A4",
    });

    // ✅ 正確註冊中文字型
    doc.addFileToVFS("NotoSansTC-Regular.ttf", font);
    doc.addFont("NotoSansTC-Regular.ttf", "custom", "normal");
    doc.setFont("custom");

    const content = `
網紅行銷合作合約書

立約人：
甲方：心邦股份有限公司
統一編號：55646980
地址：彰化縣北斗鎮大新里斗中路103號

乙方：${influencerName}

第一條：合作目的與期間
目的：甲方委託乙方透過其經營之社群平台，為「哈根良野」進行宣傳推廣。
期間：自中華民國 2025 年 7 月 5 日起，至 2025 年 8 月 5 日止。

第二條：合作內容與時程
合作平台：Instagram
產出內容：
- Instagram貼文 1 篇
- 限時動態 3 則（需置頂精選）

內容要求：依乙方創作風格，需標註 #品牌活動 與官方帳號

第三條：合作費用與支付
總費用：新台幣 30,000 元整
支付：發布後 15 日內轉帳，須提供發票
銀行：台灣銀行 北斗分行
戶名：${influencerName}
帳號：123456789

（略）

甲方簽名：________________
乙方簽名：________________
日期：2025 年 7 月 2 日
`;

    const lines = doc.splitTextToSize(content, 180);
    doc.text(lines, 15, 20);

    // 🟢 根據乙方姓名命名檔案
    const fileName = `${influencerName}_合作合約書.pdf`;
    doc.save(fileName);

    if (typeof onFinish === "function") {
      onFinish();
    }
  };

  // ✅ 正確 return 放在 generatePDF 外層
  return (
    <div className="p-4 space-y-4">
      <h2 className="text-xl font-bold">合約確認</h2>
      <p>乙方名稱：{influencerName}</p>
      <button
        onClick={generatePDF}
        className="px-4 py-2 bg-blue-600 text-white rounded"
      >
        下載合約 PDF
      </button>
    </div>
  );
};

export default ContractGenerator;
