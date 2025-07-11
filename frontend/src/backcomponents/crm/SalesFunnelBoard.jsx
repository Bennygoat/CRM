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
export const columnStyleMap = {
  INITIAL_CONTACT: "default",
  PROPOSAL: "warning",
  NEGOTIATION: "error",
  CLOSED_WON: "success",
};

export default function SalesFunnelBoard({ columns, setColumns, onCardDoubleClick, onContractGenerated }) {
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
    if (!over) return;

    const activeId = active.id;
    const overId = over.id;

    // 找出來源欄和目標欄
    const sourceColumn = Object.keys(columns).find((key) =>
      columns[key].some((item) => item.id === activeId)
    );
    const targetColumn = Object.keys(columns).includes(overId)
      ? overId
      : Object.keys(columns).find((key) =>
          columns[key].some((item) => item.id === overId)
        );
    if (!sourceColumn || !targetColumn) return;

    // 拿到被拖的那筆資料
    const activeItem = columns[sourceColumn].find((i) => i.id === activeId);

    if (sourceColumn === targetColumn) {
      // 同欄排序
      const oldIndex = columns[sourceColumn].findIndex((i) => i.id === activeId);
      const newIndex = columns[targetColumn].findIndex((i) => i.id === overId);
      if (oldIndex !== newIndex) {
        const newItems = arrayMove(columns[sourceColumn], oldIndex, newIndex);
        setColumns({ ...columns, [sourceColumn]: newItems });
      }
    } else {
       const newSource = columns[sourceColumn].filter((i) => i.id !== activeId);

      // 將 type 換成對應顏色
      const updatedItem = {
        ...activeItem,
        stage: targetColumn,
        type: columnStyleMap[targetColumn]
      };

      const newTarget = [...columns[targetColumn], updatedItem];

      setColumns({
        ...columns,
        [sourceColumn]: newSource,
        [targetColumn]: newTarget,
      });

      const payloadId = activeItem.opportunityId
        ? activeItem.opportunityId
        : Number(activeId.replace(/^c/, ""));

      // 類似 SQL 的 UPDATE
      axios
        .patch(`/opportunities/${payloadId}`, { stage: targetColumn })
        .then(() => {
          console.log(`✅ 機會 (${payloadId}) 已更新為 ${targetColumn}`);
        })
        .catch((error) => {
          console.error("❌ 更新階段失敗", error);
        });

      // 如果移到「成交」，同時產合約
      if (targetColumn === "CLOSED_WON") {
        const opportunityId = activeItem.opportunityId || String(activeItem.id).replace(/^c/, "");
        axios
          .post("/contracts/generate", { opportunityId })
          .then((res) => onContractGenerated?.(res.data))
          .catch((error) => console.error("❌ 合約產生失敗", error));
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
              onCardDoubleClick={onCardDoubleClick}
            />
          ))}
      </div>
      <DragOverlay dropAnimation={null}>
        {activeCard && (
          <SortableCard
            id={activeCard.id}
            title={activeCard.title}
            rating={activeCard.rating}
            type={activeCard.type || "default"}
            isOverlay
          />
        )}
      </DragOverlay>
    </DndContext>
  );
}

function Column({ id, title, items, isOver, onCardDoubleClick }) {
  const { setNodeRef } = useDroppable({ id });
  return (
    <div
      ref={setNodeRef}
      className={`transition-colors rounded-xl min-h-[100px] ${
        isOver ? "bg-gray-200" : "bg-white"
      }`}
    >
      <h2 className="font-bold text-lg mb-2">{title}</h2>
      <SortableContext items={items.map((item) => item.id)} strategy={rectSortingStrategy}>
        <div className="flex flex-col gap-4">
          {items.map((item) => (
            <SortableCard
              key={item.id}
              id={item.id}
              title={item.title}
              rating={item.rating || 0}
              type={item.type || "default"}
              onCardDoubleClick={() => onCardDoubleClick(item)}
            />
          ))}
        </div>
      </SortableContext>
    </div>
  );
}

function SortableCard({ id, title, rating, type = "default", isOverlay = false, onCardDoubleClick }) {
  const [currentRating, setCurrentRating] = useState(rating);
  const [showColorPicker, setShowColorPicker] = useState(false);
  const [selectedType, setSelectedType] = useState(type);

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
      <div className="absolute bottom-2 right-2">
        <FaEdit
          className="text-gray-500 hover:text-black transition duration-200 cursor-pointer"
          onClick={() => setShowColorPicker((v) => !v)}
        />
        {showColorPicker && (
          <div className="absolute right-0 mt-2 bg-white border rounded shadow-md z-50 p-2 space-y-1">
            {["success", "warning", "error", "info"].map((opt) => (
              <div
                key={opt}
                onClick={() => {
                  setSelectedType(opt);
                  setShowColorPicker(false);

                  axios.patch(`/opportunities/${id}`, { type: opt })
                      .then(() => {
                        console.log(`✅ 顏色標籤已更新為 ${opt}`);
                      })
                      .catch((error) => {
                        console.error("❌ 顏色標籤更新失敗", error);
                        setSelectedType(type);
                      });
                }}
                className="flex items-center gap-2 px-2 py-1 text-sm cursor-pointer rounded hover:bg-gray-100"
              >
                <span className={`inline-block w-3 h-3 rounded-full ${
                  { success:"bg-green-400", warning:"bg-yellow-400", error:"bg-red-400", info:"bg-blue-400" }[opt]
                }`} />
                {opt}
              </div>
            ))}
          </div>
        )}
      </div>

      <div className="font-semibold mb-2">{title}</div>
      <div className="flex items-center text-sm text-gray-600">
        {[...Array(5)].map((_, idx) => {
          const filled = idx < currentRating;
          const Icon = filled ? FaStar : FaRegStar;
          return (
            <Icon
              key={idx}
              className={filled ? "text-yellow-400 cursor-pointer" : "cursor-pointer"}
              onClick={() => setCurrentRating(filled && idx === 0 ? 0 : idx + 1)}
            />
          );
        })}
      </div>
    </div>
  );
}
