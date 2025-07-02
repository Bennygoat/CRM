import React from 'react';
import {
  Form,
  Input,
  Select,
  Button,
  DatePicker,
  Space,
  Radio,
  message,
} from 'antd';
import { UserAddOutlined } from '@ant-design/icons';

const { TextArea } = Input;

const industryOptions = [
  { label: '科技業', value: 'tech' },
  { label: '製造業', value: 'manufacturing' },
  { label: '零售業', value: 'retail' },
  { label: '金融業', value: 'finance' },
  { label: '其他', value: 'other' },
];

const levelOptions = [
  { label: '普通客戶', value: 'normal' },
  { label: '重要客戶', value: 'important' },
  { label: 'VIP 客戶', value: 'vip' },
];

const CRMCustomerForm = ({ onSubmit }) => {
  const [form] = Form.useForm();

  const handleFinish = (values) => {
    console.log('提交聯絡人資料:', values);
    message.success('新增聯絡人成功');
    onSubmit?.(values);
  };

  return (
    <Form
      form={form}
      layout="vertical"
      style={{ maxWidth: 600 }}
      onFinish={handleFinish}
      initialValues={{
        level: 'normal',
      }}
    >
      <Form.Item
        label="客戶名稱"
        name="name"
        rules={[{ required: true, message: '請輸入客戶名稱' }]}
      >
        <Input placeholder="請輸入客戶名稱" />
      </Form.Item>

      <Form.Item
        label="聯絡人"
        name="contactPerson"
        rules={[{ required: true, message: '請輸入聯絡人姓名' }]}
      >
        <Input placeholder="請輸入聯絡人姓名" />
      </Form.Item>

      <Form.Item
        label="電子郵件"
        name="email"
        rules={[
          { required: true, message: '請輸入 Email' },
          { type: 'email', message: 'Email 格式不正確' },
        ]}
      >
        <Input placeholder="請輸入聯絡 Email" />
      </Form.Item>

      <Form.Item label="電話" name="phone">
        <Input placeholder="請輸入聯絡電話" />
      </Form.Item>

      <Form.Item label="產業類別" name="industry">
        <Select options={industryOptions} placeholder="請選擇產業" />
      </Form.Item>

      <Form.Item label="等級標記" name="level">
        <Radio.Group options={levelOptions} />
      </Form.Item>

      <Form.Item label="建立日期" name="createdAt">
        <DatePicker style={{ width: '100%' }} />
      </Form.Item>

      <Form.Item label="備註" name="note">
        <TextArea rows={3} placeholder="可輸入補充資訊" />
      </Form.Item>

      <Form.Item>
        <Space>
          <Button type="primary" icon={<UserAddOutlined />} htmlType="submit">
            新增聯絡人
          </Button>
          <Button htmlType="reset">清除</Button>
        </Space>
      </Form.Item>
    </Form>
  );
};

export default CRMCustomerForm;
