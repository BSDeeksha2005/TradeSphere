import type { Account } from "@/lib/api";

interface BalanceCardProps {
  account: Account;
}

export default function BalanceCard({ account }: BalanceCardProps) {
  const formattedBalance = new Intl.NumberFormat("en-US", {
    style: "currency",
    currency: "USD",
  }).format(account.balance);

  return (
    <div className="relative overflow-hidden rounded-2xl border border-[var(--color-border)] bg-[var(--color-surface)] px-8 py-7">
      <div
        className="animate-glow pointer-events-none absolute -right-24 -top-24 h-64 w-64 rounded-full blur-[100px]"
        style={{ background: "var(--color-accent-glow)" }}
        aria-hidden="true"
      />
      <div className="relative z-10 flex flex-col gap-1">
        <span className="text-xs font-medium uppercase tracking-wide text-[var(--color-text-dim)]">
          Available balance
        </span>
        <span className="font-[family-name:var(--font-mono)] text-4xl font-semibold text-[var(--color-text)] sm:text-5xl">
          {formattedBalance}
        </span>
        <span className="mt-2 text-sm text-[var(--color-text-faint)]">
          {account.email} · {account.role}
        </span>
      </div>
    </div>
  );
}