"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";

import {
  AuthError,
  getAccount,
  getAssets,
  getPortfolio,
  getTransactions,
  type Account,
  type Asset,
  type PortfolioHolding,
  type Transaction,
} from "@/lib/api";

import Navbar from "@/components/dashboard/Navbar";
import BalanceCard from "@/components/dashboard/BalanceCard";
import PortfolioSection from "@/components/dashboard/PortfolioSection";
import MarketSection from "@/components/dashboard/MarketSection";
import ActivitySection from "@/components/dashboard/ActivitySection";
import DashboardSkeleton from "@/components/dashboard/DashboardSkeleton";

type LoadState = "loading" | "error" | "ready";

export default function DashboardPage() {
  const router = useRouter();

  const [state, setState] = useState<LoadState>("loading");
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [account, setAccount] = useState<Account | null>(null);
  const [assets, setAssets] = useState<Asset[]>([]);
  const [portfolio, setPortfolio] = useState<PortfolioHolding[]>([]);
  const [transactions, setTransactions] = useState<Transaction[]>([]);

  useEffect(() => {
    const token = localStorage.getItem("tradesphere_token");

    if (!token) {
      router.replace("/");
      return;
    }

    loadDashboard(token);

    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  async function loadDashboard(token: string) {
    setState("loading");
    setErrorMessage(null);

    try {
      const [
        accountData,
        assetsData,
        portfolioData,
        transactionsData,
      ] = await Promise.all([
        getAccount(token),
        getAssets(token),
        getPortfolio(token),
        getTransactions(token),
      ]);

      setAccount(accountData);
      setAssets(assetsData);
      setPortfolio(portfolioData);
      setTransactions(transactionsData);
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

  function handleLogout() {
    localStorage.removeItem("tradesphere_token");
    router.replace("/");
  }

  function handleRetry() {
    const token = localStorage.getItem("tradesphere_token");

    if (!token) {
      router.replace("/");
      return;
    }

    loadDashboard(token);
  }

  function handleTradeComplete() {
    const token = localStorage.getItem("tradesphere_token");

    if (token) {
      loadDashboard(token);
    }
  }

  if (state === "loading") {
    return (
      <div className="min-h-screen bg-[var(--color-bg)]">
        <Navbar onLogout={handleLogout} />
        <DashboardSkeleton />
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
            onClick={handleRetry}
            className="rounded-lg border border-[var(--color-border)] bg-[var(--color-surface-2)] px-4 py-2 text-sm font-medium text-[var(--color-text)] transition-colors hover:border-[var(--color-accent)] focus:outline-none focus-visible:ring-2 focus-visible:ring-[var(--color-accent-glow)]"
          >
            Try again
          </button>
        </div>
      </div>
    );
  }

  const greeting = getGreeting();

  return (
    <div className="min-h-screen bg-[var(--color-bg)]">
      <Navbar onLogout={handleLogout} />

      <main className="mx-auto max-w-6xl px-6 py-10">
        <div className="mb-8">
          <h1 className="font-[family-name:var(--font-display)] text-2xl font-semibold text-[var(--color-text)] sm:text-3xl">
            {greeting}
            {account ? `, ${account.name.split(" ")[0]}` : ""}
          </h1>

          <p className="mt-1 text-sm text-[var(--color-text-dim)]">
            Here's where your simulated portfolio stands today.
          </p>
        </div>

        {account && <BalanceCard account={account} />}

        <div className="mt-8 grid grid-cols-1 gap-6 lg:grid-cols-3">
          <div className="lg:col-span-2">
            <PortfolioSection
              holdings={portfolio}
              assets={assets}
              onTradeComplete={handleTradeComplete}
            />
          </div>

          <div>
            <MarketSection assets={assets} />
          </div>
        </div>

        <div className="mt-6">
          <ActivitySection transactions={transactions} />
        </div>
      </main>
    </div>
  );
}

function getGreeting(): string {
  const hour = new Date().getHours();

  if (hour < 12) return "Good morning";
  if (hour < 18) return "Good afternoon";

  return "Good evening";
}