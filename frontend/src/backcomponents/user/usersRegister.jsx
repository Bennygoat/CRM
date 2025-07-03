import React, { useState } from "react";
import {
  Form,
  Input,
  Button,
  DatePicker,
  Checkbox,
  Divider,
  message,
} from "antd";
import dayjs from "dayjs";
import axios from "../../api/axiosBackend";
import { useNavigate } from "react-router-dom";

const authorityOptions = [
  { code: "USER_READ", displayName: "檢視使用者", moduleGroup: "SYSTEM" },
  { code: "USER_CREATE", displayName: "新增使用者", moduleGroup: "SYSTEM" },
  { code: "USER_UPDATE", displayName: "編輯使用者", moduleGroup: "SYSTEM" },
  { code: "USER_DELETE", displayName: "刪除使用者", moduleGroup: "SYSTEM" },

  { code: "CUSTOMER_READ", displayName: "檢視客戶", moduleGroup: "CRM" },
  { code: "CUSTOMER_CREATE", displayName: "建立客戶資料", moduleGroup: "CRM" },
  { code: "CUSTOMER_UPDATE", displayName: "更新客戶資料", moduleGroup: "CRM" },
  { code: "CUSTOMER_DELETE", displayName: "刪除客戶資料", moduleGroup: "CRM" },
  { code: "CUSTOMER_SUPPORT", displayName: "客戶需求支援", moduleGroup: "CRM" },

  { code: "ORDER_READ", displayName: "檢視訂單", moduleGroup: "ORDER" },
  { code: "ORDER_CREATE", displayName: "新增訂單", moduleGroup: "ORDER" },
  { code: "ORDER_UPDATE", displayName: "編輯訂單", moduleGroup: "ORDER" },
  { code: "ORDER_DELETE", displayName: "刪除訂單", moduleGroup: "ORDER" },

  { code: "ARTICLE_READ", displayName: "檢視文章", moduleGroup: "CMS" },
  { code: "ARTICLE_CREATE", displayName: "新增文章", moduleGroup: "CMS" },
  { code: "ARTICLE_UPDATE", displayName: "更新文章", moduleGroup: "CMS" },
  { code: "ARTICLE_DELETE", displayName: "刪除文章", moduleGroup: "CMS" },

  { code: "REPORT_VIEW", displayName: "查看報表", moduleGroup: "ANALYTICS" },
  { code: "LOG_VIEW", displayName: "操作紀錄分析", moduleGroup: "ANALYTICS" },
];

// 權限分類
const groupedAuthorities = authorityOptions.reduce((acc, item) => {
  if (!acc[item.moduleGroup]) acc[item.moduleGroup] = [];
  acc[item.moduleGroup].push(item);
  return acc;
}, {});

const UsersRegister = () => {
  const [form] = Form.useForm();
  const [selectedAuthorities, setSelectedAuthorities] = useState([]);
  const navigate = useNavigate();

  const handleSubmit = async (values) => {
    const payload = {
      account: values.account,
      password: values.password,
      email: values.email,
      userName: values.userName,
      accessEndDate: values.accessEndDate.format("YYYY-MM-DD"),
      authorityCodes: selectedAuthorities,
    };

    try {
      await axios.post("/user/register", payload);
      message.success("註冊成功");
      form.resetFields();
      setSelectedAuthorities([]);
      navigate("/users/management");
    } catch (error) {
      message.error(
        "註冊失敗：" + (error.response?.data?.message || "未知錯誤")
      );
    }
  };

  return (
    <div className="w-full max-w-4xl mx-auto px-6 py-8 mt-6 bg-white rounded ">
      <h2 className="text-2xl font-bold mb-6">新增使用者帳號</h2>
      <Form form={form} layout="vertical" onFinish={handleSubmit}>
        <Form.Item label="帳號" name="account" rules={[{ required: true }]}>
          <Input placeholder="請輸入帳號" />
        </Form.Item>

        <Form.Item
          label="密碼"
          name="password"
          rules={[
            { required: true, message: "請輸入密碼" },
            {
              validator: (_, value) => {
                const passwordRegex =
                  /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[\W_]).{12,}$/;
                if (!value || passwordRegex.test(value)) {
                  return Promise.resolve();
                }
                return Promise.reject(
                  "密碼需至少12碼，並包含大小寫字母、數字與特殊符號"
                );
              },
            },
          ]}
        >
          <Input.Password placeholder="請輸入密碼（至少12碼，含大小寫與符號）" />
        </Form.Item>

        <Form.Item
          label="使用者名稱"
          name="userName"
          rules={[{ required: true }]}
        >
          <Input placeholder="請輸入使用者名稱" />
        </Form.Item>

        <Form.Item
          label="電子信箱"
          name="email"
          rules={[{ required: true, type: "email" }]}
        >
          <Input placeholder="請輸入電子信箱" />
        </Form.Item>

        <Form.Item
          label="權限到期日"
          name="accessEndDate"
          rules={[{ required: true, message: "請選擇權限到期日" }]}
        >
          <DatePicker className="w-full" />
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
                const others = selectedAuthorities.filter(
                  (code) => !groupCodes.includes(code)
                );
                setSelectedAuthorities([...others, ...checkedValues]);
              }}
            />
          </div>
        ))}

        <Form.Item className="mt-6">
          <Button type="primary" htmlType="submit">
            送出註冊
          </Button>
        </Form.Item>
      </Form>
    </div>
  );
};

export default UsersRegister;
