import axiosInstance from "./axios";

export const login = (payload) => axiosInstance.post("/api/users/login", payload);

export const register = (payload) =>
	axiosInstance.post("/api/users/register", payload);

export const getProfile = () => axiosInstance.get("/api/users/profile");
