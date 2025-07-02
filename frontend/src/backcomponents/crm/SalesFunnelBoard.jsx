// SalesFunnelBoard.jsx
import React, { useState } from "react";
import axios from "../../api/axiosBackend";
import {
  DndContext,
  closestCenter,
  PointerSensor,
  useSensor,
  useSensors,
  useDroppable,
  DragOverlay,
} from "@dnd-kit/core";
import {
  arrayMove,
  SortableContext,
  rectSortingStrategy,
  useSortable,
} from "@dnd-kit/sortable";
import { CSS } from "@dnd-kit/utilities";
import { FaStar, FaRegStar, FaClock, FaEdit } from "react-icons/fa";

const visibleStages = ["INITIAL_CONTACT", "PROPOSAL", "NEGOTIATION", "CLOSED_WON"];
const columnTitles = {
  INITIAL_CONTACT: "初步接洽",
  PROPOSAL: "提案",
  NEGOTIATION: "談判",
  CLOSED_WON: "成交"
};

export default function SalesFunnelBoard({ columns, setColumns, onCardDoubleClick,onContractGeneratedv}) {

  const [overColumnId, setOverColumnId] = useState(null);
  const [activeCard, setActiveCard] = useState(null);
  const [activeId, setActiveId] = useState(null);

  const sensors = useSensors(
    useSensor(PointerSensor, { activationConstraint: { distance: 20 } })
  );

  const handleDragStart = ({ active }) => {
    const id = active.id;
    setActiveId(id);
    for (const col in columns) {
      const item = columns[col].find((i) => i.id === id);
      if (item) {
        setActiveCard(item);
        break;
      }
    }
  };

  const handleDragOver = ({ active, over }) => {
    if (!over) return;
    const overId = over.id;

    const isOverColumn = Object.keys(columns).includes(overId);
    const targetColumn = isOverColumn
      ? overId
      : Object.keys(columns).find((key) =>
          columns[key].some((item) => item.id === overId)
        );

    if (targetColumn) {
      setOverColumnId(targetColumn);
    }
  };

  const handleDragEnd = ({ active, over }) => {
    setActiveCard(null);
    setOverColumnId(null);
    setActiveId(null);
    setOverColumnId(null);
    if (!over) return;

    const activeId = active.id;
    const overId = over.id;

    const sourceColumn = Object.keys(columns).find((key) =>
      columns[key].some((item) => item.id === activeId)
    );
    const targetColumn = Object.keys(columns).includes(overId)
      ? overId
      : Object.keys(columns).find((key) =>
          columns[key].some((item) => item.id === overId)
        );

    if (!sourceColumn || !targetColumn) return;

    const activeItem = columns[sourceColumn].find((i) => i.id === activeId);

    if (sourceColumn === targetColumn) {
      const oldIndex = columns[sourceColumn].findIndex(
        (i) => i.id === activeId
      );
      const newIndex = columns[targetColumn].findIndex((i) => i.id === overId);
      if (oldIndex !== newIndex) {
        const newItems = arrayMove(columns[sourceColumn], oldIndex, newIndex);
        setColumns({ ...columns, [sourceColumn]: newItems });
      }
    } else {
      const newSource = columns[sourceColumn].filter((i) => i.id !== activeId);
      const newTarget = [...columns[targetColumn], activeItem];
      setColumns({
        ...columns,
        [sourceColumn]: newSource,
        [targetColumn]: newTarget,
      });

      if (targetColumn === "NEGOTIATION") {
        const opportunityId =
          activeItem?.opportunityId || activeItem?.id?.replace(/^c/, "");
        if (opportunityId) {
          axios
            .post("/contracts/generate", { opportunityId })
            .then((res) => {
              onContractGenerated?.(res.data);
            })
            .catch((error) => {
              console.error("❌ 合約產生失敗", error);
            });
        }
      }
    }
  };

  return (
    <DndContext
      sensors={sensors}
      collisionDetection={closestCenter}
      onDragStart={handleDragStart}
      onDragOver={handleDragOver}
      onDragEnd={handleDragEnd}
    >
      <div className="grid grid-cols-4 gap-4 min-h-screen">
        {Object.entries(columns || {})
          .filter(([key]) => visibleStages.includes(key))
          .map(([columnId, items]) => (
            <Column
              key={columnId}
              id={columnId}
              title={columnTitles[columnId]}
              items={items}
              isOver={overColumnId === columnId}
              activeId={activeId}
              onCardDoubleClick={onCardDoubleClick}
            />
          ))}
      </div>
      <DragOverlay dropAnimation={null}>
        {activeCard ? (
          <SortableCard
            id={activeCard.id}
            title={activeCard.title}
            rating={activeCard.rating}
            type={activeCard.type || "default"}
            isOverlay
          />
        ) : null}
      </DragOverlay>
    </DndContext>
  );
}

