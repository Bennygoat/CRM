import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { TrophyOutlined, FireOutlined, LoadingOutlined } from '@ant-design/icons';

import MockAdapter from 'axios-mock-adapter';
const mock = new MockAdapter(axios, { delayResponse: 800 });

mock.onGet('/api/dashboard/latest-won-deals').reply(200, [
    { id: 11, name: "超級團購網 - 夏季限定冰品團購專案達成", amount: 70000, closeDate: "2025-02-15" },
    { id: 5, name: "全球百貨連鎖 - 精選冰淇淋獨家供應合約", amount: 45000, closeDate: "2025-02-10" },
    { id: 17, name: "時尚生活雜誌社 - 親子冰淇淋新品體驗專案", amount: 10000, closeDate: "2024-09-05" }
]);

mock.onGet('/api/dashboard/top-open-opportunities').reply(200, [
    { id: 16, name: "品味咖啡連鎖店 - 季節限定冰沙飲品供應", value: 150000, contactName: "方偉倫" },
    { id: 2, name: "快樂兒童樂園 - 限定款冰淇淋聯名開發", value: 120000, contactName: "蔡宗儒" },
    { id: 10, name: "品味咖啡連鎖店 - 冰淇淋咖啡系列聯名", value: 95000, contactName: "吳宗翰" }
]);

const InsightsSection = () => {
  const [latestDeals, setLatestDeals] = useState([]);
  const [topOpportunities, setTopOpportunities] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      setLoading(true);
      try {
        const [dealsRes, opportunitiesRes] = await Promise.all([
          axios.get('/api/dashboard/latest-won-deals'),
          axios.get('/api/dashboard/top-open-opportunities')
        ]);
        setLatestDeals(dealsRes.data);
        setTopOpportunities(opportunitiesRes.data);
      } catch (error) {
        console.error("無法獲取商機洞察資料:", error);
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, []);

  const currencyFormatter = new Intl.NumberFormat('zh-TW', { style: 'currency', currency: 'TWD', minimumFractionDigits: 0 });

  if (loading) {
    return (
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6 p-4">
        <div className="bg-white p-6 rounded-lg shadow-sm text-center"><LoadingOutlined className="text-2xl text-gray-400" /></div>
        <div className="bg-white p-6 rounded-lg shadow-sm text-center"><LoadingOutlined className="text-2xl text-gray-400" /></div>
      </div>
    );
  }

  return (
    <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
      {/* 最新成交紀錄 */}
      <div className="bg-white p-6 rounded-lg shadow-sm">
        <h3 className="text-lg font-bold text-gray-700 mb-4 flex items-center">
          <TrophyOutlined className="mr-2 text-yellow-500" />
          最新成交紀錄
        </h3>
        <ul className="space-y-4">
          {latestDeals.map(deal => (
            <li key={deal.id} className="flex justify-between items-center">
              <div>
                <p className="font-semibold text-gray-800">{deal.name}</p>
                <p className="text-sm text-gray-500">成交日期: {deal.closeDate}</p>
              </div>
              <p className="font-bold text-green-600">{currencyFormatter.format(deal.amount)}</p>
            </li>
          ))}
        </ul>
      </div>

      {/* 高價值商機列表 */}
      <div className="bg-white p-6 rounded-lg shadow-sm">
        <h3 className="text-lg font-bold text-gray-700 mb-4 flex items-center">
          <FireOutlined className="mr-2 text-red-500" />
          高價值商機
        </h3>
        <ul className="space-y-4">
          {topOpportunities.map(opp => (
            <li key={opp.id} className="flex justify-between items-center">
              <div>
                <p className="font-semibold text-gray-800">{opp.name}</p>
                <p className="text-sm text-gray-500">主要聯絡人: {opp.contactName}</p>
              </div>
              <p className="font-bold text-blue-600">{currencyFormatter.format(opp.value)}</p>
            </li>
          ))}
        </ul>
      </div>
    </div>
  );
};

export default InsightsSection;