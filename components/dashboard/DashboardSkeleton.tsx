export default function DashboardSkeleton() {
  return (
    <main className="mx-auto max-w-6xl px-6 py-10">
      <div className="mb-8 h-8 w-64 animate-pulse rounded-lg bg-[var(--color-surface-2)]" />
      <div className="h-32 w-full animate-pulse rounded-2xl bg-[var(--color-surface-2)]" />
      <div className="mt-8 grid grid-cols-1 gap-6 lg:grid-cols-3">
        <div className="h-64 animate-pulse rounded-2xl bg-[var(--color-surface-2)] lg:col-span-2" />
        <div className="h-64 animate-pulse rounded-2xl bg-[var(--color-surface-2)]" />
      </div>
      <div className="mt-6 h-48 w-full animate-pulse rounded-2xl bg-[var(--color-surface-2)]" />
    </main>
  );
}