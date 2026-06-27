import { Link } from "react-router-dom";

const Home = () => {
  return (
    <div className="flex min-h-screen items-center justify-center">
      <section className="w-full max-w-2xl p-8 text-center sm:p-10">
        <h1 className="text-4xl font-bold tracking-tight text-zinc-900 sm:text-5xl">
          Ecommerce Platform
        </h1>
        <p className="mt-4 text-base leading-7 text-zinc-600 sm:text-lg">
          A backend heavy project powered by SpringBoot microservices
        </p>
        <div className="mt-8">
          <Link
            to="/products"
            className="inline-flex items-center justify-center rounded-full bg-zinc-900 px-6 py-3 text-sm font-semibold text-white transition hover:bg-black"
          >
            View Product Inventory
          </Link>
        </div>
      </section>
    </div>
  );
};

export default Home;
