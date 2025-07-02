import React, { useState } from "react";
import { Form, Input, Button, Checkbox, Divider, message } from "antd";

// 假資料：模擬後端回傳的權限清單
const authorityOptions = [
  { code: "USER_READ", displayName: "檢視使用者", moduleGroup: "SYSTEM" },
  { code: "USER_CREATE", displayName: "新增使用者", moduleGroup: "SYSTEM" },
  { code: "USER_UPDATE", displayName: "編輯使用者", moduleGroup: "SYSTEM" },
  { code: "CUSTOMER_READ", displayName: "檢視客戶", moduleGroup: "CRM" },
  { code: "CUSTOMER_CREATE", displayName: "建立客戶", moduleGroup: "CRM" },
  { code: "ORDER_READ", displayName: "檢視訂單", moduleGroup: "ORDER" },
  { code: "ORDER_UPDATE", displayName: "修改訂單", moduleGroup: "ORDER" },
  { code: "ARTICLE_CREATE", displayName: "新增文章", moduleGroup: "CMS" },
  { code: "REPORT_VIEW", displayName: "查看報表", moduleGroup: "ANALYTICS" },
];

// 權限依 moduleGroup 分類
const groupedAuthorities = authorityOptions.reduce((acc, item) => {
  if (!acc[item.moduleGroup]) acc[item.moduleGroup] = [];
  acc[item.moduleGroup].push(item);
  return acc;
}, {});

const UsersAuthority = () => {
  const [form] = Form.useForm();
  const [selectedAuthorities, setSelectedAuthorities] = useState([
    "USER_READ",
    "CUSTOMER_READ",
  ]);

  const handleSubmit = (values) => {
    const payload = {
      ...values,
      authorities: selectedAuthorities,
    };
    console.log("送出的資料：", payload);
    message.success("使用者資料已送出（模擬）");
  };

  return (
    <div className="w-full max-w-6xl mx-auto px-6 py-8 mt-6 bg-transparent">
      <h2 className="text-2xl font-bold mb-6">使用者權限管理</h2>
      <Form
        form={form}
        layout="vertical"
        onFinish={handleSubmit}
        initialValues={{
          name: "王小明",
          email: "test@example.com",
        }}
      >
        <Form.Item label="姓名" name="name" rules={[{ required: true }]}>
          <Input placeholder="請輸入姓名" />
        </Form.Item>

        <Form.Item
          label="電子信箱"
          name="email"
          rules={[{ required: true, type: "email" }]}
        >
          <Input placeholder="請輸入電子信箱" />
        </Form.Item>

        <Divider>權限設定</Divider>

        {Object.entries(groupedAuthorities).map(([group, items]) => (
          <div key={group} className="mb-4">
            <h4 className="text-base font-semibold mb-2">{group} 權限</h4>
            <Checkbox.Group
              options={items.map((item) => ({
                label: item.displayName,
                value: item.code,
              }))}
              value={items
                .map((item) => item.code)
                .filter((code) => selectedAuthorities.includes(code))}
              onChange={(checkedValues) => {
                const groupCodes = items.map((item) => item.code);
                const otherCodes = selectedAuthorities.filter(
                  (code) => !groupCodes.includes(code)
                );
                setSelectedAuthorities([...otherCodes, ...checkedValues]);
              }}
            />
          </div>
        ))}

        <Form.Item className="mt-6">
          <Button type="primary" htmlType="submit">
            儲存設定
          </Button>
        </Form.Item>
      </Form>
    </div>
  );
};

export default UsersAuthority;
