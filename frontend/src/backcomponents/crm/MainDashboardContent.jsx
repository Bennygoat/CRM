import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import { SoundOutlined, PlusCircleOutlined, TeamOutlined, CalendarOutlined, FunnelPlotOutlined, PushpinOutlined, RightCircleOutlined } from '@ant-design/icons';

import MockAdapter from 'axios-mock-adapter';
const mock = new MockAdapter(axios, { delayResponse: 500 });
mock.onGet('/api/dashboard/announcements').reply(200, [
    { "id": 1, "title": "夏季限定「愛文芒果雪酪」新口味正式上市！", "date": "2025-07-07" },
    { "id": 2, "title": "重要：出貨前請再次確認冰品車溫度，確保冷鏈品質", "date": "2025-07-04" },
    { "id": 3, "title": "本週末「台中夏日美食節」攤位(A3)需要支援人力，請洽管理部", "date": "2025-07-02" }
]);

mock.onGet('/api/dashboard/system-stats').reply(200, {
    totalCustomers: 1280,
    totalContacts: 3500,
    totalOpportunities: 20
});

const QuickActions = () => {
    const navigate = useNavigate();
    const actions = [
        { name: '新增商機', icon: <PlusCircleOutlined />, path: '/crm/opportunity/new' },
        { name: '新增客戶', icon: <TeamOutlined />, path: '/crm/company' },
        { name: '行事曆', icon: <CalendarOutlined />, path: '/crm/calender' },
        { name: '銷售漏斗', icon: <FunnelPlotOutlined />, path: '/crm/salesfunnel' },
    ];
    return (
        <div className="grid grid-cols-4 gap-4 text-center">
            {actions.map(action => (
                <div key={action.name} onClick={() => navigate(action.path)} className="bg-white p-4 rounded-lg shadow-sm hover:shadow-md hover:bg-gray-50 cursor-pointer transition-all">
                    <div className="text-3xl text-blue-500 mb-2">{action.icon}</div>
                    <p className="font-semibold text-gray-700">{action.name}</p>
                </div>
            ))}
        </div>
    );
};

// 公告元件
const Announcements = () => {
    const [announcements, setAnnouncements] = useState([]);
    useEffect(() => {
        axios.get('/api/dashboard/announcements').then(res => setAnnouncements(res.data));
    }, []);
    return (
        <div className="bg-white p-6 rounded-lg shadow-sm">
            <h3 className="text-lg font-bold text-gray-700 mb-4 flex items-center"><SoundOutlined className="mr-2" />公司公告</h3>
            <ul className="space-y-3">
                {announcements.map(item => (
                    <li key={item.id} className="flex justify-between items-center text-gray-600 hover:text-blue-600 cursor-pointer">
                        <span>{item.title}</span>
                        <span className="text-sm text-gray-400">{item.date}</span>
                    </li>
                ))}
            </ul>
        </div>
    );
};

// 重點商機追蹤
const KeyOpportunities = () => {
  const keyOpportunitiesData = [
    { id: 2, name: "快樂兒童樂園 - 限定款冰淇淋聯名開發", stage: "需求分析", nextAction: "提交初步的卡通角色設計概念圖。", bgColor: "bg-blue-50", borderColor: "border-blue-500" },
    { id: 1, name: "冰城食品 - 酷夏超市獨家夏季冰棒批發案", stage: "談判", nextAction: "與採購經理敲定最終年度合約價格。", bgColor: "bg-green-50", borderColor: "border-green-500" },
    { id: 16, name: "品味咖啡連鎖店 - 季節限定冰沙飲品供應", stage: "提案", nextAction: "寄送樣品並預約下週的產品試吃會議。", bgColor: "bg-orange-50", borderColor: "border-orange-500" },
  ];
  return (
    <div className="bg-white p-6 rounded-lg shadow-sm">
      <h3 className="text-lg font-bold text-gray-700 mb-4 flex items-center"><PushpinOutlined className="mr-2" />本週重點商機追蹤</h3>
      <div className="space-y-4">
        {keyOpportunitiesData.map(opp => (
          <div key={opp.id} className={`p-4 rounded-lg border-l-4 ${opp.borderColor} ${opp.bgColor}`}>
            <div className="flex justify-between items-center"><p className="font-bold text-gray-800">{opp.name}</p><span className="text-xs font-semibold bg-gray-200 text-gray-700 px-2 py-1 rounded-full">{opp.stage}</span></div>
            <div className="mt-2 flex items-start text-sm text-gray-600"><RightCircleOutlined className="mr-2 mt-1 text-gray-400" /><span>{opp.nextAction}</span></div>
          </div>
        ))}
      </div>
    </div>
  );
};


const MainDashboardContent = () => {
    return (
        <div className="space-y-6">
            <QuickActions />
            <Announcements />
            <KeyOpportunities />
        </div>
    );
};

export default MainDashboardContent;