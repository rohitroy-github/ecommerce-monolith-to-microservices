import { useEffect, useState } from "react";
import { getAdminDashboard } from "../api/dashboardApi";

const AdminDashboard = () => {
  const [dashboard, setDashboard] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const fetchDashboard = async () => {
    setError("");
    setLoading(true);

    try {
      const response = await getAdminDashboard();
      setDashboard(response?.data ?? null);
    } catch (requestError) {
      setError(
        requestError?.response?.data?.message ||
          requestError?.message ||
          "Unable to load admin dashboard.",
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDashboard();
  }, []);

  const sellerRows = Array.isArray(dashboard?.sellers) ? dashboard.sellers : [];

  return (
    <section className="space-y-6 rounded-3xl border border-zinc-200/80 bg-white p-6 shadow-[0_24px_60px_-24px_rgba(0,0,0,0.35)] sm:p-8">
      <header className="space-y-3">
        <span className="inline-flex items-center rounded-full border border-amber-200 bg-amber-50 px-3 py-1 text-xs font-semibold uppercase tracking-wide text-amber-700">
          Admin Control
        </span>
        <h2 className="text-3xl font-bold tracking-tight text-zinc-900">
          Admin dashboard
        </h2>
        <p className="text-sm leading-6 text-zinc-600">
          Review seller performance and system-wide commerce totals from one page.
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
              Total orders
            </p>
            <p className="mt-2 text-2xl font-bold text-zinc-900">{dashboard?.totalOrders ?? 0}</p>
          </article>
          <article className="rounded-2xl border border-zinc-200 bg-zinc-50 p-4">
            <p className="text-xs font-semibold uppercase tracking-wide text-zinc-500">
              Total customers
            </p>
            <p className="mt-2 text-2xl font-bold text-zinc-900">{dashboard?.totalCustomers ?? 0}</p>
          </article>
          <article className="rounded-2xl border border-zinc-200 bg-zinc-50 p-4">
            <p className="text-xs font-semibold uppercase tracking-wide text-zinc-500">
              Total sellers
            </p>
            <p className="mt-2 text-2xl font-bold text-zinc-900">{dashboard?.totalSellers ?? 0}</p>
          </article>
          <article className="rounded-2xl border border-zinc-200 bg-zinc-50 p-4">
            <p className="text-xs font-semibold uppercase tracking-wide text-zinc-500">
              Total products
            </p>
            <p className="mt-2 text-2xl font-bold text-zinc-900">{dashboard?.totalProducts ?? 0}</p>
          </article>
        </div>
      )}

      <div className="rounded-2xl border border-zinc-200 bg-zinc-50 p-5">
        <div className="mb-4 flex flex-wrap items-center justify-between gap-3">
          <div>
            <h3 className="text-lg font-semibold text-zinc-900">Seller overview</h3>
            <p className="text-sm text-zinc-600">
              Order and listing counts for each seller account.
            </p>
          </div>
          <button
            type="button"
            onClick={fetchDashboard}
            className="rounded-xl border border-zinc-300 bg-white px-3 py-2 text-sm font-semibold text-zinc-900 transition hover:bg-zinc-100"
          >
            Refresh
          </button>
        </div>

        {!loading && sellerRows.length === 0 ? (
          <p className="text-sm text-zinc-600">No seller accounts found.</p>
        ) : (
          <div className="overflow-x-auto">
            <table className="min-w-full divide-y divide-zinc-200 text-left text-sm text-zinc-700">
              <thead>
                <tr className="text-xs uppercase tracking-wide text-zinc-500">
                  <th className="px-3 py-3 font-semibold">Seller</th>
                  <th className="px-3 py-3 font-semibold">Email</th>
                  <th className="px-3 py-3 font-semibold">Orders</th>
                  <th className="px-3 py-3 font-semibold">Products</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-zinc-200 bg-white">
                {sellerRows.map((seller) => (
                  <tr key={seller.sellerId}>
                    <td className="px-3 py-3 font-medium text-zinc-900">
                      {seller.sellerName || `Seller #${seller.sellerId}`}
                    </td>
                    <td className="px-3 py-3">{seller.sellerEmail || "Not available"}</td>
                    <td className="px-3 py-3">{seller.totalOrders ?? 0}</td>
                    <td className="px-3 py-3">{seller.totalProducts ?? 0}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </section>
  );
};

export default AdminDashboard;