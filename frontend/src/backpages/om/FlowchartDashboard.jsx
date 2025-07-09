import React from 'react';
import { ProcessStep, Arrow, PlatformIcon } from '../../backcomponents/om/FlowchartComponents.jsx';

const processData = [
  { platform: '前台購物', icon: 'S', bgColor: 'bg-orange-500', steps: ['商店設定', '待出貨訂單', '運送中訂單', '庫存更新', '收款沖帳'] },
  { platform: '客戶訂單', icon: 'S', bgColor: 'bg-black', steps: ['商店設定', '待出貨訂單', '庫存更新', '收款沖帳'] },
];

const FlowchartDashboard = () => {
  return (
    <div className="p-8 bg-gray-50">
      <h1 className="text-2xl font-bold mb-6"></h1>

      <div className="space-y-8">
        {processData.map((flow, index) => (
          <div key={index} className="flex items-center space-x-4 p-4 bg-white rounded-lg shadow-sm">
            <PlatformIcon icon={flow.icon} bgColor={flow.bgColor} platform={flow.platform} />

            {flow.steps.map((step, stepIndex) => (
              <React.Fragment key={stepIndex}>
                <Arrow />
                <ProcessStep text={step} />
              </React.Fragment>
            ))}
          </div>
        ))}
      </div>
    </div>
  );
};

export default FlowchartDashboard;