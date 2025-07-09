import React, { useRef } from 'react';
import { ProTable } from '@ant-design/pro-components';
import { Button, Tag, Statistic, Card, Row, Col } from 'antd';
import { PlusOutlined, ShoppingCartOutlined, ClockCircleOutlined, CarOutlined, CheckCircleOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';

const orderStatusConfig = {
  'PENDING': { color: 'orange', text: '待處理' },
  'PROCESSING': { color: 'blue', text: '處理中' },
  'SHIPPED': { color: 'purple', text: '已出貨' },
  'COMPLETED': { color: 'green', text: '已完成' },
  'CANCELLED': { color: 'default', text: '已取消' },
};

const currencyFormatter = new Intl.NumberFormat('zh-TW', {
  style: 'currency',
  currency: 'TWD',
  minimumFractionDigits: 0,
});

const columns = [
  { title: '訂單編號', dataIndex: 'orderId', key: 'orderId', copyable: true, width: 150 },
  { title: '客戶名稱', dataIndex: 'customerName', key: 'customerName' },
  {
    title: '訂單狀態',
    dataIndex: 'status',
    key: 'status',
    render: (status) => {
      const config = orderStatusConfig[status] || { color: 'default', text: status };
      return <Tag color={config.color}>{config.text}</Tag>;
    }
  },
  { title: '訂單金額', dataIndex: 'amount', key: 'amount', sorter: (a, b) => a.amount - b.amount, render: (amount) => currencyFormatter.format(amount),},
  { title: '訂單日期', dataIndex: 'orderDate', key: 'orderDate', valueType: 'date', sorter: (a, b) => new Date(a.orderDate) - new Date(b.orderDate) },
  {
    title: '操作',
    key: 'action',
    render: () => <a>詳情</a>,
  },
];

const fakeOrderData = [
  { orderId: '20250708-001', customerName: '酷夏連鎖超市', status: 'PROCESSING', amount: 25500, orderDate: '2025-07-08' },
  { orderId: '20250708-002', customerName: '品味咖啡連鎖店', status: 'PENDING', amount: 12800, orderDate: '2025-07-08' },
  { orderId: '20250707-005', customerName: '超級團購網', status: 'SHIPPED', amount: 31000, orderDate: '2025-07-07' },
  { orderId: '20250705-003', customerName: '全球百貨連鎖', status: 'SHIPPED', amount: 88000, orderDate: '2025-07-05' },
  { orderId: '20250702-008', customerName: '快樂兒童樂園', status: 'COMPLETED', amount: 45000, orderDate: '2025-07-02' },
  { orderId: '20250701-001', customerName: '糖心冰坊', status: 'CANCELLED', amount: 5200, orderDate: '2025-07-01' },
];

const KpiCards = () => {
  const kpiData = {
    todayOrders: 2,
    pendingOrders: 1,
    toShipOrders: 2,
    monthlySales: 133000,
  };

  return (
    <Row gutter={16}>
      <Col span={6}><Card><Statistic title="今日訂單數" value={kpiData.todayOrders} prefix={<ShoppingCartOutlined />} /></Card></Col>
      <Col span={6}><Card><Statistic title="待處理訂單" value={kpiData.pendingOrders} prefix={<ClockCircleOutlined />} /></Card></Col>
      <Col span={6}><Card><Statistic title="待出貨訂單" value={kpiData.toShipOrders} prefix={<CarOutlined />} /></Card></Col>
      <Col span={6}><Card><Statistic title="本月銷售總額" value={kpiData.monthlySales} prefix="$" /></Card></Col>
    </Row>
  );
};


const OMMainPage = () => {
  const actionRef = useRef();
  const navigate = useNavigate();

  return (
    <div className="p-6 bg-gray-50 space-y-6">
      {/* 1. 頂部核心數據儀表板 */}
      <KpiCards />

      {/* 2. 主要的訂單列表 */}
      <div className="bg-white p-4 rounded-lg shadow-sm">
        <ProTable
          columns={columns}
          dataSource={fakeOrderData} // 未來這裡會替換成從 API 獲取的資料
          actionRef={actionRef}
          rowKey="orderId"
          search={{
            labelWidth: 'auto',
          }}
          pagination={{ pageSize: 10 }}
          headerTitle="所有訂單"
          toolBarRender={() => [
            <Button
              key="new"
              icon={<PlusOutlined />}
              type="primary"
              onClick={() => navigate('/erp/sales/orders/new')} // 假設這是新增訂單的路徑
            >
              新增訂單
            </Button>,
          ]}
        />
      </div>
    </div>
  );
};

export default OMMainPage;