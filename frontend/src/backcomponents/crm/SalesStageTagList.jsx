import React from 'react';

const stageStyleConfig = {
  "需求分析": "bg-purple-100 text-purple-800",
  "提案": "bg-orange-100 text-orange-800",
  "初步接洽": "bg-blue-100 text-blue-800",
  "已成交": "bg-green-100 text-green-800",
  "已丟失": "bg-gray-200 text-gray-600",
  "談判": "bg-red-100 text-red-800",
  "default": "bg-gray-100 text-gray-500" // 提供一個預設值
};

const opportunities = [
  { id: 1, stage: "需求分析" },
  { id: 2, stage: "需求分析" },
  { id: 3, stage: "提案" },
  { id: 4, stage: "初步接洽" },
  { id: 5, stage: "已成交" },
  { id: 6, stage: "已丟失" },
  { id: 7, stage: "初步接洽" },
  { id: 8, stage: "需求分析" },
  { id: 9, stage: "需求分析" },
  { id: 10, stage: "談判" },
];

const SalesStageTagList = () => {
  return (
    <div className="w-full max-w-xs bg-white rounded-lg shadow-md">
      <div className="p-4 border-b">
        <h2 className="text-lg font-bold text-gray-700">銷售階段</h2>
      </div>
      <ul className="divide-y divide-gray-200">
        {opportunities.map(opp => {
          const styleClass = stageStyleConfig[opp.stage] || stageStyleConfig.default;

          return (
            <li key={opp.id} className="p-4 flex justify-center">
              <span className={`px-3 py-1 text-sm font-semibold rounded-full ${styleClass}`}>
                {opp.stage}
              </span>
            </li>
          );
        })}
      </ul>
    </div>
  );
};

export default SalesStageTagList;