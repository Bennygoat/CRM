import  { newsHandlers }  from './modules/news';
import { contactHandler } from './modules/contact'; 
import { productHandlers } from './modules/product';
import { loginHandlers } from './modules/login';
import { crmCustomerHandlers } from './modules/crmCustomer';
import { crmOpportunitiesHandlers } from './modules/crmOpportunities';
import { crmcalendarEventsHandler } from './modules/crmcalendarEventsHandler';
import { erpCustomersHandler } from './modules/erpCustomer';
import { erpOrdersHandler } from './modules/erpOrders';
import { erpReturnsHandler } from './modules/erpReturns';
import { erpProductsHandler } from './modules/erpProducts';
import { erpStockLevelsHandler } from './modules/erpStockLevels';
import { erpPurchaseOrdersHandler } from './modules/erpPurchaseOrders';
import { usersManageHandler } from './modules/usersManage';
import { userLogsHandler } from './modules/userLogs';
import { backLoginHandlers } from './modules/backLogin';
import { cmsproductdetailhandlers } from './modules/cmsproductdetail';
import { usersMessagehandlers } from './modules/usersmessage';

export const handlers = [
  ...newsHandlers,
  ...contactHandler,
  ...productHandlers,
  // ...loginHandlers,
  ...crmCustomerHandlers,
  ...crmOpportunitiesHandlers,
  ...crmcalendarEventsHandler,
  ...erpCustomersHandler,
  ...erpOrdersHandler,
  ...erpReturnsHandler,
  ...erpProductsHandler,
  ...erpStockLevelsHandler,
  ...erpPurchaseOrdersHandler,
  ...usersManageHandler,
  ...userLogsHandler,
  // ...backLoginHandlers,
  ...cmsproductdetailhandlers,
  ...usersMessagehandlers,

];