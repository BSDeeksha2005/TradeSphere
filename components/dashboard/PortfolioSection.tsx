"use client";

import { useState } from "react";
import type { Asset, PortfolioHolding } from "@/lib/api";
import TradeModal from "@/components/trading/TradeModal";

interface PortfolioSectionProps {
  holdings: PortfolioHolding[];
  assets: Asset[];
  onTradeComplete: () => void;
}

export default function PortfolioSection({ holdings, assets, onTradeComplete }: PortfolioSectionProps) {
  const [sellTarget, setSellTarget] = useState<{ holding: PortfolioHolding; price: number } | null>(null);

    function findPrice(symbol: string): number | undefined {
        return assets?.find((a) => a.symbol === symbol)?.price;
    }

  return (
    <div className="rounded-2xl border border-[var(--color-border)] bg-[var(--color-surface)] p-6">
      <h2 className="font-[family-name:var(--font-display)] text-lg font-semibold text-[var(--color-text)]">
        Portfolio
      </h2>

      {holdings.length === 0 ? (
        <div className="mt-6 flex flex-col items-center gap-2 rounded-xl border border-dashed border-[var(--color-border)] px-6 py-10 text-center">
          <p className="text-sm text-[var(--color-text-dim)]">You don&apos;t own any assets yet.</p>
          <p className="text-xs text-[var(--color-text-faint)]">Holdings you buy will show up here.</p>
        </div>
      ) : (
        <div className="mt-5 flex flex-col divide-y divide-[var(--color-border)]">
          {holdings.map((holding) => {
            const currentPrice = findPrice(holding.symbol);
            const currentValue =
              currentPrice !== undefined ? currentPrice * holding.quantity : undefined;

            return (
              <div key={holding.id} className="flex items-center justify-between py-3">
                <div>
                  <p className="font-[family-name:var(--font-mono)] text-sm font-medium text-[var(--color-text)]">
                    {holding.symbol}
                  </p>
                  <p className="text-xs text-[var(--color-text-faint)]">
                    {holding.quantity} shares · avg ${holding.averagePrice.toFixed(2)}
                  </p>
                </div>
                <div className="flex items-center gap-4">
                  {currentValue !== undefined && (
                    <p className="font-[family-name:var(--font-mono)] text-sm text-[var(--color-text-dim)]">
                      ${currentValue.toFixed(2)}
                    </p>
                  )}
                  <button
                    onClick={() =>
                      currentPrice !== undefined && setSellTarget({ holding, price: currentPrice })
                    }
                    disabled={currentPrice === undefined}
                    className="rounded-lg border border-[var(--color-border)] px-3 py-1 text-xs font-medium text-[var(--color-text)] transition-colors hover:border-[var(--color-danger)]/50 hover:text-[var(--color-danger)] disabled:cursor-not-allowed disabled:opacity-40 focus:outline-none focus-visible:ring-2 focus-visible:ring-[var(--color-accent-glow)]"
                  >
                    Sell
                  </button>
                </div>
              </div>
            );
          })}
        </div>
      )}

      {sellTarget && (
        <TradeModal
          mode="sell"
          symbol={sellTarget.holding.symbol}
          price={sellTarget.price}
          ownedQuantity={sellTarget.holding.quantity}
          onClose={() => setSellTarget(null)}
          onSuccess={onTradeComplete}
        />
      )}
    </div>
  );
}