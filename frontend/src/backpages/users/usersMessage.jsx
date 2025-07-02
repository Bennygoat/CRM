import React, { useEffect, useRef, useState } from 'react';
import { ProTable } from '@ant-design/pro-components';
import { Button, Tag, message } from 'antd';
import { DownloadOutlined } from '@ant-design/icons';
import axios from '../../api/axiosBackend';
import dayjs from 'dayjs';

const UsersMessage = () => {
  const actionRef = useRef();
  const [messages, setMessages] = useState([]);
  const [loading, setLoading] = useState(true);

  const fetchMessages = async () => {
    setLoading(true);
    try {
      const res = await axios.get('/customer/message/list');
      setMessages(res.data);
    } catch (error) {
      console.error('客服訊息取得失敗:', error);
      message.error('無法載入客服訊息');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchMessages();
  }, []);

  const handleExport = () => {
    // 模擬匯出
    message.success('客服訊息已匯出（模擬）');
  };

  const columns = [
    {
      title: '問題標題',
      dataIndex: 'questionTitle',
      valueType: 'text',
    },
    {
      title: '處理狀態',
      dataIndex: 'isResolved',
      valueType: 'select',
      valueEnum: {
        true: { text: '已解決', status: 'Success' },
        false: { text: '未解決', status: 'Warning' },
      },
      render: (_, record) =>
        record.isResolved ? (
          <Tag color="green">已解決</Tag>
        ) : (
          <Tag color="orange">未解決</Tag>
        ),
    },
    {
      title: '建立時間',
      dataIndex: 'createdAt',
      valueType: 'dateTime',
    },
    {
      title: '最後回覆時間',
      dataIndex: 'lastReplyTime',
      valueType: 'dateTime',
    },
    {
      title: '最後回覆內容',
      dataIndex: 'lastReplyContent',
      search: false,
      render: (_, record) => (
        <span>{record.lastReplyContent || '（無回覆）'}</span>
      ),
    },
  ];

  return (
    <div className="bg-white px-2">
      <ProTable
        columns={columns}
        dataSource={messages}
        loading={loading}
        rowKey="messageId"
        actionRef={actionRef}
        search={false}
        pagination={{ pageSize: 10 }}
        dateFormatter="string"
        headerTitle="客服訊息列表"
        options={false}

      />
    </div>
  );
};

export default UsersMessage;
