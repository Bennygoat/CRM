import React from 'react';
import { Link } from 'react-router-dom';
import { useInView } from 'react-intersection-observer';

const cardData = [
  {
    title: '良野豆乳雪糕',
    description: '濃郁豆漿為基底，打造零負擔的雪糕新選擇。',
    image: '/images/062601.jpg',
    button: '前往購買',
    link: '/store'
  },
  {
    title: '豆乳 × 經典杯 的巧妙結合',
    description: '用一杯說豆的方式，品嚐全素的溫柔。',
    image: '/images/062602.jpg',
    button: '前往購買',
    link: '/store'
  },
  {
    title: '為什麼是豆乳？',
    description: '因為我們相信，植物的力量就足以撐起一場美味革命。',
    image: '/images/062603.jpg',
    button: '閱讀更多',
    link: '/About'
  }
];

const TripleCardSection = () => {
  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-8 px-6 sm:px-10 lg:px-28 pb-20">
      {cardData.map((card, index) => {
        const { ref, inView } = useInView({
          triggerOnce: true,
          threshold: 0.2,
        });

        return (
          <Link
            to={card.link}
            key={index}
            ref={ref}
            className={`
               relative group overflow-hidden rounded-2xl shadow-lg transform transition-all duration-700 ease-in-out
               ${inView ? 'opacity-100 translate-x-0' : 'opacity-0 -translate-x-8'}
               delay-[${index * 100}ms]
            `}
          >
            <img
              src={card.image}
              alt={card.title}
              className="object-cover w-full h-[300px] sm:h-[350px] md:h-[600px] group-hover:scale-105 transition-transform duration-500 ease-in-out"
              loading="lazy"
            />
            <div className="absolute top-[70%] left-0 right-0 bottom-0 bg-logo-lightBlue bg-opacity-70 group-hover:bg-opacity-30 transition duration-300" />
            <div className="absolute bottom-6 left-6 right-6 text-white z-10">
              <h3 className="text-xl md:text-2xl font-bold uppercase tracking-wide">
                {card.title}
              </h3>
              <p className="mt-1 text-sm md:text-base max-w-[100%] text-white font-bold text-shadow">
                {card.description}
              </p>
              <div className="mt-4">
                <span className="bg-white text-black px-5 py-2 rounded shadow hover:bg-gray-100 text-sm font-medium">
                  {card.button}
                </span>
              </div>
            </div>
          </Link>
        );
      })}
    </div>
  );
};

export default TripleCardSection;
