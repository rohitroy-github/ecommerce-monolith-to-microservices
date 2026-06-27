import axiosInstance from "./axios";

export const getSellerDashboard = (sellerId) =>
	axiosInstance.get(`/api/orders/dashboard/sellers/${sellerId}/metrics`);

export const getAdminDashboard = () =>
	axiosInstance.get("/api/orders/dashboard/admin/overview");
