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
            path: '/users/management/edit',
            name: '使用者權限',
            hideInMenu: true,
          },
          {
            path: '/users/management/register',
            name: '新增使用者',
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
        routes: [
          {
            path: '/users/message/:messageId',
            name: '訊息對話',
            hideInMenu: true,
          },
        ],
      },
    ],
  },
};
export default usersConfig;
