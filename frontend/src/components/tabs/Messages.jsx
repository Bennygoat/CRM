import React, { useState } from 'react';

function Messages() {
  const [input, setInput] = useState('');
  const [messages, setMessages] = useState([
    { type: 'bot', text: '您好，請問需要什麼協助？' },
  ]);

  const handleSend = () => {
    if (!input) return;

    const newMessage = {
      type: 'user',
      text: input,
    };

    setMessages((prev) => [...prev, newMessage]);
    setInput('');
  };

  return (
    <div className="space-y-4 text-sm">
      <h2 className="text-base font-bold text-gray-800">與客服訊息</h2>

      {/* 訊息列表 */}
      <div className="h-100 overflow-y-auto border border-gray-200 rounded p-4 bg-white space-y-3">
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
          className="px-4 py-2 bg-orange-500 text-white rounded hover:bg-orange-600"
        >
          傳送
        </button>
      </div>
    </div>
  );
}

export default Messages;
