import React, { useEffect, useState } from 'react';
import {
  Calendar,
  Select,
  Form,
  Input,
  DatePicker,
  TimePicker,
  Modal,
  Button,
  Badge,
  Tooltip,
  message,
} from 'antd';
import dayjs from 'dayjs';
import axios from '../../api/axiosBackend';

const { Option } = Select;
const { TextArea } = Input;

const CRMCalendar = () => {
  const [current, setCurrent] = useState(dayjs());
  const [selectedDate, setSelectedDate] = useState(null);
  const [modalVisible, setModalVisible] = useState(false);
  const [eventsData, setEventsData] = useState({});
  const [createModalVisible, setCreateModalVisible] = useState(false);
  const [form] = Form.useForm();

  const currentYear = dayjs().year();
  const years = Array.from({ length: currentYear - 2015 + 1 }, (_, i) => 2015 + i);
  const months = Array.from({ length: 12 }, (_, i) => i);

  const colorMap = {
    MEETING: 'processing',
    CALL: 'warning',
    TASK: 'default',
    EMAIL: 'success',
    DEADLINE: 'error',
  };

  const fetchData = async () => {
    try {
      const start = `${current.year()}-01-01`;
      const end = `${current.year() + 1}-01-01`;
      const res = await axios.get('/activities', { params: { start, end } });

      const grouped = {};
      res.data.forEach((event) => {
        const date = dayjs(event.startTime).format('YYYY-MM-DD');
        if (!grouped[date]) grouped[date] = [];
        grouped[date].push({
          title: event.title,
          content: event.notes,
          time: `${dayjs(event.startTime).format('HH:mm')} - ${dayjs(event.endTime).format('HH:mm')}`,
          type: colorMap[event.activityType] || 'default',
        });
      });

      setEventsData(grouped);
    } catch (error) {
      console.error('載入活動資料失敗：', error);
    }
  };

  useEffect(() => {
    fetchData();
  }, [current.year()]);

  const getListData = (value) => {
    const key = value.format('YYYY-MM-DD');
    return eventsData[key] || [];
  };

  const dateCellRender = (value) => {
    const list = getListData(value);
    const hasEvent = list.length > 0;

    const content = (
      <ul className="space-y-0.5">
        {list.map((item, index) => (
          <li key={index}>
            <Badge status={item.type} text={item.title} />
          </li>
        ))}
      </ul>
    );

    return hasEvent ? (
      <Tooltip title="雙擊可查看詳細內容" placement="top">
        <div
          onDoubleClick={() => {
            setSelectedDate({
              date: value.format('YYYY-MM-DD'),
              events: list,
            });
            setModalVisible(true);
          }}
          className="cursor-pointer px-1"
        >
          {content}
        </div>
      </Tooltip>
    ) : content;
  };

  const customHeader = ({ value, onChange }) => (
    <div className="flex items-center justify-between px-4 py-2 border-b border-gray-200 bg-white">
      <div className="text-base font-semibold text-gray-700">
        {value.format('YYYY 年 MM 月')}
      </div>

      <div className="flex items-center gap-2">
        <Select
          value={value.year()}
          onChange={(val) => onChange(value.clone().year(val))}
          size="middle"
          className="w-28 text-base"
        >
          {years.map((y) => (
            <Option key={y} value={y}>
              {y}年
            </Option>
          ))}
        </Select>

        <Select
          value={value.month()}
          onChange={(val) => onChange(value.clone().month(val))}
          size="middle"
          className="w-24 text-base"
        >
          {months.map((m) => (
            <Option key={m} value={m}>
              {m + 1}月
            </Option>
          ))}
        </Select>

        <Button type="default" size="middle" onClick={() => onChange(dayjs())}>
          今天
        </Button>

        <Button type="primary" onClick={() => setCreateModalVisible(true)}>
          新增活動
        </Button>
      </div>
    </div>
  );

  const handleCreate = async () => {
    try {
      const values = await form.validateFields();
      const startTime = dayjs(values.date).hour(values.startTime.hour()).minute(values.startTime.minute());
      const endTime = dayjs(values.date).hour(values.endTime.hour()).minute(values.endTime.minute());

      const payload = {
        title: values.title,
        activityType: values.activityType,
        startTime: startTime.toISOString(),
        endTime: endTime.toISOString(),
        notes: values.notes,
        opportunityId: 0,
        contactId: 0,
      };

      await axios.post('/activities', payload);
      message.success('活動新增成功');
      setCreateModalVisible(false);
      form.resetFields();
      fetchData();
    } catch (err) {
      console.error(err);
      message.error('請確認欄位是否正確填寫');
    }
  };

  return (
    <div className="p-4 bg-white">
      <Calendar
        value={current}
        onChange={setCurrent}
        cellRender={dateCellRender}
        headerRender={customHeader}
      />

      <Modal
        open={modalVisible}
        title={`事件詳情：${selectedDate?.date}`}
        footer={null}
        onCancel={() => setModalVisible(false)}
      >
        <ul className="space-y-2">
          {selectedDate?.events.map((event, index) => (
            <li key={index} className="flex flex-col gap-1 border-b pb-2">
              <div className="flex items-center gap-2">
                <Badge status={event.type} />
                <span className="font-semibold">{event.title}</span>
              </div>
              <div className="text-gray-600 text-sm">{event.content}</div>
              <div className="text-gray-500 text-xs">時間：{event.time}</div>
            </li>
          ))}
        </ul>
      </Modal>

      {/* 新增活動 */}
      <Modal
        open={createModalVisible}
        title="新增活動"
        onCancel={() => setCreateModalVisible(false)}
        onOk={handleCreate}
        okText="新增"
      >
        <Form layout="vertical" form={form}>
          <Form.Item name="title" label="標題" rules={[{ required: true }]}>
            <Input />
          </Form.Item>

          <Form.Item name="activityType" label="活動類型" rules={[{ required: true }]}>
            <Select>
              <Option value="MEETING">會議</Option>
              <Option value="CALL">電話</Option>
              <Option value="TASK">任務</Option>
              <Option value="EMAIL">郵件</Option>
              <Option value="DEADLINE">截止日</Option>
            </Select>
          </Form.Item>

          <Form.Item name="date" label="日期" rules={[{ required: true }]}>
            <DatePicker className="w-full" />
          </Form.Item>

          <Form.Item label="開始時間" name="startTime" rules={[{ required: true }]}>
            <TimePicker className="w-full" format="HH:mm" />
          </Form.Item>

          <Form.Item label="結束時間" name="endTime" rules={[{ required: true }]}>
            <TimePicker className="w-full" format="HH:mm" />
          </Form.Item>

          <Form.Item name="notes" label="備註">
            <TextArea rows={3} />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default CRMCalendar;
