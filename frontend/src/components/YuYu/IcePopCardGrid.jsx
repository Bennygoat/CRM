import { motion } from "framer-motion";
import { useInView } from "react-intersection-observer";


export default function IcePopCardGrid() {
  const pops = [
    { name: "超越經典香草", img: "/images/062901.png", hoverImg: "/images/hover/062901hover.png", link: "/Store" },
    { name: "藍色狂想曲", img: "/images/062902.png", hoverImg: "/images/hover/062902hover.png", link: "/Store" },
    { name: "莓好生活", img: "/images/062903.png", hoverImg: "/images/hover/062903hover.png", link: "/Store" },
    { name: "濃醇花生", img: "/images/062904.png", hoverImg: "/images/hover/062904hover.png", link: "/Store" },
    { name: "百香果雪酪", img: "/images/062905.png", hoverImg: "/images/hover/062905hover.png", link: "/Store" },
    { name: "大花之吻", img: "/images/062906.png", hoverImg: "/images/hover/062906hover.png", link: "/Store" },
    { name: "法式焦糖佐脆餅", img: "/images/062907.png", hoverImg: "/images/hover/062907hover.png", link: "/Store" },
    { name: "特級日式抹茶", img: "/images/062908.png", hoverImg: "/images/hover/062908hover.png", link: "/Store" },
    { name: "頂級熱韻可可", img: "/images/062909.png", hoverImg: "/images/hover/062909hover.png", link: "/Store" },
    { name: "轉轉OREO", img: "/images/062910.png", hoverImg: "/images/hover/062910hover.png", link: "/Store" },
  ];

  const [ref, inView] = useInView({
    triggerOnce: true,
    threshold: 0.5,
  });

  return (
    <div className="max-w-6xl mx-auto pb-16 pb-20" ref={ref}>
      <h2 className="text-center text-2xl md:text-3xl font-bold tracking-wider mb-10 text-gray-800">
        熱門經典杯裝系列
      </h2>

      <div className="grid grid-cols-2 md:grid-cols-5 gap-6 place-items-center">
        {pops.map((pop, i) => (
          <motion.div
            key={i}
            className="group flex flex-col items-center space-y-4"
            initial={{ opacity: 0, y: 30 }}
            animate={inView ? { opacity: 1, y: 0 } : {}}
            transition={{ delay: i * 0.1, duration: 1.0 }}
          >
            <a href={pop.link}>
              <div className="relative h-[160px] w-auto">
                {/* 原圖 */}
                <img
                  src={pop.img}
                  alt={pop.name}
                  className="h-[160px] object-contain transition-opacity duration-300 group-hover:opacity-0"
                />
                {/* Hover 圖 */}
                <img
                  src={pop.hoverImg}
                  alt={`${pop.name}-hover`}
                  className="h-[160px] object-contain absolute top-0 left-0 transition-opacity duration-300 opacity-0 group-hover:opacity-100"
                />
              </div>
            </a>
            <span className="text-sm md:text-base font-semibold tracking-widest text-black group-hover:text-logo-blue">
              {pop.name}
            </span>
          </motion.div>
        ))}
      </div>
    </div>
  );
}