import React, { useEffect, useState, useRef } from 'react';
import { Button, Input, message as antdMessage } from 'antd';
import { ArrowLeftOutlined } from '@ant-design/icons';
import { useNavigate, useParams } from 'react-router-dom';
import axiosInstance from '../../api/axiosBackend';
import clsx from 'clsx';

const MessageChat = () => {
  const [messages, setMessages] = useState([]);
  const [inputValue, setInputValue] = useState('');
  const navigate = useNavigate();
  const { messageId } = useParams();
  const chatEndRef = useRef(null);

  const fetchMessages = async () => {
    try {
      const res = await axiosInstance.get(`/customer/message/${messageId}/replies`);
      console.log('取得對話成功:', res.data);
      const formatted = res.data.map((reply) => ({
        id: reply.replyId,
        sender: reply.senderType === 'CUSTOMER' ? 'user' : 'agent',
        senderName: reply.senderName,
        content: reply.content,
      }));
      setMessages(formatted);
      scrollToBottom();
    } catch (err) {
      console.error('取得對話失敗:', err);
      antdMessage.error('無法取得對話資料');
    }
  };

  const scrollToBottom = () => {
    setTimeout(() => {
      chatEndRef.current?.scrollIntoView({ behavior: 'smooth' });
    }, 100);
  };

  useEffect(() => {
    fetchMessages();
  }, [messageId]);

  const handleSend = async () => {
    if (!inputValue.trim()) return;

    try {
      await axiosInstance.post(
        `/customer/message/${messageId}/reply/user`,
        { content: inputValue.trim() }
      );
      antdMessage.success('已發送給客戶');
      setInputValue('');
      fetchMessages();
    } catch (err) {
      console.error('發送訊息失敗:', err);
      antdMessage.error('發送失敗');
    }
  };

  return (
    <div className="w-full px-8 py-6">
      
      {/* 聊天區 */}
      <div className="border rounded-lg p-6 h-[70vh] overflow-y-auto bg-white shadow">
        {messages.map((msg) => (
          <div
            key={msg.id}
            className={clsx(
              'mb-4 flex',
              msg.sender === 'agent' ? 'justify-end' : 'justify-start'
            )}
          >
            <div
              className={clsx(
                'px-4 py-3 rounded-2xl max-w-[60%] text-sm break-words shadow',
                msg.sender === 'agent'
                  ? 'bg-blue-500 text-white'
                  : 'bg-gray-100 text-gray-800'
              )}
            >
              <div className="font-medium mb-1">
                {msg.sender === 'agent' ? '客服' : msg.senderName}
              </div>
              <div>{msg.content}</div>
            </div>
          </div>
        ))}
        <div ref={chatEndRef} />
      </div>

      {/* 輸入區 */}
      <div className="flex gap-3 mt-4">
        <Input.TextArea
          className="flex-1"
          value={inputValue}
          onChange={(e) => setInputValue(e.target.value)}
          autoSize={{ minRows: 2, maxRows: 4 }}
          placeholder="輸入給客戶的回覆..."
        />
        <Button type="primary" onClick={handleSend}>
          發送
        </Button>
      </div>
    </div>
  );
};

export default MessageChat;
