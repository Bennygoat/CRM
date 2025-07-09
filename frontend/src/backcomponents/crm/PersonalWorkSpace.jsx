import React from 'react';
import { CalendarOutlined, ExclamationCircleOutlined, ClockCircleOutlined, UserOutlined } from '@ant-design/icons';

const ActivityCard = ({ activity, type }) => {
  const isOverdue = type === 'overdue';
  const borderColor = isOverdue ? 'border-red-500' : 'border-blue-500';
  return (
    <div className={`bg-white p-4 rounded-lg shadow-sm border-l-4 ${borderColor} mb-3`}>
      <p className="font-semibold text-gray-800">{activity.title}</p>
      <div className="mt-2 text-sm text-gray-500 space-y-2">
        {isOverdue ? (
          <div className="flex items-center text-red-600 font-medium"><ExclamationCircleOutlined className="mr-2" /><span>已逾期 {activity.overdueDays} 天</span></div>
        ) : (
          <div className="flex items-center"><ClockCircleOutlined className="mr-2" /><span>{activity.time}</span></div>
        )}
        {activity.contact && <div className="flex items-center"><UserOutlined className="mr-2" /><span>{activity.contact.name}</span></div>}
      </div>
    </div>
  );
};

const PersonalWorkSpace = () => {
  const todayTasksData = [
    { activityId: 35, title: "網紅行銷合約簽署會議", time: "10:00 AM - 11:00 AM", contact: { id: 27, name: "廖文斌" }},
    { activityId: 36, title: "追蹤網紅合約執行細節", time: "02:00 PM - 05:00 PM", contact: { id: 27, name: "廖文斌" }}
  ];

  const overdueTasksData = [
    { activityId: 14, title: "寄送合作簡介資料", overdueDays: 5, contact: { id: 25, name: "羅佩芬" }},
    { activityId: 13, title: "健康冰沙合作初步接洽電話", overdueDays: 3, contact: { id: 25, name: "羅佩芬" }}
  ];

  return (
    <div className="bg-gray-50 p-6 rounded-xl h-full shadow-inner">
      {/* 今日待辦區塊 */}
      <div className="mb-8">
        <h3 className="text-lg font-bold text-gray-700 mb-4 flex items-center">
          <CalendarOutlined className="mr-2 text-blue-600" />
          今日待辦
        </h3>
        {todayTasksData.length > 0 ?
          todayTasksData.map(task => <ActivityCard key={task.activityId} activity={task} type="today" />) :
          <p className="text-gray-400 px-2">沒有項目</p>
        }
      </div>

      {/* 逾期提醒區塊 */}
      <div>
        <h3 className="text-lg font-bold text-gray-700 mb-4 flex items-center">
          <ExclamationCircleOutlined className="mr-2 text-red-600" />
          逾期提醒
        </h3>
        {overdueTasksData.length > 0 ?
          overdueTasksData.map(task => <ActivityCard key={task.activityId} activity={task} type="overdue" />) :
          <p className="text-gray-400 px-2">沒有項目</p>
        }
      </div>
    </div>
  );
};

export default PersonalWorkSpace;