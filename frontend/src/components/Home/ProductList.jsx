import { useRef, useState, useEffect } from "react";
import ProductCard from "./ProductCard";
import { FaChevronLeft, FaChevronRight } from "react-icons/fa";

function ProductList({ products }) {
  const scrollRef = useRef();
  const [canScrollLeft, setCanScrollLeft] = useState(false);
  const [canScrollRight, setCanScrollRight] = useState(false);

  const checkScroll = () => {
    const el = scrollRef.current;
    if (!el) return;
    setCanScrollLeft(el.scrollLeft > 0);
    setCanScrollRight(el.scrollLeft + el.clientWidth < el.scrollWidth - 1);
  };

  const scroll = (offset) => {
    if (scrollRef.current) {
      scrollRef.current.scrollBy({ left: offset, behavior: "smooth" });
    }
  };

  useEffect(() => {
    const el = scrollRef.current;
    if (!el) return;

    checkScroll();
    el.addEventListener("scroll", checkScroll);
    window.addEventListener("resize", checkScroll);

    return () => {
      el.removeEventListener("scroll", checkScroll);
      window.removeEventListener("resize", checkScroll);
    };
  }, []);

  console.log("資料筆數：", products.length);
  console.log("實際資料列表：", products);

  return (
    <section className="py-10 px-4 md:px-28 text-left relative">
      {/* 標題區 */}
      <div className="flex flex-col md:flex-row md:items-end space-y-1 md:space-y-0 md:space-x-1 mb-6">
        <span className="text-2xl font-semibold text-gray-800">輕包裝水果冰棒。</span>
        <span className="text-2xl font-semibold text-gray-500">讓你吃的無負擔。</span>
      </div>

      {/* 輪播主區域 */}
      <div className="relative">
        {/* 左箭頭 */}
        {canScrollLeft && (
          <button
            onClick={() => scroll(-400)}
            className="absolute -left-5 top-1/2 -translate-y-1/2 z-10 bg-white/70 hover:bg-white text-gray-700 rounded-full shadow w-9 h-9 flex items-center justify-center"
          >
            <FaChevronLeft size={16} />
          </button>
        )}

        {/* 滾動卡片區 */}
        <div
          ref={scrollRef}
          className="flex gap-0 overflow-x-auto overflow-y-hidden scrollbar-hide scroll-smooth snap-x snap-mandatory pb-6 no-scrollbar"
        >
          {products.map((product) => (
            <div key={product.id} className="snap-start shrink-0 w-[250px]">
              <ProductCard product={product} />
            </div>
          ))}
        </div>

        {/* 右箭頭 */}
        {canScrollRight && (
          <button
            onClick={() => scroll(400)}
            className="absolute -right-5 top-1/2 -translate-y-1/2 z-10 bg-white/70 hover:bg-white text-gray-700 rounded-full shadow w-9 h-9 flex items-center justify-center"
          >
            <FaChevronRight size={16} />
          </button>
        )}
      </div>
    </section>
  );
}

export default ProductList;
