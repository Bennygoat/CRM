// components/BackToTop.jsx
import { useEffect, useState } from "react";
import { FaArrowUp } from "react-icons/fa";

function BackToTop() {
  const [show, setShow] = useState(false);

  useEffect(() => {
    const onScroll = () => {
      setShow(window.scrollY > 500);
    };
    window.addEventListener("scroll", onScroll);
    return () => window.removeEventListener("scroll", onScroll);
  }, []);

  const handleClick = () => {
    window.scrollTo({ top: 0, behavior: "smooth" });
  };

  return (
    show && (
      <button
        onClick={handleClick}
        className="fixed bottom-6 right-6 z-50 bg-white shadow-md hover:shadow-lg text-gray-700 rounded-full w-12 h-12 flex items-center justify-center transition-all duration-300 hover:bg-gray-100"
        aria-label="Back to top"
      >
        <FaArrowUp size={20} />
      </button>
    )
  );
}

export default BackToTop;
