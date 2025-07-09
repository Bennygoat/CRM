import { useEffect, useState } from 'react';
import axiosInstance from '../../api/axiosFrontend';
import dayjs from 'dayjs';
import MessagesForm from './MessageForm';

function Messages() {
  const [messages, setMessages] = useState([]);
  const [selectedMessageId, setSelectedMessageId] = useState(null);
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [newTitle, setNewTitle] = useState('');
  const [newContent, setNewContent] = useState('');

  const fetchMessages = async () => {
    try {
      const res = await axiosInstance.get('/customer/message/list');
      console.log('取得客服訊息成功:', res.data);
      setMessages(res.data);
    } catch (err) {
      console.error('取得客服訊息失敗:', err);
    }
  };

  useEffect(() => {
    fetchMessages();
  }, []);

  const convertResolvedStatus = (isResolved) => {
    return isResolved ? '已解決' : '未解決';
  };

  const handleCreateMessage = async () => {
    if (!newTitle.trim() || !newContent.trim()) {
      alert('請輸入標題和內容');
      return;
    }

    try {
      const res = await axiosInstance.post('/customer/message/create', {
        questionTitle: newTitle.trim(),
        content: newContent.trim(),
      });
      console.log('新增問題成功:', res.data);
      setShowCreateModal(false);
      setNewTitle('');
      setNewContent('');

      // 自動切換到剛新增的對話
      const newMessageId = res.data.messageId;
      if (newMessageId) {
        setSelectedMessageId(newMessageId);
      } else {
        fetchMessages();
      }
    } catch (err) {
      console.error('新增問題失敗:', err);
    }
  };

  const handleBackToList = () => {
    setSelectedMessageId(null);
    fetchMessages();
  };

  if (selectedMessageId) {
    return (
      <MessagesForm
        messageId={selectedMessageId}
        onBack={handleBackToList}
      />
    );
  }

  return (
    <div className="text-sm space-y-6">
      <div className="flex justify-between items-center">
        <h2 className="text-base font-bold text-gray-800">客服訊息紀錄</h2>
        <button
          onClick={() => setShowCreateModal(true)}
          className="bg-green-500 text-white px-3 py-1 rounded hover:bg-green-600 text-sm"
        >
          新增問題
        </button>
      </div>

      {/* 訊息列表 */}
      <div className="overflow-x-auto">
        <table className="w-full table-auto border-t border-gray-300 text-left">
          <thead className="text-gray-600 border-b border-gray-200">
            <tr>
              <th className="py-2 px-4 font-semibold w-1/5">問題標題</th>
              <th className="py-2 px-4 font-semibold w-1/5">建立時間</th>
              <th className="py-2 px-4 font-semibold w-1/5">最後回覆</th>
              <th className="py-2 px-4 font-semibold w-1/5">狀態</th>
              <th className="py-2 px-4 font-semibold w-1/5">操作</th>
            </tr>
          </thead>
          <tbody>
            {messages.map((msg) => (
              <tr key={msg.messageId} className="border-b border-gray-100">
                <td className="py-3 px-4 text-gray-800 w-1/5">{msg.questionTitle}</td>
                <td className="py-3 px-4 text-gray-700 w-1/5">
                  {dayjs(msg.createdAt).format('YYYY-MM-DD HH:mm')}
                </td>
                <td className="py-3 px-4 text-gray-700 w-1/5">{msg.lastReplyContent}</td>
                <td className="py-3 px-4 text-gray-700 w-1/5">
                  {convertResolvedStatus(msg.isResolved)}
                </td>
                <td className="py-3 px-4 w-1/5">
                  <button
                    onClick={() => setSelectedMessageId(msg.messageId)}
                    className="bg-blue-500 text-white px-4 py-1 rounded hover:bg-blue-600"
                  >
                    查看對話
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* 新增問題 Modal */}
      {showCreateModal && (
        <div className="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-50">
          <div className="bg-white p-6 rounded-md w-full max-w-md shadow-lg space-y-4">
            <h3 className="text-lg font-bold">新增問題</h3>
            <div>
              <label className="block text-sm font-medium mb-1">問題標題</label>
              <input
                type="text"
                value={newTitle}
                onChange={(e) => setNewTitle(e.target.value)}
                placeholder="輸入問題標題"
                className="w-full border border-gray-300 rounded px-3 py-2"
              />
            </div>
            <div>
              <label className="block text-sm font-medium mb-1">內容</label>
              <textarea
                value={newContent}
                onChange={(e) => setNewContent(e.target.value)}
                placeholder="描述您的問題..."
                className="w-full border border-gray-300 rounded px-3 py-2"
                rows={3}
              ></textarea>
            </div>
            <div className="flex justify-end gap-2">
              <button
                onClick={() => setShowCreateModal(false)}
                className="px-3 py-1 bg-gray-200 rounded hover:bg-gray-300 text-sm"
              >
                取消
              </button>
              <button
                onClick={handleCreateMessage}
                className="px-3 py-1 bg-blue-500 text-white rounded hover:bg-blue-600 text-sm"
              >
                送出
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default Messages;
