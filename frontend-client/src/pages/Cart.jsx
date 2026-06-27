import { useMemo, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { createOrder } from "../api/orderApi";
import { useAuth } from "../context/AuthContext";
import { useCart } from "../context/CartContext";

const currencyFormatter = new Intl.NumberFormat("en-IN", {
  style: "currency",
  currency: "INR",
  maximumFractionDigits: 2,
});

const Cart = () => {
  const { user } = useAuth();
  const { items, updateQuantity, removeFromCart, clearCart } = useCart();
  const navigate = useNavigate();

  const customerId = user?.userId ?? user?.id ?? null;
  const [processing, setProcessing] = useState(false);
  const [error, setError] = useState("");
  const [successMessage, setSuccessMessage] = useState("");

  const subtotal = useMemo(
    () => items.reduce((sum, item) => sum + Number(item.price || 0) * item.quantity, 0),
    [items],
  );

  const handleCheckout = async () => {
    if (!customerId) {
      setError("Unable to resolve customer profile. Please sign in again.");
      return;
    }

    if (items.length === 0) {
      setError("Your cart is empty.");
      return;
    }

    setError("");
    setSuccessMessage("");
    setProcessing(true);

    try {
      await Promise.all(
        items.map((item) =>
          createOrder({
            customerId,
            productId: item.id,
            quantity: item.quantity,
          }),
        ),
      );

      clearCart();
      setSuccessMessage("Payment successful and order placed.");
    } catch (requestError) {
      setError(
        requestError?.response?.data?.message ||
          requestError?.message ||
          "Unable to process payment right now.",
      );
    } finally {
      setProcessing(false);
    }
  };

  return (
    <section className="space-y-6 rounded-3xl border border-zinc-200/80 bg-white p-6 shadow-[0_24px_60px_-24px_rgba(0,0,0,0.35)] sm:p-8">
      <header className="space-y-3">
        <span className="inline-flex items-center rounded-full border border-amber-200 bg-amber-50 px-3 py-1 text-xs font-semibold uppercase tracking-wide text-amber-700">
          Checkout
        </span>
        <h2 className="text-3xl font-bold tracking-tight text-zinc-900">Your cart</h2>
        <p className="text-sm leading-6 text-zinc-600">
          Review cart items and proceed with payment to place your order.
        </p>
      </header>

      {error && (
        <p className="rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
          {error}
        </p>
      )}

      {successMessage && (
        <p className="rounded-xl border border-emerald-200 bg-emerald-50 px-4 py-3 text-sm text-emerald-700">
          {successMessage}
        </p>
      )}

      {items.length === 0 ? (
        <div className="rounded-2xl border border-zinc-200 bg-zinc-50 p-6">
          <p className="text-sm text-zinc-700">Your cart is empty.</p>
          <Link
            to="/products"
            className="mt-4 inline-flex rounded-xl bg-zinc-900 px-4 py-2.5 text-sm font-semibold text-white transition hover:bg-black"
          >
            Continue shopping
          </Link>
        </div>
      ) : (
        <>
          <ul className="space-y-3">
            {items.map((item) => (
              <li
                key={item.id}
                className="rounded-2xl border border-zinc-200 bg-zinc-50 p-4"
              >
                <div className="flex flex-wrap items-start justify-between gap-3">
                  <div>
                    <p className="text-base font-semibold text-zinc-900">{item.name}</p>
                    <p className="mt-1 text-sm text-zinc-600">{currencyFormatter.format(Number(item.price || 0))} each</p>
                  </div>

                  <button
                    type="button"
                    onClick={() => removeFromCart(item.id)}
                    className="rounded-lg border border-zinc-300 bg-white px-3 py-1.5 text-xs font-semibold text-zinc-700 transition hover:bg-zinc-100"
                  >
                    Remove
                  </button>
                </div>

                <div className="mt-4 flex items-center justify-between gap-3">
                  <label className="text-sm font-medium text-zinc-700" htmlFor={`qty-${item.id}`}>
                    Quantity
                  </label>
                  <input
                    id={`qty-${item.id}`}
                    type="number"
                    min="1"
                    value={item.quantity}
                    onChange={(event) => updateQuantity(item.id, event.target.value)}
                    className="w-24 rounded-lg border border-zinc-300 bg-white px-3 py-2 text-sm text-zinc-900 outline-none transition focus:border-zinc-900 focus:ring-4 focus:ring-zinc-200"
                  />
                </div>
              </li>
            ))}
          </ul>

          <div className="rounded-2xl border border-zinc-200 bg-zinc-50 p-5">
            <div className="mb-4 flex items-center justify-between gap-3">
              <p className="text-sm font-semibold uppercase tracking-wide text-zinc-500">Subtotal</p>
              <p className="text-2xl font-bold text-zinc-900">{currencyFormatter.format(subtotal)}</p>
            </div>

            <button
              type="button"
              disabled={processing}
              onClick={handleCheckout}
              className="w-full rounded-xl bg-zinc-900 px-4 py-3 text-sm font-semibold text-white transition hover:bg-black disabled:cursor-not-allowed disabled:bg-zinc-500"
            >
              {processing ? "Processing payment..." : "Make Payment"}
            </button>
          </div>
        </>
      )}
    </section>
  );
};

export default Cart;
