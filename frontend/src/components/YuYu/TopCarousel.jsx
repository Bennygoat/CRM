export default function TopCarousel({ images }) {
  return (
    <div className="group relative overflow-hidden h-[220px] mb-6">
      {/* 左側遮罩 */}
      <div className="pointer-events-none absolute top-0 left-0 w-20 h-full bg-gradient-to-r from-white via-white/50 to-transparent z-10" />
      {/* 右側遮罩 */}
      <div className="pointer-events-none absolute top-0 right-0 w-20 h-full bg-gradient-to-l from-white via-white/50 to-transparent z-10" />

      <div className="flex w-max gap-6 flex-row-reverse animate-scroll-right group-hover:[animation-play-state:paused]">
        {[...images, ...images].map((src, i) => (
          <div
            key={`top-${i}`}
            className="w-[350px] h-[220px] rounded-xl overflow-hidden shrink-0 shadow-md"
          >
            <img
              src={src}
              alt={`top-img-${i}`}
              className="w-full h-full object-cover"
            />
          </div>
        ))}
      </div>
    </div>
  );
}
