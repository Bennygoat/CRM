import React, { useEffect, useRef, useState } from "react";
import { ProTable } from "@ant-design/pro-components";
import { Pagination, Tag, message } from "antd";
import axios from "../../api/axiosBackend";
import dayjs from "dayjs";
import { useNavigate } from "react-router-dom";

const PAGE_SIZE = 10;

const UsersMessage = () => {
  const actionRef = useRef();
  const [messages, setMessages] = useState([]);
  const [loading, setLoading] = useState(true);
  const [currentPage, setCurrentPage] = useState(1);
  const [total, setTotal] = useState(0);
  const navigate = useNavigate();

  const fetchMessages = async (page = 1) => {
    setLoading(true);
    try {
      const res = await axios.get("/customer/message/list", {
        params: {
          page: page - 1, // 假設後端是從 0 開始
          size: PAGE_SIZE,
        },
      });

      // 假設 res.data = { content: [...], totalElements: 50 }
      setMessages(res.data.content || []);
      setTotal(res.data.totalElements || 0);
    } catch (error) {
      console.error("客服訊息取得失敗:", error);
      message.error("無法載入客服訊息");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchMessages(currentPage);
  }, [currentPage]);

  const columns = [
    {
      title: "問題標題",
      dataIndex: "questionTitle",
      valueType: "text",
    },
    {
      title: "處理狀態",
      dataIndex: "isResolved",
      valueType: "select",
      valueEnum: {
        true: { text: "已解決", status: "Success" },
        false: { text: "未解決", status: "Warning" },
      },
      render: (_, record) =>
        record.isResolved ? (
          <Tag color="green">已解決</Tag>
        ) : (
          <Tag color="orange">未解決</Tag>
        ),
    },
    {
      title: "建立時間",
      dataIndex: "createdAt",
      valueType: "dateTime",
    },
    {
      title: "最後回覆時間",
      dataIndex: "lastReplyTime",
      valueType: "dateTime",
    },
    {
      title: "最後回覆內容",
      dataIndex: "lastReplyContent",
      search: false,
      render: (_, record) => (
        <span>{record.lastReplyContent || "（無回覆）"}</span>
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
        pagination={false}
        dateFormatter="string"
        headerTitle="客服訊息列表"
        options={false}
        onRow={(record) => ({
          onClick: () => navigate(`/users/message/${record.messageId}`),
        })}
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

export default UsersMessage;
