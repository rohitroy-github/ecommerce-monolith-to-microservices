import axiosInstance from "./axios";

export const getOrders = () => axiosInstance.get("/api/orders");
export const createOrder = (payload) => axiosInstance.post("/api/orders", payload);
