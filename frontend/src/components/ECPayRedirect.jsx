import React, { useEffect, useRef } from "react";
import { useLocation, useNavigate } from "react-router-dom";

const ECPayRedirect = () => {
  const formRef = useRef(null);
  const { state } = useLocation();
  const navigate = useNavigate();

  useEffect(() => {
    if (!state || !state.aioCheckoutDto || !state.ecpayUrl) {
      alert("缺少金流參數，無法導向");
      navigate("/order");
    } else {
      formRef.current?.submit();
    }
  }, [state, navigate]);

  if (!state) return null;

  const { ecpayUrl, aioCheckoutDto } = state;

  return (
    <div>
      <h2>正在導向至綠界金流，請稍候...</h2>
      <form ref={formRef} method="POST" action={ecpayUrl}>
        {Object.entries(aioCheckoutDto).map(([key, value]) => (
          <input key={key} type="hidden" name={key} value={value} />
        ))}
      </form>
    </div>
  );
};

export default ECPayRedirect;
