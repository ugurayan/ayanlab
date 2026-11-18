import React, { useState } from "react";
import { Button } from "../../ui/components/Button";
import { Input } from "../../ui/components/Input";

export type DressType = "casual" | "smart-casual" | "business" | "dress-provided" | "custom-dress" | "no-preference";

export type SelectedDressItem = {
  item: string;
  color: string;
  id: string;
};

export type DressModalData = {
  selectedDressType: DressType | "";
  selectedDressItems: SelectedDressItem[];
  selectedAttireRestrictions: string[];
  selectedGroomingRestrictions: string[];
};

const DRESS_TYPES = [
  { id: "casual" as DressType, name: "Casual", image: "https://storage.googleapis.com/public-buckets/staff-iq/casual_10.jpeg" },
  { id: "smart-casual" as DressType, name: "Smart Casual", image: "https://storage.googleapis.com/public-buckets/staff-iq/smart-casual_10.jpeg" },
  { id: "business" as DressType, name: "Business", image: "https://storage.googleapis.com/public-buckets/staff-iq/office_10.png" },
  { id: "dress-provided" as DressType, name: "Dress Provided", image: "https://storage.googleapis.com/public-buckets/staff-iq/dress-provided_10.jpeg" },
  { id: "custom-dress" as DressType, name: "Custom Dress", image: "https://storage.googleapis.com/public-buckets/staff-iq/custom-dresss_10.jpeg" },
  { id: "no-preference" as DressType, name: "No Preference", image: null },
];

const DRESS_ITEMS = [
  "T-shirt", "Polo Shirt", "Button-up Shirt", "Blouse", "Dress Shirt",
  "Pants", "Jeans", "Khakis", "Slacks", "Shorts",
  "Skirt", "Dress", "Jacket", "Blazer", "Sweater",
  "Shoes", "Sneakers", "Boots", "Heels", "Flats",
  "Hat", "Cap", "Apron", "Uniform", "Vest"
];

const COLORS = [
  "Black", "White", "Navy", "Gray", "Brown", "Beige",
  "Red", "Blue", "Green", "Yellow", "Orange", "Pink",
  "Purple", "Maroon", "Burgundy", "Tan", "Khaki"
];

const COLOR_MAP: Record<string, string> = {
  "Black": "#000000",
  "White": "#FFFFFF",
  "Navy": "#000080",
  "Gray": "#808080",
  "Brown": "#A52A2A",
  "Beige": "#F5F5DC",
  "Red": "#FF0000",
  "Blue": "#0000FF",
  "Green": "#008000",
  "Yellow": "#FFFF00",
  "Orange": "#FFA500",
  "Pink": "#FFC0CB",
  "Purple": "#800080",
  "Maroon": "#800000",
  "Burgundy": "#800020",
  "Tan": "#D2B48C",
  "Khaki": "#C3B091"
};

const ATTIRE_RESTRICTIONS = [
  "No rips, holes in clothes",
  "No open toed shoes",
  "No tank tops",
  "No jeans",
  "No crop tops",
  "No shorts",
  "No baggy clothing",
  "No hoodies",
  "No headphones",
  "Clear bags only",
  "No revealing clothing",
  "No crocs, flip flops",
  "No graphics on clothes",
  "No tennis shoes, sneakers",
  "No logos or text on clothes",
  "No leggings",
  "No jeans with rips",
  "No hats",
  "No high heel shoes"
];

const GROOMING_RESTRICTIONS = [
  "No piercings on the face",
  "No visible tattoos (tattoos must be covered)",
  "No long or dirty fingernails",
  "No facial tattoos",
  "No acrylics or fake nails",
  "No nail color/paints",
  "No heavy jewelry (limited jewelry is okay)",
  "No loose long hair (long hair must be pulled up)",
  "No extreme hair colors (e.g., blue)",
  "No untrimmed beard or mustache"
];

interface DressModalContentProps {
  data: DressModalData;
  onDataChange: (data: DressModalData) => void;
  onClose: () => void;
}

