import Link from "next/link";
import type { Asset } from "@/lib/api";

interface MarketSectionProps {
  assets: Asset[];
}

export default function MarketSection({ assets }: MarketSectionProps) {
  return (
    <div className="rounded-2xl border border-[var(--color-border)] bg-[var(--color-surface)] p-6">
      <h2 className="font-[family-name:var(--font-display)] text-lg font-semibold text-[var(--color-text)]">
        Markets
      </h2>

      {assets.length === 0 ? (
        <p className="mt-6 text-sm text-[var(--color-text-dim)]">No assets are available right now.</p>
      ) : (
        <div className="mt-5 flex flex-col divide-y divide-[var(--color-border)]">
          {assets.map((asset) => (
            <div key={asset.id} className="flex items-center justify-between py-2.5">
              <div>
                <p className="font-[family-name:var(--font-mono)] text-sm font-medium text-[var(--color-text)]">
                  {asset.symbol}
                </p>
                <p className="text-xs text-[var(--color-text-faint)]">{asset.name}</p>
              </div>
              <p className="font-[family-name:var(--font-mono)] text-sm text-[var(--color-accent)]">
                ${asset.price.toFixed(2)}
              </p>
            </div>
          ))}
        </div>
      )}

      <Link
        href="/markets"
        className="mt-5 inline-block text-sm font-medium text-[var(--color-accent)] hover:underline"
      >
        View all markets →
      </Link>
    </div>
  );
}