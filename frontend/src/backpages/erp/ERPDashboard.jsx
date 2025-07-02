import React, { useEffect, useState } from "react";
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
  LineChart,
  Line,
} from "recharts";
import axios from "../../api/axiosBackend";
import { Card, Statistic, Table, Typography, Row, Col } from "antd";

const { Title } = Typography;

const ERPDashboard = () => {
  const [summary, setSummary] = useState(null);

  useEffect(() => {
    axios.get("/dashboard/erp-summary").then((res) => {
      setSummary(res.data);
    });
  }, []);

  if (!summary) return <p>Loading...</p>;

  const kpis = [
    {
      title: "本月銷售總額",
      value: summary.kpis.monthlySalesTotal,
      suffix: "元",
    },
    {
      title: "未付款訂單數",
      value: summary.kpis.unpaidSalesOrders,
    },
    {
      title: "低於安全庫存產品",
      value: summary.kpis.productsBelowSafetyStock,
    },
  ];

  const salesChartData = summary.salesComparison.labels.map((label, i) => ({
    月份: label,
    今年: summary.salesComparison.series[0].data[i],
    去年: summary.salesComparison.series[1].data[i],
  }));

  const inventoryChartData = summary.inventoryValueComparison.labels.map(
    (label, i) => ({
      月份: label,
      今年: summary.inventoryValueComparison.series[0].data[i],
      去年: summary.inventoryValueComparison.series[1].data[i],
    })
  );

  const productTableColumns = [
    {
      title: "商品名稱",
      dataIndex: "productName",
    },
    {
      title: "銷售數量",
      dataIndex: "totalSoldQuantity",
    },
  ];

  const stockTableColumns = [
    {
      title: "產品名稱",
      dataIndex: "productName",
    },
    {
      title: "當前庫存",
      dataIndex: "currentStock",
    },
    {
      title: "安全庫存",
      dataIndex: "safetyStockQuantity",
    },
  ];

  return (
    <div className="p-6">
      <Title level={2}>ERP 儀表板</Title>

      <Row gutter={16} className="mb-6">
        {kpis.map((item) => (
          <Col span={8} key={item.title}>
            <Card>
              <Statistic
                title={item.title}
                value={item.value}
                precision={item.suffix ? 2 : 0}
                suffix={item.suffix || ""}
              />
            </Card>
          </Col>
        ))}
      </Row>

      <Row gutter={16} className="mb-6">
        <Col span={12}>
          <Card title="每月銷售比較">
            <ResponsiveContainer width="100%" height={300}>
              <BarChart data={salesChartData}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="月份" />
                <YAxis />
                <Tooltip />
                <Legend />
                <Bar dataKey="今年" fill="#1890ff" />
                <Bar dataKey="去年" fill="#82ca9d" />
              </BarChart>
            </ResponsiveContainer>
          </Card>
        </Col>
        <Col span={12}>
          <Card title="每月庫存價值比較">
            <ResponsiveContainer width="100%" height={300}>
              <LineChart data={inventoryChartData}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="月份" />
                <YAxis />
                <Tooltip />
                <Legend />
                <Line type="monotone" dataKey="今年" stroke="#1890ff" />
                <Line type="monotone" dataKey="去年" stroke="#82ca9d" />
              </LineChart>
            </ResponsiveContainer>
          </Card>
        </Col>
      </Row>

      <Row gutter={16} className="mb-6">
        <Col span={12}>
          <Card title="熱銷商品前五名">
            <Table
              dataSource={summary.topSellingProducts}
              columns={productTableColumns}
              pagination={false}
              rowKey="productId"
            />
          </Card>
        </Col>
        <Col span={12}>
          <Card title="庫存不足商品">
            <Table
              dataSource={summary.lowStockProducts}
              columns={stockTableColumns}
              pagination={false}
              rowKey="productId"
            />
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default ERPDashboard;
