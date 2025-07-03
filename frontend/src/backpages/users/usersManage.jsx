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
      const res = await axios.post("/user/search", {
        params: {
          page: page - 1,
          size: PAGE_SIZE,
        },
      });
      setData(res.data.content);
      setTotal(res.data.totalElements);
      console.log("使用者資料:", res.data.content);
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
      dataIndex: "userName",
      copyable: true,
      ellipsis: true,
    },
    {
      title: "電子信箱",
      dataIndex: "email",
    },
    {
      title: "帳號狀態",
      dataIndex: "active",
      render: (val) =>
        val ? (
          <Tag color="green">啟用中</Tag>
        ) : (
          <Tag color="default">已停用</Tag>
        ),
    },
    {
      title: "使用者身分",
      dataIndex: "roleName",
      render: (text) => (
        <Tag color="blue">
          {{
            ADMIN: "管理者",
            SUPPORT: "客服",
            SALES: "業務",
            GUEST: "訪客",
          }[text] || "未指定"}
        </Tag>
      ),
    },
    {
      title: "啟用期限",
      dataIndex: "accessEndDate",
      valueType: "date",
      hideInSearch: true,
    },
    {
      title: "操作",
      valueType: "option",
      key: "option",
      render: (_, record) => [
        <a
          key={`edit-${record.userId}`}
          onClick={() => navigate(`/users/management/edit/${record.account}`)}
        >
          編輯
        </a>,
        <TableDropdown
          key={`dropdown-${record.userId}`}
          menus={[{ key: "delete", name: "刪除帳號" }]}
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
        rowKey="userId"
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
            onClick={() => navigate("/users/management/register")}
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
