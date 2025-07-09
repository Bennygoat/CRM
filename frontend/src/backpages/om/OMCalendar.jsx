import React from 'react';
import { Calendar, Badge } from 'antd';
import dayjs from 'dayjs';
import 'dayjs/locale/zh-tw';

dayjs.locale('zh-tw');

const eventTypeConfig = {
  SHIPPING_DUE: { status: 'processing', text: '預計出貨' },
  DELIVERY_DUE: { status: 'success', text: '預計到貨' },
  PAYMENT_DUE: { status: 'error', text: '付款截止' },
  PURCHASE_DUE: { status: 'warning', text: '物料採購' },
};

const fakeCalendarEvents = [
  { date: '2025-07-10', type: 'SHIPPING_DUE', content: '訂單 #20250708-001' },
  { date: '2025-07-11', type: 'PURCHASE_DUE', content: '採購台灣愛文芒果' },
  { date: '2025-07-12', type: 'SHIPPING_DUE', content: '訂單 #20250708-002' },
  { date: '2025-07-14', type: 'DELIVERY_DUE', content: '訂單 #20250707-005' },
  { date: '2025-07-15', type: 'PAYMENT_DUE', content: '客戶「全球百貨」款項截止' },
  { date: '2025-07-15', type: 'SHIPPING_DUE', content: '訂單 #20250710-004' },
];


const OMCalendar = () => {

  const dateCellRender = (value) => {
    const dateStr = value.format('YYYY-MM-DD');
    const listData = fakeCalendarEvents.filter(item => item.date === dateStr);

    return (
      <ul className="events" style={{ listStyle: 'none', margin: 0, padding: 0 }}>
        {listData.map((item, index) => (
          <li key={index}>
            <Badge
              status={eventTypeConfig[item.type]?.status || 'default'}
              text={`${eventTypeConfig[item.type]?.text}: ${item.content}`}
            />
          </li>
        ))}
      </ul>
    );
  };

  return (
    <div className="p-6 bg-white rounded-lg shadow-sm">
      <h1 className="text-2xl font-bold mb-4">行事曆</h1>
      <Calendar dateCellRender={dateCellRender} />
    </div>
  );
};

export default OMCalendar;