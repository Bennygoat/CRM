import React from 'react';
import {
  SettingOutlined,
  FileSyncOutlined,
  CarOutlined,
  StockOutlined,
  DollarCircleOutlined,
  RightOutlined
} from '@ant-design/icons';

const getStepIcon = (stepName) => {
  switch (stepName) {
    case '商店設定': return <SettingOutlined />;
    case '待出貨訂單': return <FileSyncOutlined />;
    case '運送中訂單': return <CarOutlined />;
    case '庫存更新': return <StockOutlined />;
    case '收款沖帳': return <DollarCircleOutlined />;
    default: return null;
  }
};

export const ProcessStep = ({ text }) => (
  <div className="flex flex-col items-center justify-center text-center w-28 h-full">
    <div className="text-3xl text-gray-600 mb-2">
      {getStepIcon(text)}
    </div>
    <span className="font-semibold text-gray-700 text-sm">{text}</span>
  </div>
);

export const Arrow = () => (
  <div className="flex items-center justify-center text-gray-300 text-2xl">
    <RightOutlined />
  </div>
);

export const PlatformIcon = ({ icon, bgColor, platform }) => (
  <div className="flex flex-col items-center justify-center text-center w-28">
    <div className={`w-12 h-12 rounded-full flex items-center justify-center text-white font-bold text-2xl ${bgColor}`}>
      {icon}
    </div>
    <span className="font-semibold text-gray-700 mt-2">{platform}</span>
  </div>
);