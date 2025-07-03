import React, { useState, useEffect, useRef } from 'react';

function Messages() {
  const [input, setInput] = useState('');
  const [messages, setMessages] = useState([
    { type: 'bot', text: '您好，請問需要什麼協助？' },
  ]);
  const messagesEndRef = useRef(null);

  const handleSend = () => {
    if (!input.trim()) return;

    const newMessage = { type: 'user', text: input.trim() };
    setMessages((prev) => [...prev, newMessage]);

    simulateBotReply(input.trim());
    setInput('');
  };

  const simulateBotReply = (userText) => {
    setTimeout(() => {
      const reply = getBotReply(userText);
      setMessages((prev) => [...prev, { type: 'bot', text: reply }]);
    }, 1000);
  };

  const getBotReply = (text) => {
    const lower = text.toLowerCase();
    if (lower.includes('忘記') && lower.includes('密碼'))
      return '您可以透過登入頁的「忘記密碼」連結進行密碼重設。';
    if (lower.includes('訂單'))
      return '請提供訂單編號，我們將盡快為您查詢。';
    if (lower.includes('退貨'))
      return '退貨流程請至「會員中心 > 訂單管理」中申請退貨。';
    if (lower.includes('發票'))
      return '發票將於出貨後三日內寄送至您註冊的電子信箱。';
    return '我們已收到您的訊息，稍後由客服人員回覆您。';
  };

  // 滾動到底部
  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  return (
    <div className="space-y-4 text-sm max-w-5xl mx-auto p-4">
      <h2 className="text-base font-bold text-gray-800">與客服訊息</h2>

      {/* 訊息列表 */}
      <div className="h-[400px] overflow-y-auto border border-gray-200 rounded p-4 bg-white space-y-3">
        {messages.map((msg, idx) => (
          <div
            key={idx}
            className={`flex ${
              msg.type === 'user' ? 'justify-end' : 'justify-start'
            }`}
          >
            <div
              className={`max-w-[70%] p-3 rounded-lg ${
                msg.type === 'user'
                  ? 'bg-blue-100 text-right'
                  : 'bg-gray-100 text-left'
              }`}
            >
              {msg.text && <p className="text-gray-800">{msg.text}</p>}
            </div>
          </div>
        ))}
        <div ref={messagesEndRef} />
      </div>

      {/* 輸入區 */}
      <div className="flex flex-col md:flex-row items-start md:items-end gap-2">
        <input
          type="text"
          value={input}
          onChange={(e) => setInput(e.target.value)}
          placeholder="輸入訊息..."
          className="flex-1 border border-gray-300 rounded px-3 py-2 w-full"
        />
        <button
          onClick={handleSend}
          className="px-4 py-2 bg-logo-blue text-white rounded hover:bg-sky-600"
        >
          傳送
        </button>
      </div>
    </div>
  );
}

export default Messages;
