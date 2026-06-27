const Footer = () => {
  return (
    <footer className="border-t border-zinc-200 bg-white">
      <div className="mx-auto w-full max-w-5xl px-4 py-4 text-center text-sm text-zinc-600 sm:px-6 lg:px-8">
        Created by Rohit Roy in 2026. Visit project at{" "}
        <a
          href="https://github.com/rohitroy-github/ecommerce-monolith-to-microservices"
          target="_blank"
          rel="noreferrer"
          className="font-semibold text-zinc-900 underline decoration-zinc-400 underline-offset-4 transition hover:text-black"
        >
          Link
        </a>
      </div>
    </footer>
  );
};

export default Footer;
