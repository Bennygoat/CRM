import React, { useEffect, useRef, useState } from 'react';
import { ProTable } from '@ant-design/pro-components';
import { Button, DatePicker, Space, Tag, message } from 'antd';
import { DownloadOutlined } from '@ant-design/icons';
import axios from '../../api/axiosBackend';
import dayjs from 'dayjs';

const { RangePicker } = DatePicker;

const UsersLogs = () => {
  const actionRef = useRef();
  const [logs, setLogs] = useState([]);
  const [loading, setLoading] = useState(true);

  const fetchLogs = async (params = {}) => {
    setLoading(true);
    try {
      const res = await axios.get('/admin/user-logs', { params });
      setLogs(res.data.data);
    } catch (error) {
      console.error('操作紀錄取得失敗:', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchLogs();
  }, []);

  const handleExport = () => {
    // 模擬匯出，可替換為下載 CSV 或 call /export API
    message.success('操作紀錄已匯出（模擬）');
  };

  const columns = [
    {
      title: '使用者',
      dataIndex: 'user',
      valueType: 'text',
    },
    {
      title: '操作動作',
      dataIndex: 'action',
      valueType: 'select',
      valueEnum: {
        login: { text: '登入' },
        logout: { text: '登出' },
        update: { text: '異動設定' },
        delete: { text: '刪除資料' },
        create: { text: '新增資料' },
      },
    },
    {
      title: '目標模組',
      dataIndex: 'module',
      valueType: 'text',
    },
    {
      title: '詳細內容',
      dataIndex: 'detail',
      search: false,
      render: (_, record) => (
        <Tag color="blue">{record.detail}</Tag>
      ),
    },
    {
      title: '操作時間',
      dataIndex: 'timestamp',
      valueType: 'dateTime',
    },
  ];

  return (
    <div className="bg-white px-2">
      <ProTable
        columns={columns}
        dataSource={logs}
        loading={loading}
        rowKey="id"
        actionRef={actionRef}
        search={false}
        pagination={false}
        dateFormatter="string"
        headerTitle="使用者操作紀錄"
        options={false}
        toolBarRender={() => [
          <Button key="export" icon={<DownloadOutlined />} onClick={handleExport}>
            匯出紀錄
          </Button>,
        ]}
        onSubmit={(params) => {
          // 日期轉換處理
          if (params.timestamp) {
            const [start, end] = params.timestamp;
            params.startDate = dayjs(start).format('YYYY-MM-DD');
            params.endDate = dayjs(end).format('YYYY-MM-DD');
            delete params.timestamp;
          }
          fetchLogs(params);
        }}
      />
      
    </div>
    
  );
};

export default UsersLogs;
