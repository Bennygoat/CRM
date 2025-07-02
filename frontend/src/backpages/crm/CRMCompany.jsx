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
  Select,
} from "antd";
import { useNavigate } from "react-router-dom";
import axios from "../../api/axiosBackend";

const PAGE_SIZE = 10;

const industryOptions = [
  { label: "科技業", value: "TECHNOLOGY" },
  { label: "金融業", value: "FINANCE" },
  { label: "零售業", value: "RETAIL" },
  { label: "製造業", value: "MANUFACTURING" },
  { label: "醫療保健業", value: "HEALTHCARE" },
  { label: "教育業", value: "EDUCATION" },
  { label: "房地產業", value: "REAL_ESTATE" },
  { label: "顧問業", value: "CONSULTING" },
  { label: "娛樂業", value: "ENTERTAINMENT" },
  { label: "政府機關", value: "GOVERNMENT" },
  { label: "其他", value: "OTHER" },
];

const typeOptions = [
  { label: "潛在客戶", value: "PROSPECT" },
  { label: "合作夥伴", value: "PARTNER" },
  { label: "供應商", value: "VENDOR" },
  { label: "重要客戶", value: "KEY_ACCOUNT" },
  { label: "一般客戶", value: "REGULAR" },
  { label: "非活躍客戶", value: "INACTIVE" },
  { label: "流失客戶", value: "LOST" },
];

const levelOptions = [
  { label: "鑽石級", value: "DIAMOND" },
  { label: "白金級", value: "PLATINUM" },
  { label: "黃金級", value: "GOLD" },
  { label: "白銀級", value: "SILVER" },
  { label: "青銅級", value: "BRONZE" },
  { label: "新客戶", value: "NEW" },
];

