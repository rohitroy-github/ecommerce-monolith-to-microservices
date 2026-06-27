import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

const Register = () => {
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [role, setRole] = useState("CUSTOMER");
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [loading, setLoading] = useState(false);

  const { register } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError("");
    setSuccess("");
    setLoading(true);

    try {
      await register({ name, email, password, role });
      setSuccess("Successfully registered. Redirecting to login...");
      setTimeout(() => {
        navigate("/login", { replace: true });
      }, 1500);
    } catch (requestError) {
      setError(
        requestError?.response?.data?.message ||
          requestError?.message ||
          "Registration failed.",
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <main className="flex min-h-screen items-center justify-center">
      <section className="w-full max-w-md rounded-3xl border border-zinc-200/80 bg-white p-7 shadow-[0_24px_60px_-24px_rgba(0,0,0,0.35)] sm:p-8">
        <div className="mb-7">
          <span className="inline-flex items-center rounded-full border border-amber-200 bg-amber-50 px-3 py-1 text-xs font-semibold uppercase tracking-wide text-amber-700">
            Secure Access
          </span>
          <h2 className="mt-4 text-3xl font-bold tracking-tight text-zinc-900">Create account</h2>
          <p className="mt-2 text-sm leading-6 text-zinc-600">
            Register as a customer or seller to start using the platform.
          </p>
        </div>

        <form onSubmit={handleSubmit} className="grid gap-4">
          <label className="grid gap-2 text-sm font-medium text-zinc-700" htmlFor="register-name">
            Name
            <input
              id="register-name"
              type="text"
              placeholder="Your full name"
              value={name}
              onChange={(event) => setName(event.target.value)}
              className="w-full rounded-xl border border-zinc-300 bg-white px-4 py-3 text-zinc-900 outline-none transition focus:border-zinc-900 focus:ring-4 focus:ring-zinc-200"
              required
            />
          </label>

          <label className="grid gap-2 text-sm font-medium text-zinc-700" htmlFor="register-email">
            Email
            <input
              id="register-email"
              type="email"
              placeholder="you@example.com"
              value={email}
              onChange={(event) => setEmail(event.target.value)}
              className="w-full rounded-xl border border-zinc-300 bg-white px-4 py-3 text-zinc-900 outline-none transition focus:border-zinc-900 focus:ring-4 focus:ring-zinc-200"
              required
            />
          </label>

          <label className="grid gap-2 text-sm font-medium text-zinc-700" htmlFor="register-password">
            Password
            <div className="relative">
              <input
                id="register-password"
                type={showPassword ? "text" : "password"}
                placeholder="Create a password"
                value={password}
                onChange={(event) => setPassword(event.target.value)}
                className="w-full rounded-xl border border-zinc-300 bg-white px-4 py-3 pr-12 text-zinc-900 outline-none transition focus:border-zinc-900 focus:ring-4 focus:ring-zinc-200"
                required
              />
              <button
                type="button"
                onClick={() => setShowPassword((current) => !current)}
                className="absolute inset-y-0 right-0 inline-flex items-center justify-center px-3 text-zinc-500 transition hover:text-zinc-800"
                aria-label={showPassword ? "Hide password" : "Show password"}
              >
                {showPassword ? (
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    strokeWidth="1.8"
                    className="h-5 w-5"
                    aria-hidden="true"
                  >
                    <path strokeLinecap="round" strokeLinejoin="round" d="M3 3l18 18" />
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      d="M10.58 10.58A2 2 0 0012 14a2 2 0 001.42-.58M9.88 5.09A9.77 9.77 0 0112 5c4.45 0 8.26 2.92 9.54 7a9.77 9.77 0 01-4.06 5.09M6.1 6.1A9.75 9.75 0 002.46 12a9.75 9.75 0 005.94 6.91"
                    />
                  </svg>
                ) : (
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    strokeWidth="1.8"
                    className="h-5 w-5"
                    aria-hidden="true"
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      d="M2.46 12C3.74 7.92 7.55 5 12 5s8.26 2.92 9.54 7c-1.28 4.08-5.09 7-9.54 7s-8.26-2.92-9.54-7z"
                    />
                    <circle cx="12" cy="12" r="3" />
                  </svg>
                )}
              </button>
            </div>
          </label>

          <label className="grid gap-2 text-sm font-medium text-zinc-700" htmlFor="register-role">
            Account type
            <select
              id="register-role"
              value={role}
              onChange={(event) => setRole(event.target.value)}
              className="w-full rounded-xl border border-zinc-300 bg-white px-4 py-3 text-zinc-900 outline-none transition focus:border-zinc-900 focus:ring-4 focus:ring-zinc-200"
            >
              <option value="CUSTOMER">Register as CUSTOMER</option>
              <option value="SELLER">Register as SELLER</option>
            </select>
          </label>

          <button
            type="submit"
            disabled={loading}
            className="mt-2 inline-flex items-center justify-center rounded-xl bg-zinc-900 px-4 py-3 text-sm font-semibold text-white transition hover:-translate-y-0.5 hover:bg-black disabled:translate-y-0 disabled:cursor-not-allowed disabled:bg-zinc-500"
          >
            {loading ? "Creating account..." : "Register"}
          </button>
        </form>

        {error && (
          <p className="mt-4 rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
            {error}
          </p>
        )}

        {success && (
          <p className="mt-4 rounded-xl border border-emerald-200 bg-emerald-50 px-4 py-3 text-sm text-emerald-700">
            {success}
          </p>
        )}

        <p className="mt-6 text-sm text-zinc-700">
          Already have an account?{" "}
          <Link
            to="/login"
            className="font-semibold text-zinc-900 decoration-2 underline-offset-4 transition hover:text-zinc-700"
          >
            Login
          </Link>
        </p>
      </section>
    </main>
  );
};

export default Register;
