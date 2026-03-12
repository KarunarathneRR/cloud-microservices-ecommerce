import { createContext, useContext, useState } from 'react';

const AuthContext = createContext(null);

const STORAGE_KEY = 'auth';

function loadAuth() {
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    if (raw) return JSON.parse(raw);
  } catch {
    return { token: null, userId: null, email: null, role: null };
  }
  return { token: null, userId: null, email: null, role: null };
}

export function AuthProvider({ children }) {
  const [auth, setAuth] = useState(loadAuth);

  function login(state) {
    setAuth(state);
    localStorage.setItem(STORAGE_KEY, JSON.stringify(state));
  }

  function logout() {
    setAuth({ token: null, userId: null, email: null, role: null });
    localStorage.removeItem(STORAGE_KEY);
  }

  function updateAuth(partial) {
    setAuth((current) => {
      const next = { ...current, ...partial };
      localStorage.setItem(STORAGE_KEY, JSON.stringify(next));
      return next;
    });
  }

  return <AuthContext.Provider value={{ ...auth, login, logout, updateAuth }}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used within AuthProvider');
  return ctx;
}
