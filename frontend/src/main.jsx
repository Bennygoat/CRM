// import { StrictMode } from "react";
// import { createRoot } from "react-dom/client";
// import { createBrowserRouter, RouterProvider } from "react-router-dom";
// import "./index.css";
// import router from "./Router.jsx";
//
//
// if (import.meta.env.MODE === 'development' || import.meta.env.VITE_USE_MOCK === 'true') {
//   const { worker } = await import('./mocks/browser');
//   await worker.start({
//     onUnhandledRequest: 'bypass',
//   });
// }
//
//
//
// createRoot(document.getElementById("root")).render(
//   <StrictMode>
//     <RouterProvider router={router} />
//   </StrictMode>
// );

import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import { RouterProvider } from "react-router-dom";
import { App as AntdApp, ConfigProvider } from "antd";
import zhTW from 'antd/locale/zh_TW';
import dayjs from 'dayjs';
import 'dayjs/locale/zh-tw';
import "./index.css";
import router from "./Router.jsx";

dayjs.locale('zh-tw');

const root = createRoot(document.getElementById("root"));

root.render(
  <StrictMode>
    <ConfigProvider locale={zhTW}>
      <AntdApp>
        <RouterProvider router={router} />
      </AntdApp>
    </ConfigProvider>
  </StrictMode>
);
