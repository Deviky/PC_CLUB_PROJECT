import React from 'react';
import ReserveButton from './ReserveButton';
import '../styles/PCList.css';

const PCList = ({ pcs, selectedService, dateFrom, duration, clientId, useBonus }) => {
  // Преобразуем строку в дату (формат: "DD-MM-YYYY HH:mm")
  const parseDate = (dateStr) => {
    const [day, month, year, hour, minute] = [
      dateStr.slice(0, 2),
      dateStr.slice(3, 5),
      dateStr.slice(6, 10),
      dateStr.slice(11, 13),
      dateStr.slice(14, 16),
    ];
    return new Date(`${month}/${day}/${year} ${hour}:${minute}`);
  };

  const isTimeOverlap = (reservedFrom, reservedTo, bookingStart, bookingEnd) => {
    const reservedStart = parseDate(reservedFrom);
    const reservedEnd = parseDate(reservedTo);
    return (bookingStart < reservedEnd && bookingEnd > reservedStart);
  };

  const filteredPCs = pcs.filter((pc) => {
    if (!pc.reservationList || pc.reservationList.length === 0) return true;

    const bookingStart = new Date(dateFrom);
    const bookingEnd = new Date(bookingStart);
    bookingEnd.setHours(bookingStart.getHours() + duration);

    // Проверяем, пересекаются ли временные интервалы
    return !pc.reservationList.some((reservation) =>
      isTimeOverlap(reservation.reservedFrom, reservation.reservedTo, bookingStart, bookingEnd)
    );
  });

  if (filteredPCs.length === 0) return <p>Нет доступных ПК</p>;

  return (
    <div className="pc-list">
      <ul>
        {filteredPCs.map((pc) => (
          <li key={pc.id} className="pc-item">
            <div className="pc-info">
              <span className="pc-status">
                ПК {pc.id} — {pc.status === "WORKED" ? "Доступен" : "Не доступен"}
              </span>
              {pc.status === "WORKED" && (
                <ReserveButton
                  pcId={pc.id}
                  selectedService={selectedService}
                  dateFrom={dateFrom}
                  duration={duration}
                  clientId={clientId}
                  useBonus={useBonus}
                />
              )}
            </div>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default PCList;
