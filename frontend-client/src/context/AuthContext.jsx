import { createContext, useContext, useEffect, useMemo, useState } from "react";
import * as authApi from "../api/authApi";

export const AuthContext = createContext(null);

const TOKEN_KEY = "auth_token";
const USER_KEY = "auth_user";

const parseUser = (rawUser) => {
  try {
    return rawUser ? JSON.parse(rawUser) : null;
  } catch {
    return null;
  }
};

const getTokenFromResponse = (data) => {
  return data?.token || data?.jwt || data?.accessToken || "";
};

const getUserFromResponse = (data) => {
  if (data?.user) {
    return data.user;
  }

  if (data?.role || data?.email || data?.name || data?.userId || data?.id) {
    return {
      id: data.id ?? data.userId,
      userId: data.userId ?? data.id,
      role: data.role,
      email: data.email,
      name: data.name,
    };
  }

  return null;
};

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(() => parseUser(localStorage.getItem(USER_KEY)));
  const [token, setToken] = useState(() => localStorage.getItem(TOKEN_KEY) || "");

  useEffect(() => {
    if (token) {
      localStorage.setItem(TOKEN_KEY, token);
    } else {
      localStorage.removeItem(TOKEN_KEY);
    }
  }, [token]);

  useEffect(() => {
    if (user) {
      localStorage.setItem(USER_KEY, JSON.stringify(user));
    } else {
      localStorage.removeItem(USER_KEY);
    }
  }, [user]);

  const login = async (credentials) => {
    const response = await authApi.login(credentials);
    const data = response?.data ?? {};
    const incomingToken = getTokenFromResponse(data);
    const incomingUser = getUserFromResponse(data);

    if (!incomingToken) {
      throw new Error("Login succeeded but no JWT token was returned.");
    }

    setToken(incomingToken);
    setUser(incomingUser);

    return incomingUser;
  };

  const register = async (payload) => {
    const response = await authApi.register(payload);
    return response?.data;
  };

  const logout = () => {
    setToken("");
    setUser(null);
  };

  const isAuthenticated = Boolean(token);

  const value = useMemo(
    () => ({
      user,
      token,
      isAuthenticated,
      login,
      register,
      logout,
    }),
    [user, token, isAuthenticated],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = () => {
  const context = useContext(AuthContext);

  if (!context) {
    throw new Error("useAuth must be used within AuthProvider.");
  }

  return context;
};
