import React, { useState, useEffect } from "react";
import {
  Form,
  Input,
  Button,
  DatePicker,
  Checkbox,
  Divider,
  message,
  Select,
} from "antd";
import dayjs from "dayjs";
import axios from "../../api/axiosBackend";
import { useParams, useNavigate } from "react-router-dom";

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

const groupedAuthorities = authorityOptions.reduce((acc, item) => {
  if (!acc[item.moduleGroup]) acc[item.moduleGroup] = [];
  acc[item.moduleGroup].push(item);
  return acc;
}, {});

const UserEdit = () => {
  const [form] = Form.useForm();
  const [selectedAuthorities, setSelectedAuthorities] = useState([]);
  const { account } = useParams();
  const navigate = useNavigate();

  // 取得使用者資料
  useEffect(() => {
    if (account) {
      axios
        .get(`/user/${account}`)
        .then((res) => {
          const data = res.data;
          console.log("載入使用者資料:", data);

          form.setFieldsValue({
            account: data.account,
            email: data.email,
            userName: data.userName,
            roleName: data.roleName,
            accessEndDate: dayjs(data.accessEndDate),
          });

          setSelectedAuthorities(data.authorityCodes || []);
        })
        .catch((err) => {
          message.error("載入使用者資料失敗");
        });
    }
  }, [account, form]);

  const handleSubmit = async (values) => {
    const payload = {
      userName: values.userName,
      email: values.email,
      roleName: values.roleName,
      authorityCodes: selectedAuthorities,
      accessEndDate: values.accessEndDate.format("YYYY-MM-DD"),
    };
    console.log("提交的資料:", payload);

    try {
      await axios.put(`/user/${account}`, payload);
      message.success("使用者資料更新成功");
      navigate("/users/management");
    } catch (error) {
      message.error(
        "更新失敗：" + (error.response?.data?.message || "未知錯誤")
      );
    }
  };

  return (
    <div className="w-full max-w-4xl mx-auto px-6 py-8 mt-6 bg-white rounded shadow">
      <h2 className="text-2xl font-bold mb-6">編輯使用者資料與權限</h2>
      <Form form={form} layout="vertical" onFinish={handleSubmit}>
        <Form.Item label="帳號" name="account">
          <Input disabled />
        </Form.Item>

        <Form.Item label="使用者名稱" name="userName">
          <Input placeholder="請輸入使用者名稱" />
        </Form.Item>

        <Form.Item label="電子信箱" name="email">
          <Input placeholder="請輸入電子信箱" />
        </Form.Item>

        <Form.Item label="使用者身分" name="roleName">
          <Select placeholder="請選擇使用者角色">
            <Select.Option value="SUPPORT">客服</Select.Option>
            <Select.Option value="SALES">業務</Select.Option>
            <Select.Option value="GUEST">訪客</Select.Option>
          </Select>
        </Form.Item>

        <Form.Item label="權限到期日" name="accessEndDate">
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
            儲存變更
          </Button>
        </Form.Item>
      </Form>
    </div>
  );
};

export default UserEdit;
