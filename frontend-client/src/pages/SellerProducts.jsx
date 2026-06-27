import { useEffect, useMemo, useState } from "react";
import { Link } from "react-router-dom";
import { getSellerProducts } from "../api/productApi";
import { useAuth } from "../context/AuthContext";

const SellerProducts = () => {
  const { user } = useAuth();
  const sellerId = useMemo(() => user?.userId ?? user?.id ?? null, [user]);

  const [products, setProducts] = useState([]);
  const [search, setSearch] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const fetchProducts = async () => {
    if (!sellerId) {
      setError("Unable to resolve seller profile. Please sign in again.");
      setLoading(false);
      return;
    }

    setError("");
    setLoading(true);

    try {
      const response = await getSellerProducts(sellerId);
      setProducts(Array.isArray(response?.data) ? response.data : []);
    } catch (requestError) {
      setError(
        requestError?.response?.data?.message ||
          requestError?.message ||
          "Unable to load seller products.",
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchProducts();
  }, [sellerId]);

  const filteredProducts = useMemo(() => {
    const normalized = search.trim().toLowerCase();
    if (!normalized) {
      return products;
    }

    return products.filter((product) => {
      const name = product?.name?.toLowerCase() || "";
      const id = String(product?.id ?? "").toLowerCase();
      return name.includes(normalized) || id.includes(normalized);
    });
  }, [products, search]);

  return (
    <section className="space-y-6 rounded-3xl border border-zinc-200/80 bg-white p-6 shadow-[0_24px_60px_-24px_rgba(0,0,0,0.35)] sm:p-8">
      <header className="space-y-3">
        <span className="inline-flex items-center rounded-full border border-amber-200 bg-amber-50 px-3 py-1 text-xs font-semibold uppercase tracking-wide text-amber-700">
          Seller Catalog
        </span>
        <h2 className="text-3xl font-bold tracking-tight text-zinc-900">
          Your products
        </h2>
        <p className="text-sm leading-6 text-zinc-600">
          View and search all products currently listed under your seller account.
        </p>
      </header>

      <div className="grid gap-3 sm:grid-cols-[1fr_auto] sm:items-center">
        <input
          type="text"
          value={search}
          onChange={(event) => setSearch(event.target.value)}
          placeholder="Search by product name or id"
          className="w-full rounded-xl border border-zinc-300 bg-white px-4 py-3 text-zinc-900 outline-none transition focus:border-zinc-900 focus:ring-4 focus:ring-zinc-200"
        />
        <div className="flex flex-wrap items-center gap-2 sm:justify-end">
          <button
            type="button"
            onClick={fetchProducts}
            className="inline-flex items-center justify-center rounded-xl border border-zinc-300 bg-white px-4 py-3 text-sm font-semibold text-zinc-900 transition hover:bg-zinc-100"
          >
            Refresh
          </button>
          <Link
            to="/seller/products/new"
            className="inline-flex items-center justify-center rounded-xl bg-zinc-900 px-4 py-3 text-sm font-semibold text-white transition hover:bg-black"
          >
            Add Product
          </Link>
        </div>
      </div>

      {error && (
        <p className="rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
          {error}
        </p>
      )}

      {loading ? (
        <div className="grid gap-3 sm:grid-cols-2 lg:grid-cols-3">
          {Array.from({ length: 6 }).map((_, index) => (
            <div
              key={index}
              className="h-24 animate-pulse rounded-2xl border border-zinc-200 bg-zinc-100"
            />
          ))}
        </div>
      ) : filteredProducts.length === 0 ? (
        <p className="rounded-xl border border-zinc-200 bg-zinc-50 px-4 py-6 text-sm text-zinc-600">
          No products found for this seller.
        </p>
      ) : (
        <ul className="grid gap-3 sm:grid-cols-2 lg:grid-cols-3">
          {filteredProducts.map((product) => (
            <li
              key={product.id}
              className="rounded-2xl border border-zinc-200 bg-zinc-50 p-4"
            >
              <p className="text-xs font-semibold uppercase tracking-wide text-zinc-500">
                Product ID
              </p>
              <p className="mt-1 text-sm font-semibold text-zinc-900">#{product.id}</p>
              <p className="mt-3 text-base font-medium text-zinc-800">{product.name}</p>
            </li>
          ))}
        </ul>
      )}
    </section>
  );
};

export default SellerProducts;
