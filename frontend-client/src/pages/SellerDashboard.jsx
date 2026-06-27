import { useEffect, useMemo, useState } from "react";
import { Link } from "react-router-dom";
import { getSellerDashboard } from "../api/dashboardApi";
import { getSellerProducts } from "../api/productApi";
import { useAuth } from "../context/AuthContext";

const currencyFormatter = new Intl.NumberFormat("en-IN", {
  style: "currency",
  currency: "INR",
  maximumFractionDigits: 2,
});

const SellerDashboard = () => {
  const { user } = useAuth();
  const sellerId = useMemo(() => user?.userId ?? user?.id ?? null, [user]);

  const [metrics, setMetrics] = useState(null);
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const fetchDashboard = async () => {
    if (!sellerId) {
      setError("Unable to resolve seller profile. Please sign in again.");
      setLoading(false);
      return;
    }

    setError("");
    setLoading(true);

    try {
      const [metricsResponse, productsResponse] = await Promise.all([
        getSellerDashboard(sellerId),
        getSellerProducts(sellerId),
      ]);

      setMetrics(metricsResponse?.data ?? null);
      setProducts(Array.isArray(productsResponse?.data) ? productsResponse.data : []);
    } catch (requestError) {
      setError(
        requestError?.response?.data?.message ||
          requestError?.message ||
          "Unable to load seller dashboard.",
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDashboard();
  }, [sellerId]);

  const totalRevenueNumber = Number(metrics?.totalRevenue);
  const totalRevenue = Number.isFinite(totalRevenueNumber)
    ? currencyFormatter.format(totalRevenueNumber)
    : currencyFormatter.format(0);

  return (
    <section className="space-y-6 rounded-3xl border border-zinc-200/80 bg-white p-6 shadow-[0_24px_60px_-24px_rgba(0,0,0,0.35)] sm:p-8">
      <header className="space-y-3">
        <span className="inline-flex items-center rounded-full border border-amber-200 bg-amber-50 px-3 py-1 text-xs font-semibold uppercase tracking-wide text-amber-700">
          Seller Hub
        </span>
        <h2 className="text-3xl font-bold tracking-tight text-zinc-900">
          Seller dashboard
        </h2>
        <p className="text-sm leading-6 text-zinc-600">
          Track your store performance and monitor your catalog in one place.
        </p>
      </header>

      {error && (
        <p className="rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
          {error}
        </p>
      )}

      {loading ? (
        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
          {Array.from({ length: 4 }).map((_, index) => (
            <div
              key={index}
              className="h-28 animate-pulse rounded-2xl border border-zinc-200 bg-zinc-100"
            />
          ))}
        </div>
      ) : (
        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
          <article className="rounded-2xl border border-zinc-200 bg-zinc-50 p-4">
            <p className="text-xs font-semibold uppercase tracking-wide text-zinc-500">
              Seller ID
            </p>
            <p className="mt-2 text-2xl font-bold text-zinc-900">{metrics?.sellerId ?? sellerId}</p>
          </article>
          <Link
            to="/seller/products"
            className="rounded-2xl border border-zinc-200 bg-zinc-50 p-4 transition hover:-translate-y-0.5 hover:border-zinc-300 hover:bg-white focus:outline-none focus:ring-4 focus:ring-zinc-200"
          >
            <p className="text-xs font-semibold uppercase tracking-wide text-zinc-500">
              Products
            </p>
            <p className="mt-2 text-2xl font-bold text-zinc-900">{metrics?.totalProducts ?? 0}</p>
          </Link>
          <article className="rounded-2xl border border-zinc-200 bg-zinc-50 p-4">
            <p className="text-xs font-semibold uppercase tracking-wide text-zinc-500">
              Orders
            </p>
            <p className="mt-2 text-2xl font-bold text-zinc-900">{metrics?.totalOrders ?? 0}</p>
          </article>
          <article className="rounded-2xl border border-zinc-200 bg-zinc-50 p-4">
            <p className="text-xs font-semibold uppercase tracking-wide text-zinc-500">
              Revenue
            </p>
            <p className="mt-2 text-2xl font-bold text-zinc-900">{totalRevenue}</p>
          </article>
        </div>
      )}

      <div className="rounded-2xl border border-zinc-200 bg-zinc-50 p-5">
        <div className="mb-3 flex items-center justify-between">
          <h3 className="text-lg font-semibold text-zinc-900">Your products</h3>
          <button
            type="button"
            onClick={fetchDashboard}
            className="rounded-xl border border-zinc-300 bg-white px-3 py-2 text-sm font-semibold text-zinc-900 transition hover:bg-zinc-100"
          >
            Refresh
          </button>
        </div>

        {!loading && products.length === 0 ? (
          <p className="text-sm text-zinc-600">No products found for this seller account.</p>
        ) : (
          <ul className="grid gap-2 sm:grid-cols-2">
            {products.map((product) => (
              <li
                key={product.id}
                className="rounded-xl border border-zinc-200 bg-white px-4 py-3 text-sm font-medium text-zinc-800"
              >
                {product.name}
              </li>
            ))}
          </ul>
        )}
      </div>
    </section>
  );
};

export default SellerDashboard;
