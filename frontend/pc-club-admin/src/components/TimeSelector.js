import React, { useState } from 'react';
import '../styles/TimeSelector.css';

const TimeSelector = ({ minTime, maxTime, dateFrom, setDateFrom, duration, setDuration }) => {
  const [timeValue, setTimeValue] = useState(dateFrom);

  // Обработчик изменения времени
  const handleTimeChange = (e) => {
    setTimeValue(e.target.value);
    setDateFrom(e.target.value);
  };

  return (
    <div className="time-selector">
      <h3 className="text-lg">Выберите время бронирования</h3>
      <div>
        <label htmlFor="dateFrom" className="block">Дата и время начала</label>
        <input
          id="dateFrom"
          type="datetime-local"
          min={`${new Date().toISOString().slice(0, 16)}`}  // Устанавливаем минимальное время как текущее
          max={maxTime}
          lang="ru"  // Устанавливаем локаль на русский
          className="border rounded p-2"
          value={timeValue}
          onChange={handleTimeChange}
        />
      </div>
      <div className="mt-2">
        <label htmlFor="duration" className="block">Количество часов</label>
        <input
          id="duration"
          type="number"
          className="border rounded p-2"
          value={duration}
          onChange={(e) => setDuration(e.target.value)}
          min="1"
        />
      </div>
    </div>
  );
};

export default TimeSelector;