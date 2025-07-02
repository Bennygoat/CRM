// components/Store/ProductCardCard.jsx
function ProductCard({ title, price, imageSrc, description }) {
  return (
    <div className="flex w-full max-w-3xl bg-white rounded-lg shadow hover:shadow-md transition overflow-hidden mx-auto">
      {/* 圖片區塊 */}
      <div className="w-1/3 p-4">
        <img
          src={imageSrc}
          alt={title}
          className="w-full h-auto object-cover rounded"
        />
      </div>

      {/* 資訊區塊 */}
      <div className="w-2/3 p-4 flex flex-col justify-between">
        <div>
          <h3 className="font-bold text-base mb-1">{title}</h3>
          <p className="text-sm text-gray-600 line-clamp-3">{description}</p>
        </div>
        <div className="mt-3 flex items-center justify-between">
          <span className="text-gray font-bold">{price}</span>
          <button className="bg-logo-lightBlue text-white px-3 py-1 rounded text-sm font-semibold">
            +
          </button>
        </div>
      </div>
    </div>
  );
}

export default ProductCard;
