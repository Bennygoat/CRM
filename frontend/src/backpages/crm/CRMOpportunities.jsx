import React, { useRef, useState, useEffect } from 'react';
import { PlusOutlined } from '@ant-design/icons';
import { ProTable, TableDropdown } from '@ant-design/pro-components';
import {
  Button,
  Pagination,
  Tag,
  Modal,
  Descriptions,
  Form,
  Input,
  InputNumber,
  DatePicker,
  Select,
  message,
} from 'antd';
import { useNavigate } from 'react-router-dom';
import axios from '../../api/axiosBackend';
import dayjs from 'dayjs';

const PAGE_SIZE = 10;

const stageMap = {
  '初步接洽': 'INITIAL_CONTACT',
  '需求分析': 'NEEDS_ANALYSIS',
  '提案': 'PROPOSAL',
  '談判': 'NEGOTIATION',
  '已成交': 'CLOSED_WON',
  '已丟失': 'CLOSED_LOST',
};
const reverseStageMap = Object.fromEntries(
  Object.entries(stageMap).map(([k, v]) => [v, k])
);
const stageOptions = Object.keys(stageMap).map((label) => ({
  label,
  value: label,
}));

const CRMOpportunities = () => {
  const actionRef = useRef();
  const navigate = useNavigate();
  const [data, setData] = useState([]);
  const [total, setTotal] = useState(0);
  const [currentPage, setCurrentPage] = useState(1);
  const [searchParams, setSearchParams] = useState({});
  const [loading, setLoading] = useState(true);

  const [detailVisible, setDetailVisible] = useState(false);
  const [selectedOpportunity, setSelectedOpportunity] = useState(null);
  const [editVisible, setEditVisible] = useState(false);
  const [form] = Form.useForm();

  const fetchData = async (page = 1, params = {}) => {
    setLoading(true);
    try {
      const res = await axios.get('/opportunities/search', {
        params: {
          page: page - 1,
          size: PAGE_SIZE,
          sort: 'opportunityId',
          ...params,
        },
      });
      setData(res.data.content);
      setTotal(res.data.totalElements);
    } catch (error) {
      console.error('商機資料抓取失敗:', error);
    } finally {
      setLoading(false);
    }
  };

  const fetchDetail = async (id) => {
    try {
      const res = await axios.get(`/opportunities/${id}`);
      setSelectedOpportunity(res.data);
      setDetailVisible(true);
    } catch (err) {
      message.error('無法取得商機詳情');
    }
  };

  const openEdit = () => {
    form.setFieldsValue({
      opportunityName: selectedOpportunity.opportunityName,
      description: selectedOpportunity.description,
      expectedValue: selectedOpportunity.expectedValue,
      closeDate: dayjs(selectedOpportunity.closeDate),
      stage: selectedOpportunity.stage,
    });
    setEditVisible(true);
  };

  const handleUpdate = async (values) => {
    try {
      await axios.put(`/opportunities/${selectedOpportunity.opportunityId}`, {
        opportunityName: values.opportunityName,
        description: values.description,
        expectedValue: values.expectedValue,
        closeDate: values.closeDate.format('YYYY-MM-DD'),
        stage: values.stage,
      });
      message.success('更新成功');
      setEditVisible(false);
      setDetailVisible(false);
      fetchData(currentPage, searchParams);
    } catch (err) {
      message.error('更新失敗');
    }
  };

  useEffect(() => {
    fetchData(currentPage, searchParams);
  }, [currentPage, searchParams]);

  const columns = [
    {
      title: '商機名稱',
      dataIndex: 'opportunityName',
      formItemProps: { name: 'name' },
      fieldProps: { placeholder: '搜尋商機名稱' },
      copyable: true,
      ellipsis: true,
    },
    {
      title: '客戶名稱',
      dataIndex: 'customerName',
      search: false,
    },
    {
      title: '聯絡人',
      dataIndex: 'contactName',
      search: false,
      render: (text) => text || '-',
    },
    {
      title: '銷售階段',
      dataIndex: 'stage',
      valueType: 'select',
      formItemProps: { name: 'stage' },
      fieldProps: { placeholder: '選擇銷售階段' },
      valueEnum: {
        INITIAL_CONTACT: { text: '初步接洽', status: 'Default' },
        NEEDS_ANALYSIS: { text: '需求分析', status: 'Processing' },
        PROPOSAL: { text: '提案', status: 'Processing' },
        NEGOTIATION: { text: '談判', status: 'Warning' },
        CLOSED_WON: { text: '已成交', status: 'Success' },
        CLOSED_LOST: { text: '已丟失', status: 'Error' },
      },
      render: (_, record) => (
        <Tag>{reverseStageMap[record.stage] || record.stage}</Tag>
      ),
    },
    {
      title: '預計成交日',
      dataIndex: 'closeDate',
      valueType: 'date',
      hideInSearch: true,
    },
    {
      title: '建立時間',
      dataIndex: 'createdAt',
      valueType: 'dateTime',
      hideInSearch: true,
    },
    {
      title: '操作',
      valueType: 'option',
      key: 'option',
      render: (_, record) => [
        <a key="edit" onClick={() => fetchDetail(record.opportunityId)}>編輯</a>,
        <TableDropdown
          key="dropdown"
          menus={[
            { key: 'delete', name: '刪除' },
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
        rowKey="opportunityId"
        search={{
          labelWidth: 'auto',
          searchText: '搜尋',
          resetText: '清除',
        }}
        pagination={false}
        dateFormatter="string"
        headerTitle="商機列表"
        options={false}
        onSubmit={(values) => {
          setSearchParams(values);
          setCurrentPage(1);
        }}
        onReset={() => {
          setSearchParams({});
          setCurrentPage(1);
        }}
        onRow={(record) => ({
          onClick: () => fetchDetail(record.opportunityId),
        })}
        toolBarRender={() => [
          <Button
            key="new"
            icon={<PlusOutlined />}
            type="primary"
            onClick={() => navigate('/crm/opportunity/new')}
          >
            新增商機
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
        title="商機詳情"
        open={detailVisible}
        onCancel={() => setDetailVisible(false)}
        footer={[
          <Button key="edit" onClick={openEdit}>編輯</Button>,
          <Button key="close" onClick={() => setDetailVisible(false)}>關閉</Button>,
        ]}
      >
        {selectedOpportunity && (
          <Descriptions column={1} bordered size="small">
            <Descriptions.Item label="商機名稱">{selectedOpportunity.opportunityName}</Descriptions.Item>
            <Descriptions.Item label="預估金額">${selectedOpportunity.expectedValue?.toLocaleString()}</Descriptions.Item>
            <Descriptions.Item label="說明">{selectedOpportunity.description || '-'}</Descriptions.Item>
            <Descriptions.Item label="成交日">{selectedOpportunity.closeDate}</Descriptions.Item>
            <Descriptions.Item label="階段">{selectedOpportunity.stage}</Descriptions.Item>
            <Descriptions.Item label="客戶">{selectedOpportunity.customerName}</Descriptions.Item>
            <Descriptions.Item label="聯絡人">{selectedOpportunity.contactName || '-'}</Descriptions.Item>
          </Descriptions>
        )}
      </Modal>

      <Modal
        title="編輯商機"
        open={editVisible}
        onCancel={() => setEditVisible(false)}
        onOk={() => form.submit()}
        okText="儲存"
      >
        <Form layout="vertical" form={form} onFinish={handleUpdate}>
          <Form.Item name="opportunityName" label="商機名稱" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="description" label="說明">
            <Input.TextArea />
          </Form.Item>
          <Form.Item name="expectedValue" label="預估金額">
            <InputNumber style={{ width: '100%' }} min={0} />
          </Form.Item>
          <Form.Item name="closeDate" label="成交日" rules={[{ required: true }]}>
            <DatePicker style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="stage" label="銷售階段" rules={[{ required: true }]}>
            <Select options={stageOptions} />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default CRMOpportunities;
