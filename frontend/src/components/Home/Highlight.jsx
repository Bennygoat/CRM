import { FaTrophy, FaSmile } from "react-icons/fa";
import { Link } from "react-router-dom";
import CountUp from "react-countup";
import { useInView } from "react-intersection-observer";

const Highlight = () => {
  const { ref, inView } = useInView({
    triggerOnce: true,
    threshold: 0.3,
  });

  return (
    <section
      ref={ref}
      className={`
                 px-4 md:px-28 pb-20
                 transition-all duration-[1000ms] ease-in-out
                 ${inView ? "opacity-100 translate-y-0" : "opacity-0 translate-y-12"}
  `}
    >
      {/* ⭐ 卡片背景容器（藍白背景 + 陰影 + 圓角） */}
      <div
        className="to-white rounded-3xl shadow-xl p-8 md:p-12"
      >
        {/* 標題區塊 */}
        <div className="text-center mb-8">
          <h2 className="text-2xl md:text-3xl font-bold text-gray-900 flex justify-center items-center gap-2">
            <FaTrophy className="text-yellow-500" />
            植物奶 × 義式冰品的革新之路
            <FaTrophy className="text-yellow-500" />
          </h2>
          <p className="mt-4 text-gray-700 leading-relaxed max-w-3xl mx-auto">
            良野義式冰品專注純素與乳糖不耐友善產品，結合新鮮水果與植物奶，打造兼具營養、
            美味與社會責任的義式冰品。
            <br />
            每一口冰品都來自我們對氣候、環境與飲食永續的深刻回應——從夜市起家，到進軍主流通路，
            用甜點傳遞對未來的想像。
          </p>
          <Link to="/about">
            <button className="mt-6 bg-red-600 hover:bg-red-700 text-white font-semibold py-2 px-5 rounded-full inline-flex items-center">
              <FaSmile className="mr-2" />
              探索我們的冰品理念
            </button>
          </Link>
        </div>

        {/* 數字成就區塊 */}
        <div className="grid grid-cols-2 md:grid-cols-4 gap-8 text-center mt-10 text-gray-800">
          <div>
            <p className="text-3xl font-bold text-red-600">
              {inView && <CountUp end={2000000} duration={2} separator="," />}+
            </p>
            <p className="text-sm text-gray-600 mt-1">支植物系冰品銷售量</p>
          </div>
          <div>
            <p className="text-3xl font-bold text-red-600">
              {inView && <CountUp end={1000} duration={2} separator="," />}+
            </p>
            <p className="text-sm text-gray-600 mt-1">則顧客好評與推薦</p>
          </div>
          <div>
            <p className="text-3xl font-bold text-red-600">
              {inView && <CountUp end={50} duration={2} />}+
            </p>
            <p className="text-sm text-gray-600 mt-1">家媒體與新聞專題報導</p>
          </div>
          <div>
            <p className="text-3xl font-bold text-red-600">
              {inView && <CountUp end={10} duration={2} />}+
            </p>
            <p className="text-sm text-gray-600 mt-1">項品牌合作與永續通路</p>
          </div>
        </div>
      </div>
    </section>
  );
};

export default Highlight;
