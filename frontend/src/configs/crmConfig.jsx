import { BarChartOutlined,
  UserOutlined,
  AppstoreOutlined,
  RobotOutlined,
  DesktopOutlined,
  CalendarOutlined,
  TeamOutlined,
  LineChartOutlined,
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
        icon: <CalendarOutlined />,
      },
      {
        path: '/crm/company',
        name: '客戶資料',
        icon: <TeamOutlined />,
      },
      {
        path: '/crm/customer',
        name: '聯絡人資料',
        icon: <UserOutlined />,
      },
      {
        path: '/crm/opportunity',
        name: '商機管理',
        icon: <AppstoreOutlined />,
      },
      {
        path: '/crm/salesfunnel',
        name: '銷售漏斗',
        icon: <LineChartOutlined />,
      },
    ],
  },
  location: {
    pathname: '/crm/dashboard',
  },
};
export default crmConfig;