function Column({ id, title, items, isOver, activeId ,onCardDoubleClick}) {
  const { setNodeRef } = useDroppable({ id });
  return (
    <div
      className={`transition-colors rounded-xl min-h-[100px] ${
        isOver ? "bg-gray-200" : "bg-white"
      }`}
    >
      <h2 className="font-bold text-lg mb-2">{title}</h2>
      <SortableContext
        items={items.map((item) => item.id)}
        strategy={rectSortingStrategy}
      >
        <div ref={setNodeRef} className="flex flex-col gap-4">
          {items.map((item) => (
            <SortableCard
              key={item.id}
              id={item.id}
              title={item.title}
              rating={item.rating || 0}
              type={item.type || "default"}
            />
          ))}
        </div>
      </SortableContext>
    </div>
  );
}

function SortableCard({
  id,
  title,
  rating,
  type = "default",
  isOverlay = false,
  isPreview = false,
  onCardDoubleClick,
}) {
  const [currentRating, setCurrentRating] = useState(rating);
  const [showColorPicker, setShowColorPicker] = useState(false);
  const [selectedType, setSelectedType] = useState(type);

  const handleStarClick = (index) => {
    if (currentRating === 1 && index === 0) {
      setCurrentRating(0);
    } else {
      setCurrentRating(index + 1);
    }
  };

  const handleTypeChange = (newType) => {
    setSelectedType(newType);
    setShowColorPicker(false);
  };

  const {
    attributes,
    listeners,
    setNodeRef,
    transform,
    transition,
    isDragging,
  } = useSortable({ id });

  const style = {
    transform: CSS.Transform.toString(transform),
    transition,
    opacity: isDragging && !isOverlay ? 0.3 : 1,
    zIndex: isOverlay ? 999 : undefined,
  };

  const borderColorMap = {
    default: "border-gray-300",
    success: "border-green-400",
    warning: "border-yellow-400",
    error: "border-red-400",
    info: "border-blue-400",
  };
  const borderColor = borderColorMap[selectedType] || borderColorMap.default;

  return (
    <div
      ref={setNodeRef}
      {...attributes}
      {...listeners}
      style={style}
      onDoubleClick={onCardDoubleClick}
      className={`bg-white w-full px-3 py-4 border-2 ${borderColor} hover:shadow-md rounded-2xl relative cursor-pointer group`}
    >
      {/* 卡片右上角的 FaEdit 和顏色選單 */}
      <div className="absolute bottom-2 right-2">
        <FaEdit
          className="text-gray-500 hover:text-black transition duration-200 cursor-pointer"
          onClick={() => setShowColorPicker((prev) => !prev)}
        />
        {showColorPicker && (
          <div className="absolute right-0 mt-2 bg-white border rounded shadow-md z-50 p-2 space-y-1">
            {["success", "warning", "error", "info"].map((typeOption) => (
              <div
                key={typeOption}
                onClick={() => handleTypeChange(typeOption)}
                className="flex items-center gap-2 px-2 py-1 text-sm cursor-pointer rounded hover:bg-gray-100"
              >
                <span
                  className={`inline-block w-3 h-3 rounded-full ${
                    {
                      success: "bg-green-400",
                      warning: "bg-yellow-400",
                      error: "bg-red-400",
                      info: "bg-blue-400",
                      default: "bg-gray-400",
                    }[typeOption]
                  }`}
                ></span>
                {typeOption}
              </div>
            ))}
          </div>
        )}
      </div>

      {/* 標題 */}
      <div className="font-semibold mb-2">{title}</div>

      {/* 星星評分 */}
      <div className="flex items-center text-sm text-gray-600">
        {[...Array(3)].map((_, idx) =>
          idx < currentRating ? (
            <FaStar
              key={idx}
              className="text-yellow-400 cursor-pointer"
              onClick={() =>
                setCurrentRating(currentRating === 1 && idx === 0 ? 0 : idx + 1)
              }
            />
          ) : (
            <FaRegStar
              key={idx}
              className="cursor-pointer"
              onClick={() => setCurrentRating(idx + 1)}
            />
          )
        )}
      </div>
    </div>
  );
}
