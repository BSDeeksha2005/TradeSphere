"use client";

import { useEffect, useMemo, useState } from "react";
import { useRouter } from "next/navigation";

import {
  AuthError,
  getAccount,
  getAssets,
  type Account,
  type Asset,
} from "@/lib/api";

import Navbar from "@/components/dashboard/Navbar";
import TradeModal from "@/components/trading/TradeModal";

type LoadState = "loading" | "error" | "ready";

export default function MarketsPage() {
  const router = useRouter();

  const [state, setState] = useState<LoadState>("loading");
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [assets, setAssets] = useState<Asset[]>([]);
  const [account, setAccount] = useState<Account | null>(null);
  const [selectedAsset, setSelectedAsset] = useState<Asset | null>(null);
  const [search, setSearch] = useState("");

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
      const [assetsData, accountData] = await Promise.all([
        getAssets(token),
        getAccount(token),
      ]);

      setAssets(assetsData);
      setAccount(accountData);
      setState("ready");
    } catch (err) {
      if (err instanceof AuthError) {
        localStorage.removeItem("tradesphere_token");
        router.replace("/");
        return;
      }

      setErrorMessage(
        err instanceof Error ? err.message : "Something went wrong."
      );
      setState("error");
    }
  }

  async function refreshAccount() {
    const token = localStorage.getItem("tradesphere_token");

    if (!token) {
      router.replace("/");
      return;
    }

    try {
      setAccount(await getAccount(token));
    } catch (err) {
      if (err instanceof AuthError) {
        localStorage.removeItem("tradesphere_token");
        router.replace("/");
      }
    }
  }

  function handleLogout() {
    localStorage.removeItem("tradesphere_token");
    router.replace("/");
  }

  const filteredAssets = useMemo(() => {
    const query = search.trim().toLowerCase();

    if (!query) {
      return assets;
    }

    return assets.filter(
      (asset) =>
        asset.symbol.toLowerCase().includes(query) ||
        asset.name.toLowerCase().includes(query)
    );
  }, [assets, search]);

  if (state === "loading") {
    return (
      <div className="min-h-screen bg-[var(--color-bg)]">
        <Navbar onLogout={handleLogout} />

        <main className="mx-auto max-w-6xl px-6 py-10">
          <div className="h-9 w-48 animate-pulse rounded-lg bg-[var(--color-surface-2)]" />

          <div className="mt-3 h-4 w-80 animate-pulse rounded bg-[var(--color-surface-2)]" />

          <div className="mt-8 h-12 w-full animate-pulse rounded-xl bg-[var(--color-surface-2)]" />

          <div className="mt-4 h-20 w-full animate-pulse rounded-xl bg-[var(--color-surface-2)]" />

          <div className="mt-2 h-20 w-full animate-pulse rounded-xl bg-[var(--color-surface-2)]" />
        </main>
      </div>
    );
  }

  if (state === "error") {
    return (
      <div className="min-h-screen bg-[var(--color-bg)]">
        <Navbar onLogout={handleLogout} />

        <div className="mx-auto flex max-w-3xl flex-col items-center gap-4 px-6 py-24 text-center">
          <p className="text-sm text-[var(--color-danger)]">
            {errorMessage}
          </p>

          <button
            onClick={() => {
              const token = localStorage.getItem("tradesphere_token");

              if (token) {
                load(token);
              }
            }}
            className="rounded-lg border border-[var(--color-border)] bg-[var(--color-surface-2)] px-4 py-2 text-sm font-medium text-[var(--color-text)] transition-colors hover:border-[var(--color-accent)] focus:outline-none focus-visible:ring-2 focus-visible:ring-[var(--color-accent-glow)]"
          >
            Try again
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-[var(--color-bg)]">
      <Navbar onLogout={handleLogout} />

      <main className="mx-auto max-w-6xl px-6 py-10">
        <div className="flex flex-col justify-between gap-5 sm:flex-row sm:items-end">
          <div>
            <p className="font-[family-name:var(--font-mono)] text-xs uppercase tracking-[0.18em] text-[var(--color-accent)]">
              Market
            </p>

            <h1 className="mt-2 font-[family-name:var(--font-display)] text-3xl font-semibold text-[var(--color-text)]">
              Explore markets
            </h1>

            <p className="mt-1 text-sm text-[var(--color-text-dim)]">
              Browse available assets and place simulated trades.
            </p>
          </div>

          {account && (
            <div className="rounded-xl border border-[var(--color-border)] bg-[var(--color-surface)] px-4 py-3">
              <p className="text-xs text-[var(--color-text-faint)]">
                Available balance
              </p>

              <p className="mt-1 font-[family-name:var(--font-mono)] text-sm font-semibold text-[var(--color-text)]">
                ${account.balance.toFixed(2)}
              </p>
            </div>
          )}
        </div>

        <div className="mt-8">
          <div className="relative">
            <svg
              className="pointer-events-none absolute left-4 top-1/2 -translate-y-1/2 text-[var(--color-text-faint)]"
              width="17"
              height="17"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              strokeWidth="1.8"
            >
              <circle cx="11" cy="11" r="6.5" />
              <path d="m16 16 4 4" strokeLinecap="round" />
            </svg>

            <input
              type="search"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              placeholder="Search by symbol or company..."
              className="w-full rounded-xl border border-[var(--color-border)] bg-[var(--color-surface)] py-3 pl-11 pr-4 text-sm text-[var(--color-text)] outline-none transition-colors placeholder:text-[var(--color-text-faint)] focus:border-[var(--color-accent)] focus:ring-2 focus:ring-[var(--color-accent-glow)]"
            />
          </div>
        </div>

        <div className="mt-5 overflow-hidden rounded-2xl border border-[var(--color-border)] bg-[var(--color-surface)]">
          <div className="grid grid-cols-[1fr_auto_auto] items-center border-b border-[var(--color-border)] px-6 py-3 text-xs font-medium uppercase tracking-wider text-[var(--color-text-faint)]">
            <span>Asset</span>
            <span>Price</span>
            <span className="ml-6">Action</span>
          </div>

          {filteredAssets.length === 0 ? (
            <div className="flex flex-col items-center px-6 py-16 text-center">
              <p className="text-sm text-[var(--color-text-dim)]">
                No assets found.
              </p>

              {search && (
                <button
                  onClick={() => setSearch("")}
                  className="mt-2 text-xs text-[var(--color-accent)] hover:underline"
                >
                  Clear search
                </button>
              )}
            </div>
          ) : (
            <div className="divide-y divide-[var(--color-border)]">
              {filteredAssets.map((asset) => (
                <div
                  key={asset.id}
                  className="grid grid-cols-[1fr_auto_auto] items-center px-6 py-4 transition-colors hover:bg-[var(--color-surface-2)]"
                >
                  <div className="min-w-0">
                    <p className="font-[family-name:var(--font-mono)] text-sm font-semibold text-[var(--color-text)]">
                      {asset.symbol}
                    </p>

                    <p className="mt-0.5 truncate text-xs text-[var(--color-text-faint)]">
                      {asset.name}
                    </p>
                  </div>

                  <p className="font-[family-name:var(--font-mono)] text-sm font-medium text-[var(--color-accent)]">
                    ${asset.price.toFixed(2)}
                  </p>

                  <button
                    onClick={() => setSelectedAsset(asset)}
                    className="ml-6 rounded-lg bg-[var(--color-accent)] px-4 py-2 text-xs font-semibold text-[#06170f] transition-all hover:brightness-110 focus:outline-none focus-visible:ring-2 focus-visible:ring-[var(--color-accent-glow)]"
                  >
                    Buy
                  </button>
                </div>
              ))}
            </div>
          )}
        </div>

        <p className="mt-4 text-xs text-[var(--color-text-faint)]">
          {filteredAssets.length}{" "}
          {filteredAssets.length === 1 ? "asset" : "assets"} available
        </p>
      </main>

      {selectedAsset && account && (
        <TradeModal
          mode="buy"
          symbol={selectedAsset.symbol}
          companyName={selectedAsset.name}
          price={selectedAsset.price}
          balance={account.balance}
          onClose={() => setSelectedAsset(null)}
          onSuccess={refreshAccount}
        />
      )}
    </div>
  );
}