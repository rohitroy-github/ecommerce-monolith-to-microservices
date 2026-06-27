import { Navigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

const RoleGuard = ({ children, allowedRoles = [] }) => {
  const { user } = useAuth();
  const role = user?.role;

  if (!role || !allowedRoles.includes(role)) {
    return <Navigate to="/products" replace />;
  }

  return children;
};

export default RoleGuard;