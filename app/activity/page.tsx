"use client";

import { useEffect, useMemo, useState } from "react";
import { useRouter } from "next/navigation";
import { AuthError, getTransactions, type Transaction } from "@/lib/api";
import Navbar from "@/components/dashboard/Navbar";

type LoadState = "loading" | "error" | "ready";
type FilterOption = "ALL" | "BUY" | "SELL";

export default function ActivityPage() {
  const router = useRouter();

  const [state, setState] = useState<LoadState>("loading");
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [transactions, setTransactions] = useState<Transaction[]>([]);
  const [filter, setFilter] = useState<FilterOption>("ALL");

  useEffect(() => {
    const token = localStorage.getItem("tradesphere_token");
    if (!token) {
      router.replace("/");
      return;
    }
    load(token);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  async function load(token: string) {
    setState("loading");
    setErrorMessage(null);
    try {
      const data = await getTransactions(token);
      setTransactions(data);
      setState("ready");
    } catch (err) {
      if (err instanceof AuthError) {
        localStorage.removeItem("tradesphere_token");
        router.replace("/");
        return;
      }
      setErrorMessage(err instanceof Error ? err.message : "Something went wrong.");
      setState("error");
    }
  }

  function handleRetry() {
    const token = localStorage.getItem("tradesphere_token");
    if (!token) {
      router.replace("/");
      return;
    }
    load(token);
  }

  function handleLogout() {
    localStorage.removeItem("tradesphere_token");
    router.replace("/");
  }

  const filtered = useMemo(() => {
    if (filter === "ALL") return transactions;
    return transactions.filter((tx) => tx.type === filter);
  }, [transactions, filter]);

  return (
    <div className="min-h-screen bg-[var(--color-bg)]">
      <Navbar onLogout={handleLogout} />

      <main className="mx-auto max-w-4xl px-6 py-10">
        <div className="mb-6">
          <h1 className="font-[family-name:var(--font-display)] text-2xl font-semibold text-[var(--color-text)] sm:text-3xl">
            Activity
          </h1>
          <p className="mt-1 text-sm text-[var(--color-text-dim)]">Your trading history.</p>
        </div>

        {state === "ready" && (
          <div className="mb-6 flex gap-2">
            <FilterButton label="All" active={filter === "ALL"} onClick={() => setFilter("ALL")} />
            <FilterButton label="Buys" active={filter === "BUY"} onClick={() => setFilter("BUY")} />
            <FilterButton label="Sells" active={filter === "SELL"} onClick={() => setFilter("SELL")} />
          </div>
        )}

        {state === "loading" && <ActivitySkeleton />}

        {state === "error" && (
          <div className="flex flex-col items-center gap-4 rounded-2xl border border-[var(--color-border)] bg-[var(--color-surface)] px-6 py-16 text-center">
            <p className="text-sm text-[var(--color-danger)]">{errorMessage}</p>
            <button
              onClick={handleRetry}
              className="rounded-lg border border-[var(--color-border)] bg-[var(--color-surface-2)] px-4 py-2 text-sm font-medium text-[var(--color-text)] transition-colors hover:border-[var(--color-accent)] focus:outline-none focus-visible:ring-2 focus-visible:ring-[var(--color-accent-glow)]"
            >
              Try again
            </button>
          </div>
        )}

        {state === "ready" && filtered.length === 0 && (
          <div className="flex flex-col items-center gap-2 rounded-2xl border border-dashed border-[var(--color-border)] px-6 py-20 text-center">
            <p className="text-sm text-[var(--color-text-dim)]">No activity yet</p>
            <p className="text-xs text-[var(--color-text-faint)]">
              Your completed trades will appear here.
            </p>
          </div>
        )}

        {state === "ready" && filtered.length > 0 && (
          <div className="overflow-hidden rounded-2xl border border-[var(--color-border)] bg-[var(--color-surface)]">
            {/* Desktop table header */}
            <div className="hidden grid-cols-[80px_1fr_1fr_1fr_1fr_1.2fr] gap-4 border-b border-[var(--color-border)] px-6 py-3 text-xs font-medium uppercase tracking-wide text-[var(--color-text-faint)] sm:grid">
              <span>Type</span>
              <span>Symbol</span>
              <span className="text-right">Quantity</span>
              <span className="text-right">Price</span>
              <span className="text-right">Total</span>
              <span className="text-right">Date</span>
            </div>

            <div className="flex flex-col divide-y divide-[var(--color-border)]">
              {filtered.map((tx) => (
                <TransactionRow key={tx.id} transaction={tx} />
              ))}
            </div>
          </div>
        )}
      </main>
    </div>
  );
}

function FilterButton({
  label,
  active,
  onClick,
}: {
  label: string;
  active: boolean;
  onClick: () => void;
}) {
  return (
    <button
      onClick={onClick}
      aria-pressed={active}
      className={`rounded-lg border px-3.5 py-1.5 text-sm font-medium transition-colors focus:outline-none focus-visible:ring-2 focus-visible:ring-[var(--color-accent-glow)] ${
        active
          ? "border-[var(--color-accent)]/40 bg-[var(--color-accent)]/10 text-[var(--color-accent)]"
          : "border-[var(--color-border)] text-[var(--color-text-dim)] hover:border-[var(--color-border-strong)] hover:text-[var(--color-text)]"
      }`}
    >
      {label}
    </button>
  );
}

function TransactionRow({ transaction }: { transaction: Transaction }) {
  const isBuy = transaction.type === "BUY";
  const formattedDate = new Date(transaction.timestamp).toLocaleString(undefined, {
    dateStyle: "medium",
    timeStyle: "short",
  });

  return (
    <div className="flex flex-col gap-3 px-6 py-4 sm:grid sm:grid-cols-[80px_1fr_1fr_1fr_1fr_1.2fr] sm:items-center sm:gap-4">
      {/* Type + symbol row (always visible, primary on mobile) */}
      <div className="flex items-center gap-3 sm:contents">
        <span
          className={`w-fit rounded-md px-2 py-0.5 text-xs font-semibold sm:w-auto ${
            isBuy
              ? "bg-[var(--color-accent)]/10 text-[var(--color-accent)]"
              : "bg-[var(--color-danger)]/10 text-[var(--color-danger)]"
          }`}
        >
          {transaction.type}
        </span>
        <span className="font-[family-name:var(--font-mono)] text-sm font-medium text-[var(--color-text)] sm:hidden">
          {transaction.symbol}
        </span>
        <span className="hidden font-[family-name:var(--font-mono)] text-sm font-medium text-[var(--color-text)] sm:inline">
          {transaction.symbol}
        </span>
      </div>

      {/* Mobile: labeled stack. Desktop: grid cells via sm:contents above pulls type/symbol into the grid, these fill the rest */}
      <div className="grid grid-cols-2 gap-y-1 text-sm sm:contents">
        <span className="text-[var(--color-text-faint)] sm:hidden">Quantity</span>
        <span className="text-right font-[family-name:var(--font-mono)] text-[var(--color-text-dim)] sm:text-right">
          {transaction.quantity}
        </span>

        <span className="text-[var(--color-text-faint)] sm:hidden">Price</span>
        <span className="text-right font-[family-name:var(--font-mono)] text-[var(--color-text-dim)] sm:text-right">
          ${transaction.price.toFixed(2)}
        </span>

        <span className="text-[var(--color-text-faint)] sm:hidden">Total</span>
        <span className="text-right font-[family-name:var(--font-mono)] font-medium text-[var(--color-text)] sm:text-right">
          ${transaction.totalValue.toFixed(2)}
        </span>

        <span className="text-[var(--color-text-faint)] sm:hidden">Date</span>
        <span className="text-right font-[family-name:var(--font-mono)] text-xs text-[var(--color-text-faint)] sm:text-right sm:text-sm">
          {formattedDate}
        </span>
      </div>
    </div>
  );
}

function ActivitySkeleton() {
  return (
    <div className="overflow-hidden rounded-2xl border border-[var(--color-border)] bg-[var(--color-surface)]">
      <div className="flex flex-col divide-y divide-[var(--color-border)]">
        {[0, 1, 2, 3, 4].map((i) => (
          <div key={i} className="flex items-center gap-4 px-6 py-4">
            <div className="h-5 w-12 animate-pulse rounded-md bg-[var(--color-surface-2)]" />
            <div className="h-4 w-16 animate-pulse rounded bg-[var(--color-surface-2)]" />
            <div className="ml-auto h-4 w-20 animate-pulse rounded bg-[var(--color-surface-2)]" />
          </div>
        ))}
      </div>
    </div>
  );
}