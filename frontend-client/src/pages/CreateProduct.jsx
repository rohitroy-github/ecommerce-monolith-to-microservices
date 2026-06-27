import { useMemo, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { createProduct } from "../api/productApi";
import { useAuth } from "../context/AuthContext";

const CreateProduct = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const sellerId = useMemo(() => user?.userId ?? user?.id ?? null, [user]);

  const [name, setName] = useState("");
  const [description, setDescription] = useState("");
  const [price, setPrice] = useState("");
  const [quantity, setQuantity] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (event) => {
    event.preventDefault();

    if (!sellerId) {
      setError("Unable to resolve seller profile. Please sign in again.");
      return;
    }

    const parsedPrice = Number(price);
    const parsedQuantity = Number(quantity);

    if (!Number.isFinite(parsedPrice) || parsedPrice <= 0) {
      setError("Price must be a number greater than 0.");
      return;
    }

    if (!Number.isInteger(parsedQuantity) || parsedQuantity < 1) {
      setError("Quantity must be an integer of at least 1.");
      return;
    }

    setError("");
    setLoading(true);

    try {
      await createProduct({
        name: name.trim(),
        description: description.trim(),
        price: parsedPrice,
        sellerId,
        quantity: parsedQuantity,
      });
      navigate("/seller/products", { replace: true });
    } catch (requestError) {
      setError(
        requestError?.response?.data?.message ||
          requestError?.message ||
          "Unable to create product.",
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <section className="space-y-6 rounded-3xl border border-zinc-200/80 bg-white p-6 shadow-[0_24px_60px_-24px_rgba(0,0,0,0.35)] sm:p-8">
      <header className="space-y-3">
        <span className="inline-flex items-center rounded-full border border-amber-200 bg-amber-50 px-3 py-1 text-xs font-semibold uppercase tracking-wide text-amber-700">
          Seller Catalog
        </span>
        <h2 className="text-3xl font-bold tracking-tight text-zinc-900">
          Create product
        </h2>
        <p className="text-sm leading-6 text-zinc-600">
          Add a new product to your seller inventory.
        </p>
      </header>

      <form onSubmit={handleSubmit} className="grid gap-4">
        <label className="grid gap-2 text-sm font-medium text-zinc-700" htmlFor="product-name">
          Product name
          <input
            id="product-name"
            type="text"
            value={name}
            onChange={(event) => setName(event.target.value)}
            placeholder="Enter product name"
            className="w-full rounded-xl border border-zinc-300 bg-white px-4 py-3 text-zinc-900 outline-none transition focus:border-zinc-900 focus:ring-4 focus:ring-zinc-200"
            required
          />
        </label>

        <label className="grid gap-2 text-sm font-medium text-zinc-700" htmlFor="product-description">
          Description
          <textarea
            id="product-description"
            value={description}
            onChange={(event) => setDescription(event.target.value)}
            placeholder="Describe your product"
            className="min-h-28 w-full rounded-xl border border-zinc-300 bg-white px-4 py-3 text-zinc-900 outline-none transition focus:border-zinc-900 focus:ring-4 focus:ring-zinc-200"
          />
        </label>

        <div className="grid gap-4 sm:grid-cols-2">
          <label className="grid gap-2 text-sm font-medium text-zinc-700" htmlFor="product-price">
            Price
            <input
              id="product-price"
              type="number"
              min="0.01"
              step="0.01"
              value={price}
              onChange={(event) => setPrice(event.target.value)}
              placeholder="0.00"
              className="w-full rounded-xl border border-zinc-300 bg-white px-4 py-3 text-zinc-900 outline-none transition focus:border-zinc-900 focus:ring-4 focus:ring-zinc-200"
              required
            />
          </label>

          <label className="grid gap-2 text-sm font-medium text-zinc-700" htmlFor="product-quantity">
            Quantity
            <input
              id="product-quantity"
              type="number"
              min="1"
              step="1"
              value={quantity}
              onChange={(event) => setQuantity(event.target.value)}
              placeholder="1"
              className="w-full rounded-xl border border-zinc-300 bg-white px-4 py-3 text-zinc-900 outline-none transition focus:border-zinc-900 focus:ring-4 focus:ring-zinc-200"
              required
            />
          </label>
        </div>

        {error && (
          <p className="rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
            {error}
          </p>
        )}

        <div className="flex flex-wrap items-center gap-3">
          <button
            type="submit"
            disabled={loading}
            className="inline-flex items-center justify-center rounded-xl bg-zinc-900 px-5 py-3 text-sm font-semibold text-white transition hover:-translate-y-0.5 hover:bg-black disabled:translate-y-0 disabled:cursor-not-allowed disabled:bg-zinc-500"
          >
            {loading ? "Creating..." : "Create product"}
          </button>

          <Link
            to="/seller/products"
            className="inline-flex items-center justify-center rounded-xl border border-zinc-300 bg-white px-5 py-3 text-sm font-semibold text-zinc-900 transition hover:bg-zinc-100"
          >
            Cancel
          </Link>
        </div>
      </form>
    </section>
  );
};

export default CreateProduct;
