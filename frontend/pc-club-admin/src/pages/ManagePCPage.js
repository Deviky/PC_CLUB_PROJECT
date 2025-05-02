import React, { useState, useEffect } from "react";
import { useAuth } from "../auth/AuthContext";
import { toast, ToastContainer } from "react-toastify";
import CreatePCForm from "../components/CreatePCForm"; // Импортируем форму
import CreatePCGroupForm from "../components/CreatePCGroupForm"; // Импортируем форму создания группы ПК
import "react-toastify/dist/ReactToastify.css";
import "../styles/ManagePCPage.css";

const ManagePCPage = () => {
  const [pcGroups, setPcGroups] = useState([]);
  const [loading, setLoading] = useState(true);
  const { token } = useAuth();
  const [expandedGroup, setExpandedGroup] = useState(null);
  const [showCreateForm, setShowCreateForm] = useState(false); // состояние для создания ПК
  const [showCreateGroupForm, setShowCreateGroupForm] = useState(false); // состояние для создания группы ПК

  useEffect(() => {
    const fetchPcGroups = async () => {
      try {
        const response = await fetch("http://62.109.1.5:8966/pc-service/pc-group/get-all", {
          method: "GET",
          headers: {
            "Authorization": `Bearer ${token}`,
          },
        });
        const data = await response.json();
        console.log(data);
        setPcGroups(data);
      } catch (error) {
        console.error("Ошибка при получении данных:", error);
        toast.error("Не удалось загрузить данные.");
      } finally {
        setLoading(false);
      }
    };

    fetchPcGroups();
  }, [token]);

  const handleStatusUpdate = async (pcId, newStatus) => {
    try {
      const response = await fetch("http://62.109.1.5:8966/pc-service/pc/change-status", {
        method: "PATCH",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`,
        },
        body: JSON.stringify({ pcId, newStatus }),
      });

      const data = await response.json();

      if (!response.ok) {
        // Ошибка удаления
        if (data.message && data.clients && data.clients.length > 0) {
          toast.error(data.message);
          setPcGroups((prevGroups) =>
            prevGroups.map((group) => ({
              ...group,
              pcs: group.pcs.map((pc) =>
                pc.id === pcId ? { ...pc, clients: data.clients } : pc
              ),
            }))
          );
        } else {
          toast.error(data.message || "Ошибка при обновлении статуса ПК.");
        }
      } else {
        toast.success("Статус ПК обновлён!");
        setPcGroups((prevGroups) =>
          prevGroups.map((group) => ({
            ...group,
            pcs: group.pcs.map((pc) =>
              pc.id === pcId ? { ...pc, status: newStatus } : pc
            ),
          }))
        );
      }
    } catch (error) {
      console.error("Ошибка при изменении статуса:", error);
      toast.error("Не удалось обновить статус ПК.");
    }
  };

  const handleDeletePc = async (pcId) => {
    try {
      const response = await fetch(`http://62.109.1.5:8966/pc-service/pc/delete/${pcId}`, {
        method: "DELETE",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`,
        },
      });
  
      const data = await response.json();
  
      if (!response.ok) {
        // Ошибка удаления
        if (data.message && data.clients && data.clients.length > 0) {
          toast.error(data.message);
          setPcGroups((prevGroups) =>
            prevGroups.map((group) => ({
              ...group,
              pcs: group.pcs.map((pc) =>
                pc.id === pcId ? { ...pc, clients: data.clients } : pc
              ),
            }))
          );
        } else {
          toast.error(data.message || "Ошибка при удалении ПК.");
        }
      } else {
        toast.success("ПК успешно удалён!");
        setPcGroups((prevGroups) =>
          prevGroups.map((group) => ({
            ...group,
            pcs: group.pcs.filter((pc) => pc.id !== pcId), // Удаляем ПК из группы
          }))
        );
      }
    } catch (error) {
      console.error("Ошибка при удалении ПК:", error);
      toast.error("Не удалось удалить ПК.");
    }
  };

  const toggleGroup = (groupId) => {
    setExpandedGroup((prev) => (prev === groupId ? null : groupId));
  };

  const handleCreatePc = async (requestData) => {
    try {
      const response = await fetch("http://62.109.1.5:8966/pc-service/pc/create", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`,
        },
        body: JSON.stringify(requestData),
      });

      const data = await response.json();

      if (data.message && data.message.startsWith("Ошибка")) {
        toast.error(data.message);
      } else {
        toast.success("ПК успешно создан!");
        setShowCreateForm(false);
        const updatedResponse = await fetch("http://62.109.1.5:8966/pc-service/pc-group/get-all", {
          method: "GET",
          headers: {
            "Authorization": `Bearer ${token}`,
          },
        });
        const updatedData = await updatedResponse.json();
        setPcGroups(updatedData);
      }
    } catch (error) {
      console.error("Ошибка при создании ПК:", error);
      toast.error("Не удалось создать ПК.");
    }
  };

  const handleCreateGroup = async (requestData) => {
    try {
      const response = await fetch("http://62.109.1.5:8966/pc-service/pc-group/create", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`,
        },
        body: JSON.stringify(requestData),
      });

      const data = await response.json();

      if (data.message && data.message.startsWith("Ошибка")) {
        toast.error(data.message);
      } else {
        toast.success("Группа ПК успешно создана!");
        setShowCreateGroupForm(false);
        const updatedResponse = await fetch("http://62.109.1.5:8966/pc-service/pc-group/get-all", {
          method: "GET",
          headers: {
            "Authorization": `Bearer ${token}`,
          },
        });
        const updatedData = await updatedResponse.json();
        setPcGroups(updatedData);
      }
    } catch (error) {
      console.error("Ошибка при создании группы ПК:", error);
      toast.error("Не удалось создать группу ПК.");
    }
  };

  const handleDeleteGroup = async (groupId) => {
    if (!window.confirm("Вы уверены, что хотите удалить эту группу ПК?")) return;

    try {
      const response = await fetch(`http://62.109.1.5:8966/pc-service/pc-group/delete/${groupId}`, {
        method: "DELETE",
        headers: {
          "Authorization": `Bearer ${token}`,
        },
      });

      const data = await response.json();

      if (!response.ok) {
        toast.error(data.message || "Ошибка при удалении группы ПК.");
      } else {
        toast.success("Группа ПК успешно удалена!");
        setPcGroups((prevGroups) => prevGroups.filter((group) => group.id !== groupId));
      }
    } catch (error) {
      console.error("Ошибка при удалении группы ПК:", error);
      toast.error("Не удалось удалить группу ПК.");
    }
  };

  if (loading) {
    return <div>Загрузка...</div>;
  }

  return (
    <div className={`pc-manage-manage-pc-page ${showCreateForm || showCreateGroupForm ? 'pc-manage-modal-open' : ''}`}>
      <h1>Управление ПК</h1>
      <button className="pc-manage-add-pc-button" onClick={() => setShowCreateForm(true)}>Добавить ПК</button>
      <button className="pc-manage-add-pc-group-button" onClick={() => setShowCreateGroupForm(true)}>Добавить группу ПК</button>

      {pcGroups.length === 0 ? (
        <p>Нет доступных групп ПК.</p>
      ) : (
        pcGroups.map((group) => (
          <div key={group.id} className={`pc-manage-pc-group ${expandedGroup === group.id ? 'pc-manage-expanded' : ''}`}>
            <div className="pc-manage-pc-group-header">
              <h2>{`Группа ПК: ${group.cpu} | ${group.gpu} | ${group.ram}GB RAM`}</h2>
              <div className="pc-manage-pc-group-actions">
                <button
                  className="pc-manage-delete-group-button"
                  onClick={() => handleDeleteGroup(group.id)}
                >
                  Удалить группу
                </button>
                <button onClick={() => toggleGroup(group.id)}>
                  {expandedGroup === group.id ? "Скрыть" : "Развернуть"}
                </button>
              </div>
            </div>
            {expandedGroup === group.id && (
              <div className="pc-manage-pc-list">
                {group.pcs.map((pc) => (
                  <div key={pc.id} className="pc-manage-pc-item">
                    <div className="pc-manage-pc-info">
                      <p>ПК ID: {pc.id}</p>
                    </div>
                    <div className="pc-manage-pc-actions">
                      <button 
                        onClick={() => handleStatusUpdate(pc.id, pc.status === "WORKED" ? "NOTWORKED" : "WORKED")}
                      >
                        {pc.status === "WORKED" ? "Перевести в НЕРАБОТАЮЩИЙ" : "Перевести в РАБОТАЮЩИЙ"}
                      </button>
                      <button onClick={() => handleDeletePc(pc.id)}>Удалить ПК</button>
                    </div>
                    {pc.clients && pc.clients.length > 0 && (
                      <div className="pc-manage-pc-clients">
                        <h3>Клиенты, использующие этот ПК:</h3>
                        <ul>
                          {pc.clients.map((client) => (
                            <li key={client.id}>
                              {client.email} (ID: {client.id})
                            </li>
                          ))}
                        </ul>
                      </div>
                    )}
                  </div>
                ))}
              </div>
            )}
          </div>
        ))
      )}

      {showCreateForm && (
        <div className="pc-manage-modal-overlay">
          <div className="pc-manage-modal-content">
            <CreatePCForm pcGroups={pcGroups} onSubmit={handleCreatePc} />
            <button className="pc-manage-close-button" onClick={() => setShowCreateForm(false)}>X</button>
          </div>
        </div>
      )}

      {showCreateGroupForm && (
        <div className="pc-manage-modal-overlay">
          <div className="pc-manage-modal-content">
            <CreatePCGroupForm onSubmit={handleCreateGroup} />
            <button className="pc-manage-close-button" onClick={() => setShowCreateGroupForm(false)}>X</button>
          </div>
        </div>
      )}

      <ToastContainer position="bottom-right" autoClose={3000} />
    </div>
  );
};

export default ManagePCPage;
