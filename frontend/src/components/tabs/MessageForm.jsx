import { useEffect, useState, useRef } from 'react';
import axiosInstance from '../../api/axiosFrontend';
import dayjs from 'dayjs';

function MessagesForm({ messageId, onBack }) {
  const [replies, setReplies] = useState([]);
  const [input, setInput] = useState('');
  const messagesEndRef = useRef(null);

  const fetchReplies = async () => {
    try {
      const res = await axiosInstance.get(`/customer/message/${messageId}/replies`);
      setReplies(res.data);
      console.log('取得對話紀錄:', res.data);
    } catch (err) {
      console.error('取得對話紀錄失敗:', err);
    }
  };

  const sendReply = async () => {
  if (!input.trim()) return;

  try {
    await axiosInstance.post(
      `/customer/message/${messageId}/reply/customer`, 
      {
        content: input.trim(), 
      }
    );
    setInput('');
    fetchReplies();
  } catch (err) {
    console.error('發送訊息失敗:', err);
  }
};


  useEffect(() => {
    fetchReplies();
  }, [messageId]);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [replies]);

  return (
    <div className="space-y-4 text-sm">
      <div className="flex justify-between items-center">
        <h2 className="text-base font-bold text-gray-800">客服訊息</h2>
        <button
          onClick={onBack}
          className="text-blue-500 hover:underline text-sm"
        >
          ← 返回列表
        </button>
      </div>

      {/* 對話列表 */}
      <div className="h-[400px] overflow-y-auto border border-gray-200 rounded p-4 bg-white space-y-3">
        {replies.map((reply) => (
          <div
            key={reply.replyId}
            className={`flex ${
              reply.senderType === 'CUSTOMER' ? 'justify-end' : 'justify-start'
            }`}
          >
            <div
              className={`max-w-[70%] p-3 rounded-lg space-y-1 ${
                reply.senderType === 'CUSTOMER'
                  ? 'bg-blue-100 text-right'
                  : 'bg-gray-100 text-left'
              }`}
            >
              <p className="text-xs text-gray-500">
                {reply.senderName} · {dayjs(reply.sentAt).format('YYYY-MM-DD HH:mm')}
              </p>
              <p className="text-gray-800">{reply.content}</p>
            </div>
          </div>
        ))}
        <div ref={messagesEndRef} />
      </div>

      {/* 發送訊息 */}
      <div className="flex flex-col md:flex-row items-start md:items-end gap-2">
        <input
          type="text"
          value={input}
          onChange={(e) => setInput(e.target.value)}
          placeholder="輸入訊息..."
          className="flex-1 border border-gray-300 rounded px-3 py-2 w-full"
        />
        <button
          onClick={sendReply}
          className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
        >
          傳送
        </button>
      </div>
    </div>
  );
}

export default MessagesForm;
