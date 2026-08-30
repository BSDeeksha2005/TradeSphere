import Link from "next/link";

interface NavbarProps {
  onLogout: () => void;
}

export default function Navbar({ onLogout }: NavbarProps) {
  return (
    <header className="sticky top-0 z-20 border-b border-[var(--color-border)] bg-[var(--color-bg)]/80 backdrop-blur-sm">
      <div className="mx-auto flex max-w-6xl items-center justify-between px-6 py-4">
        <div className="flex items-center gap-10">
          <Link
            href="/dashboard"
            className="font-[family-name:var(--font-display)] text-lg font-semibold tracking-tight text-[var(--color-text)]"
          >
            Trade<span className="text-[var(--color-accent)]">Sphere</span>
          </Link>
          <nav className="hidden items-center gap-6 md:flex">
            <Link
              href="/dashboard"
              className="text-sm font-medium text-[var(--color-text)] hover:text-[var(--color-accent)]"
            >
              Dashboard
            </Link>
            <Link
              href="/markets"
              className="text-sm text-[var(--color-text-faint)] hover:text-[var(--color-text-dim)]"
            >
              Markets
            </Link>
            <Link
              href="/activity"
              className="text-sm text-[var(--color-text-faint)] hover:text-[var(--color-text-dim)]"
            >
              Activity
            </Link>
          </nav>
        </div>

        <button
          onClick={onLogout}
          className="rounded-lg border border-[var(--color-border)] px-3.5 py-1.5 text-sm font-medium text-[var(--color-text-dim)] transition-colors hover:border-[var(--color-danger)]/40 hover:text-[var(--color-danger)] focus:outline-none focus-visible:ring-2 focus-visible:ring-[var(--color-accent-glow)]"
        >
          Log out
        </button>
      </div>
    </header>
  );
}