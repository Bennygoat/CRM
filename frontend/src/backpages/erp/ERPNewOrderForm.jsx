import React, { useState } from "react";
import {
  Card,
  Form,
  Input,
  DatePicker,
  Button,
  Select,
  Table,
  InputNumber,
  message,
  Space,
} from "antd";
import axios from "../../api/axiosBackend";
import dayjs from "dayjs";

const { Option } = Select;

const ERPNewOrderForm = () => {
  const [form] = Form.useForm();
  const [details, setDetails] = useState([]);
  const [loading, setLoading] = useState(false);

  const addDetail = () => {
    setDetails([...details, { key: Date.now(), productId: null, quantity: 1, unitPrice: 0 }]);
  };

  const removeDetail = (key) => {
    setDetails(details.filter((item) => item.key !== key));
  };

  const updateDetail = (key, field, value) => {
    setDetails(details.map(item =>
      item.key === key ? { ...item, [field]: value } : item
    ));
  };

  const handleSubmit = async (values) => {
    if (details.length === 0) {
      message.warning("請至少新增一項商品明細");
      return;
    }

    const payload = {
      ...values,
      orderDate: values.orderDate.format("YYYY-MM-DD"),
      details: details.map(({ key, ...item }) => item),
    };

    try {
      setLoading(true);
      await axios.post("/sales-orders", payload);
      message.success("訂單新增成功");
      form.resetFields();
      setDetails([]);
    } catch (error) {
      message.error("訂單新增失敗");
    } finally {
      setLoading(false);
    }
  };

  const columns = [
    {
      title: "商品ID",
      dataIndex: "productId",
      render: (val, record) => (
        <InputNumber
          value={val}
          onChange={(value) => updateDetail(record.key, "productId", value)}
        />
      ),
    },
    {
      title: "數量",
      dataIndex: "quantity",
      render: (val, record) => (
        <InputNumber
          min={1}
          value={val}
          onChange={(value) => updateDetail(record.key, "quantity", value)}
        />
      ),
    },
    {
      title: "單價",
      dataIndex: "unitPrice",
      render: (val, record) => (
        <InputNumber
          min={0}
          value={val}
          onChange={(value) => updateDetail(record.key, "unitPrice", value)}
        />
      ),
    },
    {
      title: "操作",
      render: (_, record) => (
        <Button danger onClick={() => removeDetail(record.key)}>
          刪除
        </Button>
      ),
    },
  ];

  return (
    <Card title="新增訂單" loading={loading}>
      <Form form={form} layout="vertical" onFinish={handleSubmit}>
        <Form.Item label="客戶ID" name="customerId" rules={[{ required: true }]}>
          <InputNumber style={{ width: "100%" }} />
        </Form.Item>

        <Form.Item label="倉庫ID" name="warehouseId" rules={[{ required: true }]}>
          <InputNumber style={{ width: "100%" }} />
        </Form.Item>

        <Form.Item label="訂單日期" name="orderDate" rules={[{ required: true }]}>
          <DatePicker style={{ width: "100%" }} />
        </Form.Item>

        <Form.Item label="出貨地址" name="shippingAddress" rules={[{ required: true }]}>
          <Input.TextArea rows={2} />
        </Form.Item>

        <Form.Item label="出貨方式" name="shippingMethod" rules={[{ required: true }]}>
          <Input />
        </Form.Item>

        <Form.Item label="付款方式" name="paymentMethod" rules={[{ required: true }]}>
          <Select placeholder="請選擇">
            <Option value="CASH">現金</Option>
            <Option value="CREDIT_CARD">信用卡</Option>
            <Option value="TRANSFER">轉帳</Option>
          </Select>
        </Form.Item>

        <Form.Item label="備註" name="remarks">
          <Input.TextArea rows={3} />
        </Form.Item>

        <Form.Item label="訂單明細">
          <Table
            dataSource={details}
            columns={columns}
            pagination={false}
            rowKey="key"
          />
          <Button type="dashed" onClick={addDetail} className="mt-2">
            + 新增商品明細
          </Button>
        </Form.Item>

        <Form.Item>
          <Space>
            <Button type="primary" htmlType="submit">
              儲存訂單
            </Button>
            <Button htmlType="reset" onClick={() => setDetails([])}>
              清除表單
            </Button>
          </Space>
        </Form.Item>
      </Form>
    </Card>
  );
};

export default ERPNewOrderForm;
