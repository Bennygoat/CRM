import { createBrowserRouter } from "react-router-dom";
import App from "./App.jsx";
import Home from "./pages/Home";
import Store from "./pages/Store";
import About from "./pages/About";
import News from "./pages/News";
import Contact from "./pages/Contact";
import Login from "./pages/Login";
import FunnyError from "./components/FunnyError";
import Product from "./pages/Product.jsx";
import NewsDetail from "./pages/NewsDetail.jsx";
import User from "./pages/User.jsx";
import SignFlow from "./pages/SignFlow.jsx";
import SignSuccess from "./pages/SignSuccess.jsx";
import Cart from "./pages/Cart.jsx";
import ECPayRedirect from "./components/ECPayRedirect.jsx";

import BaseLayout from "./layout/BaseLayout";
import cmsConfig from "./configs/cmsConfig";
import erpConfig from "./configs/erpConfig";
import crmConfig from "./configs/crmConfig";
import usersConfig from "./configs/usersConfig.jsx";
import appListConfig from "./configs/appListConfig";
import useBackUserStore from "./stores/useBackUserStore";
import "antd/dist/reset.css";
import BackLogin from "./backpages/BackLogin.jsx";
//CRM相關頁面
import CRMCustomer from "./backpages/crm/CRMCustomer.jsx";
import CRMDashboard from "./backpages/crm/CRMDashboard.jsx";
import SalesFunnel from "./backpages/crm/SalesFunnel.jsx";
import CRMOpportunities from "./backpages/crm/CRMOpportunities.jsx";
import CRMOpportunityForm from "./backpages/crm/CRMOpportunityForm.jsx";
import CRMCustomerForm from "./backpages/crm/CRMCustomerForm.jsx";
import CRMCalendar from "./backpages/crm/CRMCalendar.jsx";
import CRMCompany from "./backpages/crm/CRMCompany.jsx";
//ERP相關頁面
import ERPOrders from "./backpages/erp/ERPOrders.jsx";
import ERPProducts from "./backpages/erp/ERPProducts.jsx";
import ERPStockLevels from "./backpages/erp/ERPStockLevels.jsx";
import ERPPurchaseOrders from "./backpages/erp/ERPPurchaseOrders.jsx";
import ERPCustomers from "./backpages/erp/ERPCustomers.jsx";
import ERPDashboard from "./backpages/erp/ERPDashboard.jsx";
import ERPOrderForm from "./backpages/erp/ERPOrderForm.jsx";
import ERPNewOrderForm from "./backpages/erp/ERPNewOrderForm.jsx";
// 使用者角色和權限
import UsersManage from "./backpages/users/usersManage.jsx";
import UsersLogs from "./backpages/users/usersLogs.jsx";
import UsersEdit from "./backcomponents/user/usersEdit.jsx";
import UsersMessage from "./backpages/users/usersMessage.jsx";
import UsersRegister from "./backcomponents/user/usersRegister.jsx";
import UserMessageChat from "./backcomponents/user/MessageChat.jsx";

const user = useBackUserStore.getState().backUser;
const role = user?.role || "admin";
const getFilteredAppList = (role) =>
  appListConfig.filter((item) => item.roles.includes(role));
const filteredAppList = getFilteredAppList(role);

const router = createBrowserRouter([
  {
    path: "/",
    element: <App />, // 前台 layout
    children: [
      { index: true, element: <Home /> },
      { path: "store", element: <Store /> },
      { path: "about", element: <About /> },
      { path: "news", element: <News /> },
      { path: "contact", element: <Contact /> },
      { path: "login", element: <Login /> },
      { path: "signFlow", element: <SignFlow /> },
      { path: "signsuccess", element: <SignSuccess /> },
      { path: "product/:id", element: <Product /> },
      { path: "cart", element: <Cart /> },
      { path: "funnyerror", element: <FunnyError /> },
      { path: "user", element: <User /> },
      { path: "news/:id", element: <NewsDetail /> },
      {path: "ecpay-redirect", element: <ECPayRedirect />}, 
    ],
  },
  {path: "/backlogin", element: <BackLogin />}, // 後台登入頁面
  {
    path: "/cms/*",
    element: ["admin", "editor"].includes(role) ? (
      <BaseLayout menuConfig={cmsConfig} appListConfig={filteredAppList} />
    ) : (
      <FunnyError />
    ),
  },
  {
    path: "/crm/*",
    element:
      role === "admin" ? (
        <BaseLayout menuConfig={crmConfig} appListConfig={filteredAppList} />
      ) : (
        <FunnyError />
      ),
    children: [
      {
        path: "dashboard",
        element: <CRMDashboard />,
      },
      {
        path: "salesfunnel",
        element: <SalesFunnel />,
      },
      {
        path: "customer",
        element: <CRMCustomer />,
      },
      {
        path: "calender",
        element: <CRMCalendar />,
      },
      {
        path: "opportunity",
        element: <CRMOpportunities />,
      },
      {
        path: "opportunity/:id",
        element: <CRMOpportunityForm />,
      },
      {
        path: "customer/:id",
        element: <CRMCustomerForm />,
      },
      {
        path: "company",
        element: <CRMCompany />,
      },
    ],
  },
  {
    path: "/erp/*",
    element: ["admin", "manager"].includes(role) ? (
      <BaseLayout menuConfig={erpConfig} appListConfig={filteredAppList} />
    ) : (
      <FunnyError />
    ),
    children: [
      { path: "dashboard", element: <ERPDashboard /> },
      {
        path: "inventory/products",
        element: <ERPProducts />,
      },
      {
        path: "inventory/purchaseorders",
        element: <ERPPurchaseOrders />,
      },
      {
        path: "inventory/stocklevels",
        element: <ERPStockLevels />,
      },
      {
        path: "sales/orders",
        element: <ERPOrders />,
      },
      {
        path: "sales/customers",
        element: <ERPCustomers />,
      },
      {
        path: "sales/orders/:id",
        element: <ERPOrderForm />,
      },
      {
        path: "sales/orders/new",
        element: <ERPNewOrderForm />,
      },
    ],
  },
  {
    path: "/users/*",
    element:
      role === "admin" ? (
        <BaseLayout menuConfig={usersConfig} appListConfig={filteredAppList} />
      ) : (
        <FunnyError />
      ),
      children: [
        {
          path: "management",
          element: <UsersManage />,
        },
        {
          path: "logs",
          element: <UsersLogs />,
        },
        {
          path: "management/edit/:account",
          element: <UsersEdit />,
        },
        {
          path: "message",
          element: <UsersMessage />,
        },
        {
          path: "management/register",
          element: <UsersRegister />,
        },
        {
          path: "message/:messageId",
          element: <UserMessageChat />,
        },
      ],
  },
]);

export default router;
