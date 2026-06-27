import { useLocation, useNavigate } from "react-router-dom";
import { useCart } from "../context/CartContext";
import { useAuth } from "../context/AuthContext";

const currencyFormatter = new Intl.NumberFormat("en-IN", {
  style: "currency",
  currency: "INR",
  maximumFractionDigits: 2,
});

const ProductCard = ({ product }) => {
  const { addToCart } = useCart();
  const { isAuthenticated } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const name = product?.name || "Unnamed product";
  const description = product?.description || "No description provided.";
  const sellerId = product?.sellerId ?? "N/A";
  const priceNumber = Number(product?.price);
  const price = Number.isFinite(priceNumber)
    ? currencyFormatter.format(priceNumber)
    : "Price unavailable";

  const handleAddToCart = () => {
    if (!isAuthenticated) {
      navigate("/login", { state: { from: location } });
      return;
    }

    addToCart(product);
  };

  return (
    <article className="group rounded-2xl border border-zinc-200 bg-white p-5 shadow-sm transition hover:-translate-y-0.5 hover:shadow-md">
      <div className="mb-3 flex items-center justify-between gap-3">
        <span className="rounded-full border border-amber-200 bg-amber-50 px-2.5 py-1 text-xs font-semibold tracking-wide text-amber-700">
          Seller #{sellerId}
        </span>
        <span className="text-sm font-semibold text-zinc-900">{price}</span>
      </div>

      <h3 className="text-lg font-semibold text-zinc-900">{name}</h3>
      <p className="mt-2 line-clamp-3 text-sm leading-6 text-zinc-600">
        {description}
      </p>

      <button
        type="button"
        onClick={handleAddToCart}
        className="mt-4 inline-flex w-full items-center justify-center rounded-xl bg-zinc-900 px-4 py-2.5 text-sm font-semibold text-white transition hover:bg-black"
      >
        Add to Cart
      </button>
    </article>
  );
};

export default ProductCard;
