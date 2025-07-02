import { UserOutlined ,
  AppstoreOutlined, 
  RobotOutlined, 
  BarChartOutlined, 
  DesktopOutlined
} from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';


const usersConfig = {
  route: {
    path: '/',
    routes: [
      {
        path: '/users/management',
        name: '使用者管理',
        icon: <UserOutlined />,
        routes: [
          {
            path: '/users/management/authority',
            name: '權限管理',
            hideInMenu: true,
          },
        ],    
      },
      {
        path: '/users/logs',
        name: '操作紀錄',
        icon: <UserOutlined />,
      },
      {
        path: '/users/message',
        name: '客服訊息',
        icon: <RobotOutlined />,
      },
    ],
  },
};
export default usersConfig;
