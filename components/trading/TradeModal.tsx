"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { AuthError, buyStock, sellStock } from "@/lib/api";

interface TradeModalProps {
  mode: "buy" | "sell";
  symbol: string;
  companyName?: string;
  price: number;
  balance?: number; // required for "buy"
  ownedQuantity?: number; // required for "sell"
  onClose: () => void;
  onSuccess: () => void; // parent refetches account/portfolio/transactions
}

export default function TradeModal({
  mode,
  symbol,
  companyName,
  price,
  balance,
  ownedQuantity,
  onClose,
  onSuccess,
}: TradeModalProps) {
  const router = useRouter();

  const [quantity, setQuantity] = useState(1);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isSuccess, setIsSuccess] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    function handleKey(e: KeyboardEvent) {
      if (e.key === "Escape") onClose();
    }
    window.addEventListener("keydown", handleKey);
    return () => window.removeEventListener("keydown", handleKey);
  }, [onClose]);

  const total = price * quantity;
  const isQuantityValid =
    Number.isInteger(quantity) &&
    quantity > 0 &&
    (mode === "sell" ? quantity <= (ownedQuantity ?? 0) : true);
  const isWithinBalance = mode === "buy" ? total <= (balance ?? 0) : true;
  const canSubmit = isQuantityValid && isWithinBalance && !isSubmitting && !isSuccess;

  function adjustQuantity(delta: number) {
    setQuantity((q) => {
      const next = q + delta;
      if (next < 1) return 1;
      if (mode === "sell" && ownedQuantity !== undefined && next > ownedQuantity) {
        return ownedQuantity;
      }
      return next;
    });
  }

  function handleQuantityInput(value: string) {
    const parsed = parseInt(value, 10);
    if (Number.isNaN(parsed)) {
      setQuantity(1);
      return;
    }
    if (mode === "sell" && ownedQuantity !== undefined && parsed > ownedQuantity) {
      setQuantity(ownedQuantity);
      return;
    }
    setQuantity(Math.max(1, parsed));
  }

  async function handleConfirm() {
    if (!canSubmit) return;

    const token = localStorage.getItem("tradesphere_token");
    if (!token) {
      router.replace("/");
      return;
    }

    setError(null);
    setIsSubmitting(true);

    try {
      if (mode === "buy") {
        await buyStock(token, symbol, quantity);
      } else {
        await sellStock(token, symbol, quantity);
      }
      setIsSuccess(true);
      setTimeout(() => {
        onSuccess();
        onClose();
      }, 700);
    } catch (err) {
      if (err instanceof AuthError) {
        localStorage.removeItem("tradesphere_token");
        router.replace("/");
        return;
      }
      setError(err instanceof Error ? err.message : "Something went wrong. Please try again.");
      setIsSubmitting(false);
    }
  }

  return (
    <div
      className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 px-4 backdrop-blur-sm"
      onClick={onClose}
    >
      <div
        role="dialog"
        aria-modal="true"
        aria-labelledby="trade-modal-title"
        onClick={(e) => e.stopPropagation()}
        className="w-full max-w-sm rounded-2xl border border-[var(--color-border)] bg-[var(--color-surface)] p-6 shadow-[0_20px_60px_rgba(0,0,0,0.5)]"
      >
        <div className="flex items-start justify-between">
          <div>
            <p
              id="trade-modal-title"
              className="font-[family-name:var(--font-display)] text-lg font-semibold text-[var(--color-text)]"
            >
              {mode === "buy" ? "Buy" : "Sell"} {symbol}
            </p>
            {companyName && <p className="text-xs text-[var(--color-text-faint)]">{companyName}</p>}
          </div>
          <button
            onClick={onClose}
            aria-label="Close"
            className="rounded-md p-1 text-[var(--color-text-faint)] transition-colors hover:text-[var(--color-text-dim)] focus:outline-none focus-visible:ring-2 focus-visible:ring-[var(--color-accent-glow)]"
          >
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8">
              <path d="M6 6l12 12M18 6L6 18" strokeLinecap="round" />
            </svg>
          </button>
        </div>

        <p className="mt-4 font-[family-name:var(--font-mono)] text-2xl font-semibold text-[var(--color-text)]">
          ${price.toFixed(2)}
          <span className="ml-1 text-sm font-normal text-[var(--color-text-faint)]">/ share</span>
        </p>

        <div className="mt-6">
          <label className="text-xs font-medium text-[var(--color-text-dim)]">Quantity</label>
          <div className="mt-1.5 flex items-center gap-3">
            <button
              type="button"
              onClick={() => adjustQuantity(-1)}
              disabled={quantity <= 1}
              aria-label="Decrease quantity"
              className="flex h-9 w-9 items-center justify-center rounded-lg border border-[var(--color-border)] text-[var(--color-text)] transition-colors hover:border-[var(--color-accent)] disabled:cursor-not-allowed disabled:opacity-30"
            >
              −
            </button>
            <input
              type="number"
              min={1}
              value={quantity}
              onChange={(e) => handleQuantityInput(e.target.value)}
              className="w-20 rounded-lg border border-[var(--color-border)] bg-[var(--color-surface-2)] px-2 py-1.5 text-center font-[family-name:var(--font-mono)] text-sm text-[var(--color-text)] outline-none focus:border-[var(--color-accent)] focus:ring-2 focus:ring-[var(--color-accent-glow)]"
            />
            <button
              type="button"
              onClick={() => adjustQuantity(1)}
              disabled={mode === "sell" && ownedQuantity !== undefined && quantity >= ownedQuantity}
              aria-label="Increase quantity"
              className="flex h-9 w-9 items-center justify-center rounded-lg border border-[var(--color-border)] text-[var(--color-text)] transition-colors hover:border-[var(--color-accent)] disabled:cursor-not-allowed disabled:opacity-30"
            >
              +
            </button>
          </div>
          {mode === "sell" && (
            <p className="mt-1.5 text-xs text-[var(--color-text-faint)]">You own {ownedQuantity} shares</p>
          )}
        </div>

        <div className="mt-5 flex items-center justify-between border-t border-[var(--color-border)] pt-4">
          <span className="text-sm text-[var(--color-text-dim)]">
            {mode === "buy" ? "Total" : "Estimated proceeds"}
          </span>
          <span className="font-[family-name:var(--font-mono)] text-lg font-semibold text-[var(--color-text)]">
            ${total.toFixed(2)}
          </span>
        </div>

        {mode === "buy" && balance !== undefined && (
          <div className="mt-2 flex items-center justify-between">
            <span className="text-xs text-[var(--color-text-faint)]">Available balance</span>
            <span className="font-[family-name:var(--font-mono)] text-xs text-[var(--color-text-dim)]">
              ${balance.toFixed(2)}
            </span>
          </div>
        )}

        {!isWithinBalance && (
          <p className="mt-2 text-xs text-[var(--color-danger)]">This exceeds your available balance.</p>
        )}

        {error && (
          <div
            role="alert"
            className="mt-4 rounded-lg border border-[var(--color-danger)]/30 bg-[var(--color-danger)]/10 px-3.5 py-2.5 text-sm text-[var(--color-danger)]"
          >
            {error}
          </div>
        )}

        {isSuccess && (
          <div
            role="status"
            className="mt-4 rounded-lg border border-[var(--color-accent)]/30 bg-[var(--color-accent)]/10 px-3.5 py-2.5 text-sm text-[var(--color-accent)]"
          >
            {mode === "buy" ? "Purchase complete." : "Sale complete."}
          </div>
        )}

        <button
          onClick={handleConfirm}
          disabled={!canSubmit}
          className="mt-5 flex w-full items-center justify-center gap-2 rounded-lg bg-[var(--color-accent)] px-4 py-2.5 text-sm font-semibold text-[#06170f] transition-all hover:brightness-110 disabled:cursor-not-allowed disabled:opacity-40 disabled:hover:brightness-100 focus:outline-none focus-visible:ring-2 focus-visible:ring-[var(--color-accent-glow)] focus-visible:ring-offset-2 focus-visible:ring-offset-[var(--color-surface)]"
        >
          {isSubmitting ? (
            <>
              <span className="h-4 w-4 animate-spin rounded-full border-2 border-[#06170f]/30 border-t-[#06170f]" />
              {mode === "buy" ? "Confirming purchase…" : "Confirming sale…"}
            </>
          ) : mode === "buy" ? (
            "Confirm Buy"
          ) : (
            "Confirm Sell"
          )}
        </button>
      </div>
    </div>
  );
}