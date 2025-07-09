import React from 'react';
import PersonalWorkSpace from '../../backcomponents/crm/PersonalWorkSpace.jsx';
import MainDashboardContent from '../../backcomponents/crm/MainDashboardContent.jsx';



const MainDashboardPage = () => {
  return (
    <div className="p-6 h-full">
      <div className="grid grid-cols-3 gap-6 h-full">
        {/* 左側主要內容區 */}
        <div className="col-span-2 bg-white p-6 rounded-xl shadow-sm">
           <MainDashboardContent />
        </div>

        {/* 右側個人工作區 */}
        <div className="col-span-1">
          <PersonalWorkSpace />
        </div>
      </div>
    </div>
  );
};

export default MainDashboardPage;