import React from "react";
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
} from "recharts";

const FunnelStackedBarChart = ({ rawData, title }) => {
  const reversed = [...rawData].reverse();
  const cumulativeData = reversed.map((_, index) => {
    const sliced = reversed.slice(index);
    const total = sliced.reduce((sum, item) => sum + item.value, 0);
    return {
      name: reversed[index].name,
      value: total,
    };
  }).reverse();

  return (
    <div>
      <h2 className="text-lg font-semibold mb-4">{title}</h2>
      <ResponsiveContainer width="100%" height={300}>
        <BarChart
          data={cumulativeData}
          layout="vertical"
          margin={{ top: 20, right: 30, left: 20, bottom: 5 }}
        >
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis type="number" />
          <YAxis type="category" dataKey="name" reversed /> {/* 👈 加上這行 */}
          <Tooltip />
          <Bar dataKey="value" fill="#4f46e5" barSize={40} />
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
};

export default FunnelStackedBarChart;
