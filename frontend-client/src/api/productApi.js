import axiosInstance from "./axios";

export const getProducts = () => axiosInstance.get("/api/products");
export const getProductById = (id) => axiosInstance.get(`/api/products/${id}`);
export const getSellerProducts = (sellerId) =>
	axiosInstance.get(`/api/products/sellers/${sellerId}`);
export const createProduct = (payload) =>
  axiosInstance.post("/api/products", payload);
