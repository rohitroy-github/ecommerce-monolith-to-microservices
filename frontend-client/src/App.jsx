import { Navigate, Route, Routes, useLocation } from "react-router-dom";
import Footer from "./components/Footer";
import Navbar from "./components/Navbar";
import ProtectedRoute from "./components/ProtectedRoute";
import RoleGuard from "./components/RoleGuard";
import { useAuth } from "./context/AuthContext";
import Home from "./pages/Home";
import Login from "./pages/Login";
import Products from "./pages/Products";
import Register from "./pages/Register";
import CreateProduct from "./pages/CreateProduct";
import Cart from "./pages/Cart";
import SellerDashboard from "./pages/SellerDashboard";
import SellerProducts from "./pages/SellerProducts";
import AdminDashboard from "./pages/AdminDashboard";

const App = () => {
  const { isAuthenticated, user } = useAuth();
  const location = useLocation();

  const defaultRoute = isAuthenticated
    ? user?.role === "SELLER"
      ? "/seller/dashboard"
      : user?.role === "ADMIN"
        ? "/admin/dashboard"
        : "/products"
    : "/";

  const isAuthPage =
    location.pathname === "/login" || location.pathname === "/register";

  const isCenteredPage = isAuthPage || location.pathname === "/";

  return (
    <main className="flex min-h-screen flex-col bg-white text-zinc-900">
      <div
        className={`mx-auto w-full flex-1 px-4 sm:px-6 lg:px-8 ${
          isCenteredPage ? "max-w-3xl" : "max-w-5xl py-8"
        }`}
      >
        {!isCenteredPage && <Navbar />}

        <div className={isAuthPage ? "flex min-h-screen items-center justify-center" : ""}>
          <Routes>
            <Route
              path="/"
              element={
                isAuthenticated ? (
                  <Navigate
                    to={
                      user?.role === "SELLER"
                        ? "/seller/dashboard"
                        : user?.role === "ADMIN"
                          ? "/admin/dashboard"
                          : "/products"
                    }
                    replace
                  />
                ) : (
                  <Home />
                )
              }
            />
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            <Route
              path="/products"
              element={<Products />}
            />
            <Route
              path="/cart"
              element={
                <ProtectedRoute>
                  <RoleGuard allowedRoles={["CUSTOMER"]}>
                    <Cart />
                  </RoleGuard>
                </ProtectedRoute>
              }
            />
            <Route
              path="/seller/dashboard"
              element={
                <ProtectedRoute>
                  <RoleGuard allowedRoles={["SELLER"]}>
                    <SellerDashboard />
                  </RoleGuard>
                </ProtectedRoute>
              }
            />
            <Route
              path="/seller/products"
              element={
                <ProtectedRoute>
                  <RoleGuard allowedRoles={["SELLER"]}>
                    <SellerProducts />
                  </RoleGuard>
                </ProtectedRoute>
              }
            />
            <Route
              path="/seller/products/new"
              element={
                <ProtectedRoute>
                  <RoleGuard allowedRoles={["SELLER"]}>
                    <CreateProduct />
                  </RoleGuard>
                </ProtectedRoute>
              }
            />
            <Route
              path="/admin/dashboard"
              element={
                <ProtectedRoute>
                  <RoleGuard allowedRoles={["ADMIN"]}>
                    <AdminDashboard />
                  </RoleGuard>
                </ProtectedRoute>
              }
            />
            <Route path="*" element={<Navigate to={defaultRoute} replace />} />
          </Routes>
        </div>
      </div>
      <Footer />
    </main>
  );
};

export default App;
