import type { Transaction } from "@/lib/api";

interface ActivitySectionProps {
  transactions: Transaction[];
}

export default function ActivitySection({ transactions }: ActivitySectionProps) {
  const recent = transactions.slice(0, 5);

  return (
    <div className="rounded-2xl border border-[var(--color-border)] bg-[var(--color-surface)] p-6">
      <h2 className="font-[family-name:var(--font-display)] text-lg font-semibold text-[var(--color-text)]">
        Recent activity
      </h2>

      {recent.length === 0 ? (
        <div className="mt-6 flex flex-col items-center gap-2 rounded-xl border border-dashed border-[var(--color-border)] px-6 py-10 text-center">
          <p className="text-sm text-[var(--color-text-dim)]">No activity yet.</p>
          <p className="text-xs text-[var(--color-text-faint)]">
            Your trades will appear here as you make them.
          </p>
        </div>
      ) : (
        <div className="mt-5 flex flex-col divide-y divide-[var(--color-border)]">
          {recent.map((tx) => (
            <div key={tx.id} className="flex items-center justify-between py-3">
              <div className="flex items-center gap-3">
                <span
                  className={`rounded-md px-2 py-0.5 text-xs font-semibold ${
                    tx.type === "BUY"
                      ? "bg-[var(--color-accent)]/10 text-[var(--color-accent)]"
                      : "bg-[var(--color-danger)]/10 text-[var(--color-danger)]"
                  }`}
                >
                  {tx.type}
                </span>
                <div>
                  <p className="font-[family-name:var(--font-mono)] text-sm font-medium text-[var(--color-text)]">
                    {tx.symbol}
                  </p>
                  <p className="text-xs text-[var(--color-text-faint)]">
                    {tx.quantity} shares @ ${tx.price.toFixed(2)}
                  </p>
                </div>
              </div>
              <div className="text-right">
                <p className="font-[family-name:var(--font-mono)] text-sm text-[var(--color-text-dim)]">
                  ${tx.totalValue.toFixed(2)}
                </p>
                <p className="text-xs text-[var(--color-text-faint)]">
                  {new Date(tx.timestamp).toLocaleDateString()}
                </p>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}