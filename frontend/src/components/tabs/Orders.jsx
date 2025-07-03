import { useEffect, useState } from 'react';
import axiosInstance from '../../api/axiosFrontend';
import dayjs from 'dayjs';

function Orders() {
  const [orders, setOrders] = useState([]);
  const [selectedOrder, setSelectedOrder] = useState(null);

  useEffect(() => {
    const fetchOrders = async () => {
      try {
        const res = await axiosInstance.get('/orders/my-orders');
        console.log('取得訂單成功:', res.data);
        setOrders(res.data);
      } catch (err) {
        console.error('取得訂單失敗:', err);
      }
    };

    fetchOrders();
  }, []);

  const convertOrderStatus = (status) => {
    switch (status) {
      case 'PENDING_PAYMENT':
        return '待付款';
      case 'PENDING_SHIPMENT':
        return '待出貨';
      case 'SHIPPED':
        return '已出貨';
      case 'COMPLETED':
        return '已完成';
      case 'CANCELLED':
        return '已取消';
      default:
        return status;
    }
  };

  const convertPaymentMethod = (method) => {
    switch (method) {
      case 'CASH_ON_DELIVERY':
        return '貨到付款';
      case 'ONLINE_PAYMENT':
        return '線上支付';
      default:
        return method;
    }
  };

  const convertPaymentStatus = (status) => {
    switch (status) {
      case 'PAID':
        return '已付款';
      case 'UNPAID':
        return '未付款';
      default:
        return status;
    }
  };

  return (
    <div className="text-sm space-y-6">
      <h2 className="text-base font-bold text-gray-800">訂單紀錄</h2>

      {/* 表格 */}
      <div className="overflow-x-auto">
        <table className="w-full table-auto border-t border-gray-300 text-left">
          <thead className="text-gray-600 border-b border-gray-200">
            <tr>
              <th className="py-2 px-4 font-semibold">訂單號碼</th>
              <th className="py-2 px-4 font-semibold">訂單日期</th>
              <th className="py-2 px-4 font-semibold">合計</th>
              <th className="py-2 px-4 font-semibold">訂單狀態</th>
              <th className="py-2 px-4 font-semibold">操作</th>
            </tr>
          </thead>
          <tbody>
            {orders.map((order) => (
              <tr key={order.orderid} className="border-b border-gray-100">
                <td className="py-3 px-4 text-gray-800">{order.orderid}</td>
                <td className="py-3 px-4 text-gray-700">{order.orderdate}</td>
                <td className="py-3 px-4 text-gray-700">NT${order.totalAmount.toLocaleString()}</td>
                <td className="py-3 px-4 text-gray-700">
                  {convertOrderStatus(order.orderStatus)}
                </td>
                <td className="py-3 px-4">
                  <button
                    onClick={() => setSelectedOrder(order)}
                    className="bg-gray-500 text-white px-4 py-1 rounded hover:bg-gray-600"
                  >
                    查閱
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <p className="text-xs text-gray-500 text-right mt-2">僅顯示 2 年內訂單</p>

      {/* Modal 彈出視窗 */}
      {selectedOrder && (
        <div className="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-50">
          <div className="bg-white p-6 rounded-md max-w-lg w-full shadow-lg">
            <h3 className="text-lg font-bold mb-4">訂單明細 #{selectedOrder.orderid}</h3>
            <p className="text-sm mb-2 text-gray-700">訂單日期：{selectedOrder.orderdate}</p>
            <p className="text-sm mb-2 text-gray-700">
              付款方式：{convertPaymentMethod(selectedOrder.paymentMethod)}
            </p>
            <p className="text-sm mb-2 text-gray-700">
              付款狀態：{convertPaymentStatus(selectedOrder.paymentStatus)}
            </p>

            <div className="mt-4 border-t pt-4">
              <h4 className="font-semibold mb-2">商品列表：</h4>
              <ul className="space-y-2">
                {selectedOrder.orderDetails.map((item, idx) => (
                  <li key={idx} className="text-sm text-gray-800">
                    {item.productName} × {item.quantity}（單價 NT${item.unitPrice}）
                  </li>
                ))}
              </ul>
            </div>

            <div className="mt-6 text-right">
              <button
                onClick={() => setSelectedOrder(null)}
                className="px-4 py-1 bg-gray-200 hover:bg-gray-300 rounded"
              >
                關閉
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default Orders;
