import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { useCart } from "../context/CartContext";

const linkClass =
  "rounded-md border border-zinc-300 px-3 py-1.5 text-sm font-medium text-zinc-800 transition hover:bg-zinc-100";

const Navbar = () => {
  const { isAuthenticated, logout, user } = useAuth();
  const { itemCount } = useCart();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  return (
    <nav className="mb-6 flex flex-wrap items-center justify-between gap-3 rounded-xl border border-zinc-200 bg-zinc-50 p-3">
      <div className="flex flex-wrap items-center gap-2">
        <Link to="/products" className={linkClass}>
          Products
        </Link>
      </div>

      <div className="flex flex-wrap items-center gap-2">
        {!isAuthenticated && (
          <Link to="/login" className={linkClass}>
            Login
          </Link>
        )}
        {!isAuthenticated && (
          <Link to="/register" className={linkClass}>
            Register
          </Link>
        )}
        {isAuthenticated && user?.role && (
          <span className="rounded-md border border-zinc-300 px-3 py-1.5 text-xs font-semibold tracking-wide text-zinc-700">
            {user.role}
          </span>
        )}

        {user?.role === "SELLER" && (
          <Link to="/seller/dashboard" className={linkClass}>
            Seller Dashboard
          </Link>
        )}

        {user?.role === "SELLER" && (
          <Link to="/seller/products" className={linkClass}>
            Seller Products
          </Link>
        )}

        {user?.role === "ADMIN" && (
          <Link to="/admin/dashboard" className={linkClass}>
            Admin Dashboard
          </Link>
        )}

        {user?.role === "CUSTOMER" && (
          <Link to="/cart" className={linkClass}>
            Cart ({itemCount})
          </Link>
        )}

        {isAuthenticated && (
          <button
            type="button"
            onClick={handleLogout}
            className="rounded-md bg-zinc-900 px-3 py-1.5 text-sm font-semibold text-white transition hover:bg-black"
          >
            Logout
          </button>
        )}
      </div>
    </nav>
  );
};

export default Navbar;
