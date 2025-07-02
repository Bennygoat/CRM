import React, { useRef, useState, useEffect } from "react";
import { PlusOutlined } from "@ant-design/icons";
import { ProTable, TableDropdown } from "@ant-design/pro-components";
import { Button, Pagination, Tag } from "antd";
import { useNavigate } from "react-router-dom";
import axios from "../../api/axiosBackend";

const PAGE_SIZE = 10;

const UsersManage = () => {
  const actionRef = useRef();
  const [data, setData] = useState([]);
  const [total, setTotal] = useState(0);
  const [currentPage, setCurrentPage] = useState(1);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  const fetchData = async (page = 1) => {
    setLoading(true);
    try {
      const res = await axios.get("/admin/users", {
        params: {
          page,
          pageSize: PAGE_SIZE,
        },
      });
      setData(res.data.data);
      setTotal(res.data.total);
    } catch (error) {
      console.error("使用者資料抓取失敗:", error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData(currentPage);
  }, [currentPage]);

  const columns = [
    {
      title: "使用者名稱",
      dataIndex: "name",
      copyable: true,
      ellipsis: true,
    },
    {
      title: "電子信箱",
      dataIndex: "email",
    },
    {
      title: "帳號狀態",
      dataIndex: "status",
      valueType: "select",
      valueEnum: {
        active: { text: "啟用中", status: "Success" },
        inactive: { text: "已停用", status: "Default" },
        suspended: { text: "暫停中", status: "Warning" },
      },
    },
    {
      title: "使用者身分",
      dataIndex: "role",
      valueType: "select",
      valueEnum: {
        admin: { text: "管理者", status: "Success" },
        support: { text: "客服", status: "Processing" },
        sales: { text: "業務", status: "Warning" },
        guest: { text: "訪客", status: "Default" },
      },
      render: (_, record) => (
        <Tag color="blue">
          {{
            admin: "管理者",
            support: "客服",
            sales: "業務",
            guest: "訪客",
          }[record.role] || "未指定"}
        </Tag>
      ),
    },
    {
      title: "建立時間",
      dataIndex: "created_at",
      valueType: "date",
      hideInSearch: true,
    },
    {
      title: "操作",
      valueType: "option",
      key: "option",
      render: (_, record) => [
        <a key="edit" onClick={() => navigate(`/users/authority`)}>
          編輯
        </a>,
        <TableDropdown
          key="dropdown"
          menus={[
            { key: "reset", name: "重設密碼" },
            { key: "delete", name: "刪除帳號" },
          ]}
        />,
      ],
    },
  ];

  return (
    <div className="bg-white px-2">
      <ProTable
        columns={columns}
        actionRef={actionRef}
        dataSource={data}
        loading={loading}
        rowKey="id"
        search={false}
        pagination={false}
        dateFormatter="string"
        headerTitle="使用者帳號管理"
        options={false}
        toolBarRender={() => [
          <Button
            key="new"
            icon={<PlusOutlined />}
            type="primary"
            onClick={() => navigate("/admin/users/new")}
          >
            新增使用者
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

export default UsersManage;
