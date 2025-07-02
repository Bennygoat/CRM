import React, { useRef, useState, useEffect } from "react";
import { PlusOutlined } from "@ant-design/icons";
import { ProTable, TableDropdown } from "@ant-design/pro-components";
import { Button, Pagination } from "antd";
import { useNavigate } from "react-router-dom";
import axios from "../../api/axiosBackend";

const PAGE_SIZE = 10;

const ERPOrders = () => {
  const actionRef = useRef();
  const [data, setData] = useState([]);
  const [total, setTotal] = useState(0);
  const [currentPage, setCurrentPage] = useState(1);
  const [searchFilters, setSearchFilters] = useState({});
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  const fetchData = async (page = 1, filters = {}) => {
    setLoading(true);
    setData([]);
    try {
      const res = await axios.get("/sales-orders", {
        params: {
          ...filters,
          page: page - 1,
          size: PAGE_SIZE,
          sort: "orderDate",
        },
      });

      setData(res.data.content);
      setTotal(res.data.totalElements);
    } catch (error) {
      console.error("訂單資料抓取失敗:", error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData(currentPage, searchFilters);
  }, [currentPage, searchFilters]);

  const columns = [
    {
      title: "客戶名稱",
      dataIndex: "customerName",
      formItemProps: {
        label: "客戶名稱",
      },
      fieldProps: {
        placeholder: "請輸入客戶名稱",
      },
      search: {
        transform: (value) => ({ keyword: value }),
      },
    },
    {
      title: "訂單編號",
      dataIndex: "orderNumber",
      copyable: true,
      ellipsis: true,
      hideInSearch: true,
    },
    {
      title: "狀態",
      dataIndex: "orderStatus",
      valueType: "select",
      valueEnum: {
        PENDING_PAYMENT: { text: "待付款", status: "Default" },
        PENDING_SHIPMENT: { text: "待出貨", status: "Processing" },
        SHIPPED: { text: "已出貨", status: "Success" },
        PENDING_PICKUP: { text: "已到門市", status: "Warning" },
        COMPLETE: { text: "已完成", status: "Success" },
        CANCELLED: { text: "已取消", status: "Error" },
        RETURN_REQUESTED: { text: "申請退貨", status: "Warning" },
        RETURNED: { text: "已退貨", status: "Default" },
      },
      search: {
        transform: (value) => ({ orderStatus: value }),
      },
    },
    {
      title: "總金額",
      dataIndex: "totalAmount",
      render: (_, record) => `$${record.totalAmount.toLocaleString()}`,
      hideInSearch: true,
    },
    {
      title: "建立日期",
      dataIndex: "orderDate",
      valueType: "date",
      hideInSearch: true,
    },
    {
      title: "操作",
      valueType: "option",
      key: "option",
      render: (_, record) => [
        <a
          key="edit"
          onClick={() => navigate(`/erp/sales/orders/${record.salesOrderId}`)}
        >
          編輯
        </a>,
        <TableDropdown
          key="dropdown"
          menus={[
            { key: "copy", name: "複製" },
            { key: "delete", name: "刪除" },
          ]}
        />,
      ],
      hideInSearch: true,
    },
  ];

  return (
    <div className="bg-white px-2">
      <ProTable
        columns={columns}
        actionRef={actionRef}
        dataSource={data}
        loading={loading}
        rowKey="salesOrderId"
        search={{
          labelWidth: "auto",
          searchText: "搜尋",
          resetText: "清除",
        }}
        pagination={false}
        dateFormatter="string"
        headerTitle="訂單管理"
        options={false}
        onSubmit={(params) => {
          setSearchFilters(params);
          setCurrentPage(1);
        }}
        toolBarRender={() => [
          <Button
            key="new"
            icon={<PlusOutlined />}
            type="primary"
          >
            新增訂單
          </Button>,
        ]}
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

export default ERPOrders;
