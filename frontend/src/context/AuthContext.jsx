import React, { createContext, useContext, useState, useEffect } from 'react';
import api from '../api/client';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [token, setToken] = useState(localStorage.getItem('repairmatch_token'));
  const [user, setUser] = useState(() => {
    const saved = localStorage.getItem('repairmatch_user');
    return saved ? JSON.parse(saved) : null;
  });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (token) {
      api.get('/auth/me')
        .then((res) => {
          setUser(res.data);
          localStorage.setItem('repairmatch_user', JSON.stringify(res.data));
        })
        .catch(() => {
          logout();
        })
        .finally(() => setLoading(false));
    } else {
      setLoading(false);
    }
  }, [token]);

  const login = async (email, password) => {
    const res = await api.post('/auth/login', { email, password });
    const { token, userId, role, fullName } = res.data;
    localStorage.setItem('repairmatch_token', token);
    const userData = { id: userId, email, role, fullName };
    localStorage.setItem('repairmatch_user', JSON.stringify(userData));
    setToken(token);
    setUser(userData);
    return userData;
  };

  const register = async (data) => {
    const res = await api.post('/auth/register', data);
    const { token, userId, role, fullName, email } = res.data;
    localStorage.setItem('repairmatch_token', token);
    const userData = { id: userId, email, role, fullName };
    localStorage.setItem('repairmatch_user', JSON.stringify(userData));
    setToken(token);
    setUser(userData);
    return userData;
  };

  const sendOtp = async (phoneNumber) => {
    const res = await api.post('/auth/otp/send', { phoneNumber });
    return res.data;
  };

  const loginWithOtp = async (phoneNumber, otp) => {
    const res = await api.post('/auth/otp/verify', { phoneNumber, otp });
    const { token, userId, role, fullName, email } = res.data;
    localStorage.setItem('repairmatch_token', token);
    const userData = { id: userId, email, role, fullName };
    localStorage.setItem('repairmatch_user', JSON.stringify(userData));
    setToken(token);
    setUser(userData);
    return userData;
  };

  const logout = () => {
    localStorage.removeItem('repairmatch_token');
    localStorage.removeItem('repairmatch_user');
    setToken(null);
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ token, user, role: user?.role, isAuthenticated: !!token, loading, login, register, sendOtp, loginWithOtp, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);