const CRMCompany = () => {
  const actionRef = useRef();
  const navigate = useNavigate();

  const [data, setData] = useState([]);
  const [total, setTotal] = useState(0);
  const [currentPage, setCurrentPage] = useState(1);
  const [loading, setLoading] = useState(true);
  const [selectedCompany, setSelectedCompany] = useState(null);
  const [detailVisible, setDetailVisible] = useState(false);
  const [deleteVisible, setDeleteVisible] = useState(false);
  const [deletingId, setDeletingId] = useState(null);

  const [editVisible, setEditVisible] = useState(false);
  const [editCompany, setEditCompany] = useState(null);
  const [form] = Form.useForm();

  const fetchData = async (page = 1) => {
    setLoading(true);
    try {
      const res = await axios.get("/customers", {
        params: {
          page: page - 1,
          size: PAGE_SIZE,
          sort: "customerId",
        },
      });
      setData(res.data.content || []);
      setTotal(res.data.totalElements || 0);
    } catch (error) {
      console.error("客戶資料抓取失敗:", error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData(currentPage);
  }, [currentPage]);

  const fetchCompanyDetail = async (id) => {
    try {
      const res = await axios.get(`/customers/${id}`);
      setSelectedCompany(res.data);
      setDetailVisible(true);
    } catch (error) {
      console.error("客戶詳細資料取得失敗:", error);
      message.error("無法取得客戶資料");
    }
  };

  const handleEdit = async (id) => {
    try {
      const res = await axios.get(`/customers/${id}`);
      const company = res.data;
      setEditCompany(company);
      form.setFieldsValue({
        customerName: company.customerName,
        industry: company.industry,
        customerAddress: company.customerAddress,
        customerTel: company.customerTel,
        customerEmail: company.customerEmail,
        bcustomerType: company.bcustomerType,
        bcustomerLevel: company.bcustomerLevel,
      });
      setEditVisible(true);
    } catch (err) {
      console.error("取得客戶資料失敗", err);
      message.error("無法取得資料");
    }
  };

  const handleAdd = () => {
    form.resetFields();
    setEditCompany(null);
    setEditVisible(true);
  };

  const handleDelete = (id) => {
    setDeletingId(id);
    setDeleteVisible(true);
  };

  const confirmDelete = async () => {
    try {
      await axios.delete(`/customers/${deletingId}`);
      message.success("刪除成功");
      setDeleteVisible(false);
      fetchData(currentPage);
    } catch (error) {
      console.error("刪除失敗:", error);
      message.error("刪除失敗");
    }
  };

  const columns = [
    {
      title: "客戶名稱",
      dataIndex: "customerName",
      copyable: true,
      ellipsis: true,
    },
    {
      title: "產業別",
      dataIndex: "industry",
      ellipsis: true,
    },
    {
      title: "客戶等級",
      dataIndex: "bcustomerLevel",
      ellipsis: true,
    },
    {
      title: "建立時間",
      dataIndex: "createdAt",
      valueType: "date",
    },
    {
      title: "操作",
      valueType: "option",
      key: "option",
      render: (_, record) => [
        <div key="actions" onClick={(e) => e.stopPropagation()}>
          <a onClick={() => handleEdit(record.customerId)}>編輯</a>
          <TableDropdown
            menus={[{ key: "delete", name: "刪除" }]}
            onSelect={(key) => {
              if (key === "delete") {
                handleDelete(record.customerId);
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
        rowKey="customerId"
        search={false}
        pagination={false}
        dateFormatter="string"
        headerTitle="客戶列表"
        options={false}
        onRow={(record) => ({
          onClick: () => fetchCompanyDetail(record.customerId),
        })}
        toolBarRender={() => [
          <Button
            key="new"
            icon={<PlusOutlined />}
            type="primary"
            onClick={handleAdd}
          >
            新增客戶
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
        title="客戶詳細資料"
        open={detailVisible}
        onCancel={() => setDetailVisible(false)}
        footer={null}
      >
        {selectedCompany && (
          <Descriptions column={1} bordered size="small">
            <Descriptions.Item label="客戶名稱">
              {selectedCompany.customerName}
            </Descriptions.Item>
            <Descriptions.Item label="產業別">
              {selectedCompany.industry || "-"}
            </Descriptions.Item>
            <Descriptions.Item label="客戶等級">
              {selectedCompany.bcustomerLevel || "-"}
            </Descriptions.Item>
            <Descriptions.Item label="建立時間">
              {selectedCompany.createdAt}
            </Descriptions.Item>
          </Descriptions>
        )}
      </Modal>

      <Modal
        title={editCompany ? "編輯客戶" : "新增客戶"}
        open={editVisible}
        onCancel={() => setEditVisible(false)}
        onOk={() => form.submit()}
        okText="儲存"
      >
        <Form
          layout="vertical"
          form={form}
          onFinish={async (values) => {
            try {
              if (editCompany) {
                await axios.put(`/customers/${editCompany.customerId}`, values);
                message.success("更新成功");
              } else {
                await axios.post(`/customers`, values);
                message.success("新增成功");
              }
              setEditVisible(false);
              fetchData(currentPage);
            } catch (error) {
              console.error("儲存失敗", error);
              message.error("儲存失敗");
            }
          }}
        >
          <Form.Item
            label="客戶名稱"
            name="customerName"
            rules={[{ required: true, message: "客戶名稱不可為空" }]}
          >
            <Input />
          </Form.Item>

          <Form.Item label="產業別" name="industry">
            <Select options={industryOptions} allowClear />
          </Form.Item>

          <Form.Item label="客戶類型" name="bcustomerType">
            <Select options={typeOptions} allowClear />
          </Form.Item>

          <Form.Item label="客戶等級" name="bcustomerLevel">
            <Select options={levelOptions} allowClear />
          </Form.Item>

          <Form.Item label="地址" name="customerAddress">
            <Input />
          </Form.Item>

          <Form.Item label="電話" name="customerTel">
            <Input />
          </Form.Item>

          <Form.Item
            label="Email"
            name="customerEmail"
            rules={[
              { required: true, message: "Email 不可為空" },
              { type: "email", message: "Email 格式不正確" },
            ]}
          >
            <Input />
          </Form.Item>
        </Form>
      </Modal>

      <Modal
        title="確認刪除"
        open={deleteVisible}
        onOk={confirmDelete}
        onCancel={() => setDeleteVisible(false)}
        okText="確定"
        cancelText="取消"
      >
        <p>確定要刪除這個客戶嗎？</p>
      </Modal>
    </div>
  );
};

export default CRMCompany;