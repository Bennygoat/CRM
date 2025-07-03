import React, { useEffect, useState } from 'react';
import { Button, Input, message } from 'antd';
import { ArrowLeftOutlined } from '@ant-design/icons';
import { useNavigate, useParams } from 'react-router-dom';
import dayjs from 'dayjs';
import clsx from 'clsx';

const mockConversation = [
  {
    id: 1,
    sender: 'customer',
    content: '你好，我忘記密碼了，該怎麼辦？',
    timestamp: dayjs().subtract(2, 'day').toISOString(),
  },
  {
    id: 2,
    sender: 'agent',
    content: '您好，請點選登入頁的「忘記密碼」連結進行重設。',
    timestamp: dayjs().subtract(2, 'day').add(1, 'hour').toISOString(),
  },
  {
    id: 3,
    sender: 'customer',
    content: '我已經重設完成了，謝謝！',
    timestamp: dayjs().subtract(1, 'day').toISOString(),
  },
];

const MessageChat = () => {
  const [messages, setMessages] = useState([]);
  const [inputValue, setInputValue] = useState('');
  const navigate = useNavigate();
  const { messageId } = useParams();

  useEffect(() => {
    setMessages(mockConversation);
  }, [messageId]);

  const handleSend = () => {
    if (!inputValue.trim()) return;
    const newMessage = {
      id: Date.now(),
      sender: 'customer',
      content: inputValue,
      timestamp: new Date().toISOString(),
    };
    setMessages((prev) => [...prev, newMessage]);
    setInputValue('');
    message.success('訊息已送出');
  };

  return (
    <div className="max-w-8xl mx-auto px-6 py-6">
      <Button
        icon={<ArrowLeftOutlined />}
        onClick={() => navigate(-1)}
        className="mb-4"
      >
        返回訊息列表
      </Button>

      <h2 className="text-xl font-semibold mb-4">與客服對話</h2>

      <div className="border p-6 rounded-lg h-[600px] overflow-y-auto bg-gray-50 mb-6 shadow-sm">
        {messages.map((msg) => (
          <div
            key={msg.id}
            className={clsx(
              'mb-3 flex',
              msg.sender === 'customer' ? 'justify-end' : 'justify-start'
            )}
          >
            <div
              className={clsx(
                'px-4 py-2 rounded-lg max-w-[70%] text-sm break-words',
                msg.sender === 'customer'
                  ? 'bg-blue-500 text-white'
                  : 'bg-gray-200 text-black'
              )}
            >
              <div>{msg.content}</div>
              <div
                className={clsx(
                  'text-[10px] text-right mt-1',
                  msg.sender === 'customer' ? 'text-white/80' : 'text-gray-500'
                )}
              >
                {dayjs(msg.timestamp).format('YYYY-MM-DD HH:mm')}
              </div>
            </div>
          </div>
        ))}
      </div>

      <div className="flex gap-2">
        <Input.TextArea
          className="flex-1"
          value={inputValue}
          onChange={(e) => setInputValue(e.target.value)}
          autoSize={{ minRows: 2, maxRows: 4 }}
          placeholder="輸入訊息..."
        />
        <Button type="primary" onClick={handleSend}>
          送出
        </Button>
      </div>
    </div>
  );
};

export default MessageChat;
