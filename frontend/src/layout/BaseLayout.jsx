import {
  CaretDownFilled,
  DoubleRightOutlined,
  GithubFilled,
  InfoCircleFilled,
  LogoutOutlined,
  PlusCircleFilled,
  QuestionCircleFilled,
  SearchOutlined,
} from "@ant-design/icons";
import {
  PageContainer,
  ProCard,
  ProConfigProvider,
  ProLayout,
} from "@ant-design/pro-components";
import { css } from "@emotion/css";
import { Button, ConfigProvider, Dropdown } from "antd";
import React, { useState } from "react";
import logo from "../assets/prologo.png";
import { Outlet, useNavigate, useLocation } from "react-router-dom";
import useBackUserStore from "../stores/useBackUserStore";

export default function BaseLayout({ menuConfig, appListConfig }) {
  const navigate = useNavigate();
  const location = useLocation();
  const { logoutBackUser } = useBackUserStore();
  const pathname = location.pathname;
  const [settings, setSetting] = useState({
    fixSiderbar: true,
    layout: "side",
  });

  // const [pathname, setPathname] = useState('/list/sub-page/sub-sub-page1');
  const [num, setNum] = useState(40);

  if (typeof document === "undefined") return <div />;

  return (
    <div id="test-pro-layout" style={{ height: "100vh", overflow: "auto" }}>
      <ProConfigProvider hashed={false}>
        <ConfigProvider
          getTargetContainer={() =>
            document.getElementById("test-pro-layout") || document.body
          }
        >
          <ProLayout
            logo={logo}
            title="後台系統"
            prefixCls="my-prefix"
            bgLayoutImgList={[
              {
                src: "https://img.alicdn.com/imgextra/i2/O1CN01O4etvp1DvpFLKfuWq_!!6000000000279-2-tps-609-606.png",
                left: 85,
                bottom: 100,
                height: "303px",
              },
              {
                src: "https://img.alicdn.com/imgextra/i2/O1CN01O4etvp1DvpFLKfuWq_!!6000000000279-2-tps-609-606.png",
                bottom: -68,
                right: -45,
                height: "303px",
              },
              {
                src: "https://img.alicdn.com/imgextra/i3/O1CN018NxReL1shX85Yz6Cx_!!6000000005798-2-tps-884-496.png",
                bottom: 0,
                left: 0,
                width: "331px",
              },
            ]}
            {...menuConfig} // 引入多個路由配置
            appList={appListConfig}
            location={{ pathname }}
            token={{
              header: {
                colorBgMenuItemSelected: "rgba(0,0,0,0.04)",
              },
            }}
            siderMenuType="sub"
            menu={{ collapsedShowGroupTitle: true }}
            avatarProps={{
              // https://cdn-icons-png.flaticon.com/512/5069/5069643.png
              // https://cdn-icons-png.flaticon.com/512/126/126472.png
              src: "https://cdn-icons-png.flaticon.com/512/126/126472.png",
              size: "small",
              title: "系統設定",
              render: (props, dom) => (
                <Dropdown
                  menu={{
                    items: [
                      {
                        key: "logout",
                        icon: <LogoutOutlined />,
                        label: "登出",
                        onClick: () => {
                          logoutBackUser();
                          navigate("/login");
                        },
                      },
                    ],
                  }}
                >
                  {dom}
                </Dropdown>
              ),
            }}
            actionsRender={(props) => {
              if (props.isMobile) return [];
              if (typeof window === "undefined") return [];
              return [
                <InfoCircleFilled key="info" />,
                <QuestionCircleFilled key="question" />,
                <GithubFilled key="github" />,
              ];
            }}
            menuFooterRender={(props) =>
              props?.collapsed ? undefined : (
                <div style={{ textAlign: "center", paddingTop: 0 }}>
                  {/* <div>2025 © 哈根良野有限公司</div> */}
                  
                </div>
              )
            }
            // onMenuHeaderClick={(e) => console.log(e)}
            menuItemRender={(item, dom) => (
              <div onClick={() => item.path && navigate(item.path)}>{dom}</div>
            )}
            {...settings}
          >
            <PageContainer token={{ paddingInlinePageContainerContent: num }}>
              <ProCard>
                <Outlet />
              </ProCard>
            </PageContainer>
          </ProLayout>
        </ConfigProvider>
      </ProConfigProvider>
    </div>
  );
}
