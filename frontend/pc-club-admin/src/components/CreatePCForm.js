import React, { useState } from 'react';
import '../styles/CreatePCForm.css';


const CreatePCForm = ({ pcGroups, onSubmit }) => {
  const [selectedGroup, setSelectedGroup] = useState('');
  const [status, setStatus] = useState('WORKED'); // по умолчанию "РАБОТАЕТ"

  const handleSubmit = (e) => {
    e.preventDefault();
    const requestData = {
      pcGroupId: selectedGroup,
      status: status,
    };
    onSubmit(requestData); // передаем данные родительскому компоненту
  };

  return (
    <form onSubmit={handleSubmit} className="create-pc-form">
      <div>
        <label>Выберите группу ПК:</label>
        <select
          value={selectedGroup}
          onChange={(e) => setSelectedGroup(e.target.value)}
          required
        >
          <option value="">Выберите группу</option>
          {pcGroups.map((group) => (
            <option key={group.id} value={group.id}>
              {group.cpu} | {group.gpu} | {group.ram}GB RAM
            </option>
          ))}
        </select>
      </div>
      <div>
        <label>Статус ПК:</label>
        <select
          value={status}
          onChange={(e) => setStatus(e.target.value)}
          required
        >
          <option value="WORKED">РАБОТАЕТ</option>
          <option value="NOTWORKED">НЕРАБОТАЕТ</option>
        </select>
      </div>
      <button type="submit">Добавить</button>
    </form>
  );
};

export default CreatePCForm;