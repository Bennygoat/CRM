import { BarChartOutlined,
  UserOutlined,
  AppstoreOutlined,
  RobotOutlined,
  DesktopOutlined,
  CalendarOutlined,
  TeamOutlined,
  LineChartOutlined,
  FunnelPlotOutlined,
  TagOutlined,
  PartitionOutlined,
 } from '@ant-design/icons';
 import { useNavigate } from 'react-router-dom';


const omConfig = {
  route: {
    path: '/',
    routes: [
      {
        path: 'om/dashboard',
        name: '流程圖',
        icon: <PartitionOutlined />,
      },
      {
        path: 'om/calendar',
        name: '行事曆',
        icon: <CalendarOutlined />,
      },
      {
        path: 'om/order',
        name: '訂單管理',
        icon: <AppstoreOutlined />,
      },
      {
        path: 'om/coupon',
        name: '優惠卷管理',
        icon: <TagOutlined />,
      },
    ],
  },
  location: {
    pathname: '/om/dashboard',
  },
};
export default omConfig;
