// routes/appListConfig.js
import {
  UserOutlined,
  AppstoreOutlined,
  RobotOutlined,
  BarChartOutlined,
  DesktopOutlined,
} from "@ant-design/icons";

const appListConfig = [
  {
    key: 'users',
    icon: <UserOutlined style={{ fontSize: "30px", color: "#08c" }} />,
    title: "使用者權限",
    desc: "管理使用者權限",
    url: "/users/management",
    roles: ['admin'], //
  },
  {
    key: 'erp',
    icon: <AppstoreOutlined style={{ fontSize: "30px", color: "#08c" }} />,
    title: "ERP模組",
    desc: "企業資源規劃模組",
    url: "/erp/dashboard",
    roles: ['admin', 'manager'],
  },
  {
    key: 'crm',
    icon: <BarChartOutlined style={{ fontSize: "30px", color: "#08c" }} />,
    title: "CRM模組",
    desc: "客戶關係管理模組",
    url: "/crm/dashboard",
    roles: ['admin'],
  },
  {
    key: 'cms',
    icon: <DesktopOutlined style={{ fontSize: "30px", color: "#08c" }} />,
    title: "CMS模組",
    desc: "內容管理系統模組",
    url: "/cms/dashboard",
    roles: ['admin', 'editor'],
  },
];

export default appListConfig;
