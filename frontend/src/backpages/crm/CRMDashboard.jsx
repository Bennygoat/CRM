import React, { useEffect, useState } from "react";
import axios from "../../api/axiosBackend";
import {
  PieChart, Pie, Cell, Tooltip as RechartsTooltip, LineChart, Line,
  XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Legend,
  BarChart, Bar, LabelList
} from "recharts";

const COLORS = ["#0088FE", "#00C49F", "#FFBB28", "#FF8042", "#8884D8", "#A4DE6C", "#D842C4"];
const RADIAN = Math.PI / 180;

const renderCustomizedLabel = ({ cx, cy, midAngle, innerRadius, outerRadius, percent }) => {
  const radius = innerRadius + (outerRadius - innerRadius) * 0.5;
  const x = cx + radius * Math.cos(-midAngle * RADIAN);
  const y = cy + radius * Math.sin(-midAngle * RADIAN);
  return (
    <text x={x} y={y} fill="#fff" textAnchor="middle" dominantBaseline="central" fontSize={14}>
      {`${(percent * 100).toFixed(0)}%`}
    </text>
  );
};

const CRMDashboard = () => {
  const [dashboardData, setDashboardData] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    axios
      .get("/dashboard")
      .then((res) => {
        setDashboardData(res.data);
      })
      .catch((err) => {
        console.error("儀表板資料請求失敗:", err);
      })
      .finally(() => {
        setLoading(false);
      });
  }, []);

  if (loading || !dashboardData) {
    return <div className="text-center p-8 text-lg">儀表板載入中...</div>;
  }



  const { 
    kpis = [], 
    stageDistribution = { title: "階段分佈", data: [] }, 
    stageValue = { title: "階段金額", data: [] },
    monthlyTrend = { title: "每月趨勢", data: [] } 
  } = dashboardData;

  return (
    <div className="p-6 space-y-8 bg-gray-50 min-h-screen">
      {/* --- KPI 卡片區塊 --- */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-6">
        {kpis.map((item, idx) => (
          <div key={idx} className="bg-white shadow-md rounded-xl p-4 transition-transform transform hover:scale-105">
            <p className="text-base text-gray-500">{item.title}</p>
            <p className="text-3xl font-bold text-gray-800">
             {new Intl.NumberFormat('en-US', { useGrouping: false }).format(item.value)}
              <span className="text-lg ml-1 font-normal">{item.unit}</span>
            </p>
          </div>
        ))}
      </div>

      {/* --- 圖表區塊 (並排顯示) --- */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* 左側：圓餅圖 (商機數量) */}
        <div className="bg-white shadow-md rounded-xl p-4">
          <h2 className="text-lg font-semibold mb-4">{stageDistribution.title}</h2>
          <ResponsiveContainer width="100%" height={400}>
            <PieChart>
              <Pie data={stageDistribution.data}
                    dataKey="value"
                    nameKey="name"
                    cx="50%"
                    cy="50%"
                    outerRadius={120}
                    labelLine={false}
                    label={renderCustomizedLabel}
              >
                {stageDistribution.data.map((entry, index) => <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />)}
              </Pie>
              <RechartsTooltip formatter={(value, name) => [new Intl.NumberFormat().format(value) + ' 筆', name]}/>
              <Legend layout="horizontal" align="center" verticalAlign="bottom" iconType="circle" />
            </PieChart>
          </ResponsiveContainer>
        </div>

        {/* 右側：長條圖 (商機金額) */}
        <div className="bg-white shadow-md rounded-xl p-4">
                  <h2 className="text-lg font-semibold mb-4">{stageValue.title}</h2>
                  <ResponsiveContainer width="100%" height={400}>
                    <BarChart data={stageValue.data} margin={{ top: 20, right: 30, left: 0, bottom: 5 }}>
                      <CartesianGrid stroke="#f5f5f5" />
                       <XAxis dataKey="name" fontSize={12} stroke="#666" padding={{ left: 10, right: 10 }} />
                      <YAxis tickFormatter={(value) => new Intl.NumberFormat("zh-TW", { notation: "compact" }).format(value)} fontSize={12} stroke="#666" />
                      <Tooltip
                        cursor={false}
                        contentStyle={{
                          background: '#fff',
                          border: '1px solid #ddd',
                          borderRadius: '5px'
                        }}
                        formatter={(value) => [new Intl.NumberFormat("zh-TW", { useGrouping: false }).format(value) + ' 元', "金額"]}
                      />

                      <Bar dataKey="value" radius={[5, 5, 0, 0]} maxBarSize={100}>
                        {stageValue.data.map((entry, index) => (
                          <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                        ))}
                      </Bar>
                    </BarChart>
                  </ResponsiveContainer>
                </div>
             </div>

     <div className="bg-white shadow-md rounded-xl p-4">
             <h2 className="text-lg font-semibold mb-4">商機漏斗分析</h2>
             <ResponsiveContainer width="100%" height={350}>
               <BarChart
                 layout="vertical"
                 data={stageDistribution.data}
                 margin={{ top: 5, right: 50, left: 30, bottom: 5 }}
               >
                 <CartesianGrid stroke="#f5f5f5" />
                 <XAxis type="number" stroke="#666" />
                 <YAxis
                   type="category"
                   dataKey="name"
                   stroke="#666"
                   fontSize={12}
                   width={80}
                 />

                 <Tooltip
                   cursor={{ fill: 'rgba(240, 240, 240, 0.5)' }}
                   formatter={(value, name) => [new Intl.NumberFormat().format(value) + ' 筆', "數量"]}
                 />
                 <Legend />
                 <Bar dataKey="value" name="商機數" radius={[0, 5, 5, 0]}>
                   <LabelList dataKey="value" position="right" fill="#666" />
                   {stageDistribution.data.map((entry, index) => (
                     <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                   ))}
                 </Bar>
               </BarChart>
             </ResponsiveContainer>
           </div>


      {/* --- 近一年商機趨勢 --- */}
      <div className="bg-white shadow-md rounded-xl p-4">
        <h2 className="text-lg font-semibold mb-4">{monthlyTrend.title}</h2>
        <ResponsiveContainer width="100%" height={300}>
          <LineChart data={monthlyTrend.data}
            margin={{ top: 5, right: 20, left: 20, bottom: 20 }}
          >
            <CartesianGrid strokeDasharray="3 3" />
            <XAxis dataKey="name" />
            <YAxis />
            <Tooltip />
            <Line
                type="monotone"
                name="新增商機數"
                dataKey="value"
                stroke="#8884d8"
                strokeWidth={2}
                activeDot={{ r: 8 }}
            />
          </LineChart>
        </ResponsiveContainer>
      </div>
    </div>
  );
};

export default CRMDashboard;