export function DressModalContent({ data, onDataChange, onClose }: DressModalContentProps) {
  const [selectedItem, setSelectedItem] = useState("");
  const [selectedColor, setSelectedColor] = useState("");

  const handleDressTypeSelect = (type: DressType) => {
    onDataChange({ ...data, selectedDressType: type });
  };

  const handleAddDressItem = () => {
    if (selectedItem && selectedColor) {
      const newItem: SelectedDressItem = {
        item: selectedItem,
        color: selectedColor,
        id: `${Date.now()}-${Math.random().toString(36).substr(2, 9)}`
      };
      onDataChange({
        ...data,
        selectedDressItems: [...data.selectedDressItems, newItem]
      });
      setSelectedItem("");
      setSelectedColor("");
    }
  };

  const handleRemoveDressItem = (id: string) => {
    onDataChange({
      ...data,
      selectedDressItems: data.selectedDressItems.filter(item => item.id !== id)
    });
  };

  const handleAttireRestrictionToggle = (restriction: string) => {
    const isSelected = data.selectedAttireRestrictions.includes(restriction);
    onDataChange({
      ...data,
      selectedAttireRestrictions: isSelected
        ? data.selectedAttireRestrictions.filter(r => r !== restriction)
        : [...data.selectedAttireRestrictions, restriction]
    });
  };

  const handleGroomingRestrictionToggle = (restriction: string) => {
    const isSelected = data.selectedGroomingRestrictions.includes(restriction);
    onDataChange({
      ...data,
      selectedGroomingRestrictions: isSelected
        ? data.selectedGroomingRestrictions.filter(r => r !== restriction)
        : [...data.selectedGroomingRestrictions, restriction]
    });
  };

  return (
    <div className="space-y-6">
      {/* First Card: Attire Instructions (Dress Types) */}
      <div className="space-y-3">
        <div className="text-sm font-semibold text-slate-900">
          Attire instructions <span className="text-red-500">*</span>
        </div>
        <div className="grid grid-cols-3 gap-3">
          {DRESS_TYPES.map((type) => (
            <button
              key={type.id}
              type="button"
              onClick={() => handleDressTypeSelect(type.id)}
              className={`
                relative border-2 rounded-lg p-3 transition-all
                ${data.selectedDressType === type.id
                  ? 'border-blue-500 bg-blue-50'
                  : 'border-slate-200 hover:border-slate-300'
                }
              `}
            >
              {data.selectedDressType === type.id && (
                <div className="absolute top-2 right-2 bg-green-500 rounded-full p-1">
                  <svg className="w-4 h-4 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
                  </svg>
                </div>
              )}
              <div className="flex flex-col items-center gap-2">
                {type.image ? (
                  <img
                    src={type.image}
                    alt={type.name}
                    className="w-full h-32 object-cover rounded"
                    onError={(e) => {
                      e.currentTarget.src = "https://via.placeholder.com/200x150?text=" + type.name;
                    }}
                  />
                ) : (
                  <div className="w-full h-32 bg-slate-100 rounded flex items-center justify-center">
                    <svg className="w-12 h-12 text-slate-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                    </svg>
                  </div>
                )}
                <span className="text-xs font-medium text-slate-700">{type.name}</span>
              </div>
            </button>
          ))}
        </div>
      </div>

      {/* Second Card: Additional Dress Items */}
      <div className="space-y-3">
        <div className="text-sm font-semibold text-slate-900">Additional dress items</div>
        <div className="grid grid-cols-2 gap-3">
          <div>
            <div className="text-xs text-slate-500 mb-1">Dress Item</div>
            <select
              className="w-full rounded-2xl border border-slate-200 bg-white px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-slate-900/5"
              value={selectedItem}
              onChange={(e) => setSelectedItem(e.target.value)}
            >
              <option value="">Select dress item</option>
              {DRESS_ITEMS.map((item) => (
                <option key={item} value={item}>{item}</option>
              ))}
            </select>
          </div>
          <div>
            <div className="text-xs text-slate-500 mb-1">Color</div>
            <select
              className="w-full rounded-2xl border border-slate-200 bg-white px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-slate-900/5"
              value={selectedColor}
              onChange={(e) => setSelectedColor(e.target.value)}
            >
              <option value="">Select color</option>
              {COLORS.map((color) => (
                <option key={color} value={color}>{color}</option>
              ))}
            </select>
          </div>
        </div>
        <Button
          type="button"
          onClick={handleAddDressItem}
          disabled={!selectedItem || !selectedColor}
          className="w-full"
        >
          Add Item
        </Button>
        
        {/* Selected Items List */}
        {data.selectedDressItems.length > 0 && (
          <div className="space-y-2">
            <div className="text-xs text-slate-500">Selected items:</div>
            <div className="space-y-2">
              {data.selectedDressItems.map((item) => (
                <div
                  key={item.id}
                  className="flex items-center justify-between bg-slate-50 rounded-lg px-3 py-2"
                >
                  <div className="flex items-center gap-3">
                    <div 
                      className="w-8 h-8 rounded border border-slate-300" 
                      style={{ backgroundColor: COLOR_MAP[item.color] || item.color }}
                    ></div>
                    <div>
                      <div className="text-sm font-medium text-slate-900">{item.item}</div>
                      <div className="text-xs text-slate-500">{item.color}</div>
                    </div>
                  </div>
                  <button
                    type="button"
                    onClick={() => handleRemoveDressItem(item.id)}
                    className="text-slate-400 hover:text-slate-600"
                  >
                    ×
                  </button>
                </div>
              ))}
            </div>
          </div>
        )}
      </div>

      {/* Third Card: Attire Restrictions */}
      <div className="space-y-3">
        <div className="text-sm font-semibold text-slate-900">Attire restrictions</div>
        {data.selectedAttireRestrictions.length > 0 && (
          <div>
            <div className="text-xs text-slate-500 mb-1">Selected restrictions:</div>
            <Input
              value={data.selectedAttireRestrictions.join(", ")}
              readOnly
              className="bg-slate-100 text-slate-600 cursor-not-allowed"
            />
          </div>
        )}
        <div className="space-y-2 max-h-60 overflow-y-auto border border-slate-200 rounded-lg p-3">
          {ATTIRE_RESTRICTIONS.map((restriction) => (
            <label
              key={restriction}
              className={`
                flex items-center gap-2 p-2 rounded cursor-pointer transition-colors
                ${data.selectedAttireRestrictions.includes(restriction)
                  ? 'bg-blue-50'
                  : 'hover:bg-slate-50'
                }
              `}
            >
              <input
                type="checkbox"
                checked={data.selectedAttireRestrictions.includes(restriction)}
                onChange={() => handleAttireRestrictionToggle(restriction)}
                className="rounded border-slate-300"
              />
              <span className="text-sm text-slate-700">{restriction}</span>
            </label>
          ))}
        </div>
      </div>

      {/* Fourth Card: Grooming Restrictions */}
      <div className="space-y-3">
        <div className="text-sm font-semibold text-slate-900">Grooming restrictions</div>
        {data.selectedGroomingRestrictions.length > 0 && (
          <div>
            <div className="text-xs text-slate-500 mb-1">Selected restrictions:</div>
            <Input
              value={data.selectedGroomingRestrictions.join(", ")}
              readOnly
              className="bg-slate-100 text-slate-600 cursor-not-allowed"
            />
          </div>
        )}
        <div className="space-y-2 max-h-60 overflow-y-auto border border-slate-200 rounded-lg p-3">
          {GROOMING_RESTRICTIONS.map((restriction) => (
            <label
              key={restriction}
              className={`
                flex items-center gap-2 p-2 rounded cursor-pointer transition-colors
                ${data.selectedGroomingRestrictions.includes(restriction)
                  ? 'bg-blue-50'
                  : 'hover:bg-slate-50'
                }
              `}
            >
              <input
                type="checkbox"
                checked={data.selectedGroomingRestrictions.includes(restriction)}
                onChange={() => handleGroomingRestrictionToggle(restriction)}
                className="rounded border-slate-300"
              />
              <span className="text-sm text-slate-700">{restriction}</span>
            </label>
          ))}
        </div>
      </div>

      <div className="flex justify-end gap-2 pt-4 border-t">
        <Button type="button" variant="secondary" onClick={onClose}>
          Back
        </Button>
        <Button type="button" onClick={onClose}>
          Next
        </Button>
      </div>
    </div>
  );
}

