import { BarChartOutlined,
  UserOutlined,
  AppstoreOutlined,
  RobotOutlined,
  DesktopOutlined,
 } from '@ant-design/icons';
 import { useNavigate } from 'react-router-dom';


const crmConfig = {
  route: {
    path: '/',
    routes: [
      {
        path: '/crm/dashboard',
        name: 'CRM 儀表板',
        icon: <BarChartOutlined />,
      },
      {
        path: '/crm/calender',
        name: '行事曆',
        icon: <BarChartOutlined />,
      },
      {
        path: '/crm/company',
        name: '客戶資料',
        icon: <BarChartOutlined />,
      },
      {
        path: '/crm/customer',
        name: '聯絡人資料',
        icon: <BarChartOutlined />,
      },
      {
        path: '/crm/opportunity',
        name: '商機管理',
        icon: <BarChartOutlined />,
      },
      {
        path: '/crm/salesfunnel',
        name: '銷售漏斗',
        icon: <BarChartOutlined />,
      },
    ],
  },
  location: {
    pathname: '/crm/dashboard',
  },
};
export default crmConfig;
