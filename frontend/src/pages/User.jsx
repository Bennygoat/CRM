import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import useUserStore from '../stores/userStore';
import PersonalInfo from '../components/tabs/PersonalInfo';
import StoreCredits from '../components/tabs/StoreCredits';
import Coupons from '../components/tabs/Coupons';
import Messages from '../components/tabs/Messages';
import Orders from '../components/tabs/Orders';

const tabs = ['個人資訊', '商店購物金', '優惠券', '訊息', '訂單'];

const tabComponents = [
  <PersonalInfo />,
  <StoreCredits />,
  <Coupons />,
  <Messages />,
  <Orders />,
];

function User() {
  const [activeTab, setActiveTab] = useState(0);
  const navigate = useNavigate();
  const user = useUserStore((state) => state.user);
  const logout = useUserStore((state) => state.logout);

  return (
    <div className="min-h-screen  text-gray-800 px-4 md:px-12 py-8">
      {/* Header */}
      <div className="flex justify-between items-center mb-6 text-sm">
        <div>
          你好, <span className="font-bold">{user?.customerName || '訪客'}</span>
        </div>
        <div className="flex items-center gap-4">
          <button
            className="text-logo-blue hover:underline"
            onClick={() => {
              logout();
              navigate('/login');
            }}
          >
            登出
          </button>
        </div>
      </div>

      {/* Tabs */}
      <div className="overflow-x-auto">
        <div className="grid grid-cols-5 min-w-[500px] border-b border-gray-200 text-center text-sm font-medium tracking-wide">
          {tabs.map((tab, index) => (
            <div
              key={index}
              onClick={() => setActiveTab(index)}
              className={`py-3 cursor-pointer transition-all duration-200 ${
                activeTab === index
                  ? 'text-logo-blue border-b-2 border-logo-blue bg-white font-semibold'
                  : 'text-gray-500 hover:bg-gray-100'
              }`}
            >
              {tab}
            </div>
          ))}
        </div>
      </div>

      {/* Content */}
      <div className="bg-white min-h-[400px] p-6 mt-4 ">
        {tabComponents[activeTab]}
      </div>
    </div>
  );
}

export default User;
