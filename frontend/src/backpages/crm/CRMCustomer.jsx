import React, { useRef, useState, useEffect } from "react";
import { PlusOutlined } from "@ant-design/icons";
import { ProTable, TableDropdown } from "@ant-design/pro-components";
import {
  Button,
  Pagination,
  Modal,
  message,
  Descriptions,
  Form,
  Input,
} from "antd";
import { useNavigate } from "react-router-dom";
import axios from "../../api/axiosBackend";

const PAGE_SIZE = 10;

const CRMCustomer = () => {
  const actionRef = useRef();
  const navigate = useNavigate();

  const [data, setData] = useState([]);
  const [total, setTotal] = useState(0);
  const [currentPage, setCurrentPage] = useState(1);
  const [loading, setLoading] = useState(true);
  const [searchFilters, setSearchFilters] = useState({});
  const [deleteVisible, setDeleteVisible] = useState(false);
  const [deletingId, setDeletingId] = useState(null);

  const [detailVisible, setDetailVisible] = useState(false);
  const [selectedContact, setSelectedContact] = useState(null);
  const [editVisible, setEditVisible] = useState(false);
  const [editContact, setEditContact] = useState(null);
  const [form] = Form.useForm();

  const fetchData = async (page = 1, filters = {}) => {
    setLoading(true);
    try {
      const keyword = filters?.keyword || "";

      const res = await axios.get("/contacts/search/by-name", {
        params: {
          name: keyword,
          page: page - 1,
          size: PAGE_SIZE,
          sort: "contactId",
        },
      });

      setData(res.data.content || []);
      setTotal(res.data.totalElements || 0);
    } catch (error) {
      console.error("資料抓取失敗:", error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData(currentPage, searchFilters);
  }, [currentPage, searchFilters]);

  const handleDelete = (id) => {
    setDeletingId(id);
    setDeleteVisible(true);
  };

  const confirmDelete = async () => {
    try {
      await axios.delete(`/contacts/${deletingId}`);
      message.success("刪除成功");
      setDeleteVisible(false);
      fetchData(currentPage, searchFilters);
    } catch (error) {
      console.error("刪除失敗:", error);
      message.error("刪除失敗");
    }
  };

  const handleEdit = async (id) => {
    try {
      const res = await axios.get(`/contacts/${id}`);
      const contact = res.data;
      setEditContact(contact);
      form.setFieldsValue({
        contactName: contact.contactName,
        title: contact.title,
        email: contact.email,
        phone: contact.phone,
        notes: contact.notes,
      });
      setEditVisible(true);
    } catch (err) {
      console.error("取得聯絡人資料失敗", err);
      message.error("無法取得資料");
    }
  };

  const fetchContactDetail = async (id) => {
    try {
      const res = await axios.get(`/contacts/${id}`);
      console.log("聯絡人詳情:", res.data);
      setSelectedContact(res.data);
      setDetailVisible(true);
    } catch (error) {
      console.error("取得聯絡人詳情失敗:", error);
      message.error("無法取得聯絡人資料");
    }
  };

  const columns = [
    {
      title: "聯絡人名稱",
      dataIndex: "contactName",
      copyable: true,
      ellipsis: true,
      fieldProps: {
        placeholder: "搜尋聯絡人名稱",
      },
      search: {
        transform: (value) => ({ keyword: value }),
      },
    },
    {
      title: "職位",
      dataIndex: "title",
      ellipsis: true,
      hideInSearch: true,
    },
    {
      title: "客戶名稱",
      dataIndex: "customerName",
      ellipsis: true,
      hideInSearch: true,
    },
    {
      title: "建立時間",
      dataIndex: "createdAt",
      valueType: "date",
      hideInSearch: true,
    },
    {
      title: "操作",
      valueType: "option",
      key: "option",
      render: (_, record) => [
        <div key="actions" onClick={(e) => e.stopPropagation()}>
          <a onClick={() => handleEdit(record.contactId)}>編輯</a>
          <TableDropdown
            menus={[{ key: "delete", name: "刪除" }]}
            onSelect={(key) => {
              if (key === "delete") {
                handleDelete(record.contactId);
              }
            }}
          />
        </div>,
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
        rowKey="contactId"
        search={{
          labelWidth: "auto",
          searchText: "搜尋",
          resetText: "清除",
        }}
        pagination={false}
        dateFormatter="string"
        headerTitle="聯絡人列表"
        options={false}
        onSubmit={(params) => {
          setSearchFilters(params);
          setCurrentPage(1);
        }}
        onRow={(record) => ({
          onClick: () => fetchContactDetail(record.contactId),
        })}
        toolBarRender={() => [
          <Button
            key="new"
            icon={<PlusOutlined />}
            type="primary"
            onClick={() => navigate("/crm/customer/new")}
          >
            新增聯絡人
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

      <Modal
        title="聯絡人詳細資訊"
        open={detailVisible}
        onCancel={() => setDetailVisible(false)}
        footer={null}
      >
        {selectedContact && (
          <Descriptions column={1} bordered size="small">
            <Descriptions.Item label="姓名">
              {selectedContact.contactName}
            </Descriptions.Item>
            <Descriptions.Item label="職稱">
              {selectedContact.title}
            </Descriptions.Item>
            <Descriptions.Item label="Email">
              {selectedContact.email}
            </Descriptions.Item>
            <Descriptions.Item label="電話">
              {selectedContact.phone}
            </Descriptions.Item>
            <Descriptions.Item label="備註">
              {selectedContact.notes || "-"}
            </Descriptions.Item>
            <Descriptions.Item label="所屬公司">
              {selectedContact.customerName}
            </Descriptions.Item>
          </Descriptions>
        )}
      </Modal>

      <Modal
        title="確認刪除"
        open={deleteVisible}
        onOk={confirmDelete}
        onCancel={() => setDeleteVisible(false)}
        okText="確定"
        cancelText="取消"
      >
        <p>確定要刪除這位聯絡人嗎？</p>
      </Modal>

      <Modal
        title="編輯聯絡人"
        open={editVisible}
        onCancel={() => setEditVisible(false)}
        onOk={() => form.submit()}
        okText="儲存"
      >
        {editContact && (
          <Form
            layout="vertical"
            form={form}
            onFinish={async (values) => {
              try {
                await axios.put(`/contacts/${editContact.contactId}, {
                  ...editContact,
                  ...values,
                }`);
                message.success("更新成功");
                setEditVisible(false);
                fetchData(currentPage, searchFilters);
              } catch (error) {
                console.error("更新失敗", error);
                message.error("更新失敗");
              }
            }}
          >
            <Form.Item
              label="姓名"
              name="contactName"
              rules={[{ required: true, message: "姓名不可為空" }]}
            >
              <Input />
            </Form.Item>

            <Form.Item label="職稱" name="title">
              <Input />
            </Form.Item>

            <Form.Item
              label="Email"
              name="email"
              rules={[
                { required: true, message: "Email 不可為空" },
                { type: "email", message: "Email 格式不正確" },
              ]}
            >
              <Input />
            </Form.Item>

            <Form.Item label="電話" name="phone">
              <Input />
            </Form.Item>

            <Form.Item label="備註" name="notes">
              <Input.TextArea />
            </Form.Item>
          </Form>
        )}
      </Modal>
    </div>
  );
};

export default CRMCustomer;