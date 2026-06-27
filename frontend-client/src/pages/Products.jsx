import { useEffect, useMemo, useState } from "react";
import ProductCard from "../components/ProductCard";
import { getProducts } from "../api/productApi";

const Products = () => {
  const [products, setProducts] = useState([]);
  const [search, setSearch] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const fetchProducts = async () => {
    setError("");
    setLoading(true);

    try {
      const response = await getProducts();
      setProducts(Array.isArray(response?.data) ? response.data : []);
    } catch (requestError) {
      setError(
        requestError?.response?.data?.message ||
          requestError?.message ||
          "Unable to load products.",
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchProducts();
  }, []);

  const filteredProducts = useMemo(() => {
    const normalizedSearch = search.trim().toLowerCase();

    if (!normalizedSearch) {
      return products;
    }

    return products.filter((product) => {
      const name = product?.name?.toLowerCase() || "";
      const description = product?.description?.toLowerCase() || "";
      return (
        name.includes(normalizedSearch) ||
        description.includes(normalizedSearch)
      );
    });
  }, [products, search]);

  return (
    <section className="space-y-6 rounded-3xl border border-zinc-200/80 bg-white p-6 shadow-[0_24px_60px_-24px_rgba(0,0,0,0.35)] sm:p-8">
      <header className="space-y-3">
        <span className="inline-flex items-center rounded-full border border-amber-200 bg-amber-50 px-3 py-1 text-xs font-semibold uppercase tracking-wide text-amber-700">
          Product Catalog
        </span>
        <h2 className="text-3xl font-bold tracking-tight text-zinc-900">
          Browse products
        </h2>
        <p className="text-sm leading-6 text-zinc-600">
          Find items from the latest catalog and explore details before placing
          your order.
        </p>
      </header>

      <div className="grid gap-3 sm:grid-cols-[1fr_auto] sm:items-center">
        <input
          type="text"
          value={search}
          onChange={(event) => setSearch(event.target.value)}
          placeholder="Search by name or description"
          className="w-full rounded-xl border border-zinc-300 bg-white px-4 py-3 text-zinc-900 outline-none transition focus:border-zinc-900 focus:ring-4 focus:ring-zinc-200"
        />
        <button
          type="button"
          onClick={fetchProducts}
          className="inline-flex items-center justify-center rounded-xl border border-zinc-300 bg-white px-4 py-3 text-sm font-semibold text-zinc-900 transition hover:bg-zinc-100"
        >
          Refresh
        </button>
      </div>

      {error && (
        <p className="rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
          {error}
        </p>
      )}

      {!loading && !error && filteredProducts.length === 0 && (
        <p className="rounded-xl border border-zinc-200 bg-zinc-50 px-4 py-6 text-sm text-zinc-600">
          No products found for your search.
        </p>
      )}

      {loading ? (
        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
          {Array.from({ length: 6 }).map((_, index) => (
            <div
              key={index}
              className="h-40 animate-pulse rounded-2xl border border-zinc-200 bg-zinc-100"
            />
          ))}
        </div>
      ) : (
        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
          {filteredProducts.map((product) => (
            <ProductCard key={product.id} product={product} />
          ))}
        </div>
      )}
    </section>
  );
};

export default Products;
