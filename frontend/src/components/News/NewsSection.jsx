import { Link } from "react-router-dom";
import { motion } from "framer-motion";

function NewsSection({ imageSrc, date, title, id, direction = 'left', index = 0 }) {
  const duration = 1.0;
  const delayGap = 1.2; // 每張滑完後間隔 0.2 秒才滑下一張

  const variants = {
    hidden: {
      opacity: 0,
      x: direction === 'left' ? -100 : 100,
    },
    visible: {
      opacity: 1,
      x: 0,
      transition: {
        duration,
        ease: [0.25, 0.25, 0.75, 0.75], // 自訂平滑速度
        delay: index * delayGap,
      },
    },
  };

  return (
    <Link to={`/news/${id}`}>
      <motion.div
        className="flex flex-col md:flex-row items-center bg-white rounded-xl shadow-md overflow-hidden my-4 w-full max-w-4xl mx-auto transition hover:scale-[1.01] hover:shadow-lg"
        initial="hidden"
        whileInView="visible"
        viewport={{ once: true, amount: 0.3 }}
        variants={variants}
      >
        <div className="w-full md:w-[300px] h-[200px] flex-shrink-0">
          <img src={imageSrc} alt={title} className="w-full h-full object-cover" />
        </div>
        <div className="p-6 flex flex-col justify-center text-left w-full">
          <p className="text-sm text-gray-500">{date}</p>
          <h2 className="text-xl font-bold mt-1 leading-snug">{title}</h2>
        </div>
      </motion.div>
    </Link>
  );
}

export default NewsSection;
