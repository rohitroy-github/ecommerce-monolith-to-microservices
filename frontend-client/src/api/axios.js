import axios from "axios";

const TOKEN_KEY = "auth_token";

const axiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || "http://localhost:8080",
  headers: {
    "Content-Type": "application/json",
  },
});

axiosInstance.interceptors.request.use((config) => {
  const requestPath = config.url || "";
  const isPublicAuthPath =
    requestPath === "/api/users/login" || requestPath === "/api/users/register";

  if (isPublicAuthPath) {
    return config;
  }

  const token = localStorage.getItem(TOKEN_KEY);

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

export default axiosInstance;
