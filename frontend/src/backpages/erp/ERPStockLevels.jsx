import React, { useRef, useState, useEffect } from 'react';
import { ProTable } from '@ant-design/pro-components';
import { Pagination, Tag } from 'antd';
import axios from '../../api/axiosBackend';

const PAGE_SIZE = 10;

const ERPStockLevels = () => {
  const actionRef = useRef();
  const [data, setData] = useState([]);
  const [total, setTotal] = useState(0);
  const [currentPage, setCurrentPage] = useState(1);
  const [loading, setLoading] = useState(true);

  const fetchData = async (page = 1) => {
    setLoading(true);
    try {
      const res = await axios.get('/inventory', {
        params: {
          page: page - 1, // API 的 page 從 0 開始
          size: PAGE_SIZE,
          sort: 'inventoryId',
        },
      });
      setData(res.data.content);
      setTotal(res.data.totalElements);
    } catch (error) {
      console.error('庫存明細抓取失敗:', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData(currentPage);
  }, [currentPage]);

  const columns = [
    {
      title: '產品名稱',
      dataIndex: 'productName',
      copyable: true,
    },
    {
      title: '產品編號',
      dataIndex: 'productCode',
    },
    {
      title: '倉庫名稱',
      dataIndex: 'warehouseName',
    },
    {
      title: '目前庫存',
      dataIndex: 'currentStock',
      render: (val) => `${val} 件`,
    },
    {
      title: '平均成本',
      dataIndex: 'averageCost',
      valueType: 'money',
    },
    {
      title: '最後更新時間',
      dataIndex: 'lastUpdatedAt',
      valueType: 'dateTime',
    },
    {
      title: '狀態',
      dataIndex: 'currentStock',
      render: (val) => {
        if (val === 0) return <Tag color="red">已缺貨</Tag>;
        if (val < 20) return <Tag color="orange">低庫存</Tag>;
        return <Tag color="green">正常</Tag>;
      },
    },
  ];

  return (
    <div className="bg-white px-2">
      <ProTable
        columns={columns}
        actionRef={actionRef}
        dataSource={data}
        loading={loading}
        rowKey="inventoryId"
        search={false}
        pagination={false}
        dateFormatter="string"
        headerTitle="庫存明細"
        options={false}
      />
      <div className="flex justify-center py-4">
        <Pagination
          current={currentPage}
          pageSize={PAGE_SIZE}
          total={total}
          onChange={(page) => setCurrentPage(page)}
          showSizeChanger={false}
        />
      </div>
    </div>
  );
};

export default ERPStockLevels;
