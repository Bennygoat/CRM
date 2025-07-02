import React, { useEffect, useState } from "react";
import { Descriptions, Card, Spin, Table, Input, Button, message } from "antd";
import { useParams } from "react-router-dom";
import axios from "../../api/axiosBackend";

const ERPOrderForm = () => {
  const { id } = useParams();
  const [order, setOrder] = useState(null);
  const [loading, setLoading] = useState(true);
  const [editing, setEditing] = useState(false);
  const [remarks, setRemarks] = useState("");

  useEffect(() => {
    console.log("Fetching order with ID:", id);
    const fetchOrder = async () => {
      setLoading(true);
      try {
        const res = await axios.get(`/sales-orders/${id}`);
        
        setOrder(res.data);
        setRemarks(res.data.remarks);
      } catch (error) {
        message.error("無法載入訂單資料");
      } finally {
        setLoading(false);
      }
    };

    fetchOrder();
  }, [id]);

  const handleSave = async () => {
    try {
      await axios.put(`/sales-orders/${id}`, { ...order, remarks });
      message.success("訂單更新成功");
      setEditing(false);
    } catch (error) {
      message.error("更新失敗");
    }
  };

  const columns = [
    { title: "商品代碼", dataIndex: "productCode" },
    { title: "商品名稱", dataIndex: "productName" },
    { title: "數量", dataIndex: "quantity" },
    { title: "單價", dataIndex: "unitPrice", render: val => `$${val}` },
    { title: "小計", dataIndex: "itemAmount", render: val => `$${val}` },
  ];

  return (
    <Spin spinning={loading}>
      {order && (
        <Card title={`訂單明細 - ${order.orderNumber}`}>
          <Descriptions bordered column={2} size="middle">
            <Descriptions.Item label="訂單日期">{order.orderDate}</Descriptions.Item>
            <Descriptions.Item label="訂單狀態">{order.orderStatus}</Descriptions.Item>
            <Descriptions.Item label="付款狀態">{order.paymentStatus}</Descriptions.Item>
            <Descriptions.Item label="付款方式">{order.paymentMethod}</Descriptions.Item>
            <Descriptions.Item label="客戶名稱">{order.customerName}</Descriptions.Item>
            <Descriptions.Item label="客戶類型">{order.customerType}</Descriptions.Item>
            <Descriptions.Item label="出貨地址" span={2}>{order.shippingAddress}</Descriptions.Item>
            <Descriptions.Item label="備註" span={2}>
              {editing ? (
                <Input.TextArea
                  value={remarks}
                  onChange={(e) => setRemarks(e.target.value)}
                  rows={3}
                />
              ) : (
                <div>{remarks || "-"}</div>
              )}
            </Descriptions.Item>
          </Descriptions>

          <div className="my-4">
            <Table
              dataSource={order.details}
              columns={columns}
              rowKey="productId"
              pagination={false}
            />
          </div>

          <Descriptions bordered size="small" column={1}>
            <Descriptions.Item label="未稅總金額">${order.totalNetAmount}</Descriptions.Item>
            <Descriptions.Item label="稅額">${order.totalTaxAmount}</Descriptions.Item>
            <Descriptions.Item label="總金額">${order.totalAmount}</Descriptions.Item>
          </Descriptions>

          <div className="mt-4 text-right">
            {editing ? (
              <>
                <Button onClick={() => setEditing(false)} style={{ marginRight: 8 }}>取消</Button>
                <Button type="primary" onClick={handleSave}>儲存</Button>
              </>
            ) : (
              <Button type="primary" onClick={() => setEditing(true)}>編輯備註</Button>
            )}
          </div>
        </Card>
      )}
    </Spin>
  );
};

export default ERPOrderForm;
