interface BrandPanelProps {
  eyebrow?: string;
  headline?: React.ReactNode;
  description?: string;

  // Keep backwards compatibility with the existing login page
  headlineLines?: [string, string];
  supportingText?: string;
}

export default function BrandPanel({
  eyebrow = "Trade smarter",
  headline,
  description,
  headlineLines = ["Trade smarter.", "Trade simpler."],
  supportingText = "Practice trading with real-time portfolio tracking, transaction history, and a balance that resets whenever you need a clean start.",
}: BrandPanelProps) {
  return (
    <div className="relative hidden md:flex md:w-1/2 flex-col justify-between overflow-hidden bg-[var(--color-surface)] px-14 py-12">
      <div
        className="animate-glow pointer-events-none absolute -left-32 top-1/3 h-[420px] w-[420px] rounded-full blur-[120px]"
        style={{ background: "var(--color-accent-glow)" }}
        aria-hidden="true"
      />

      <div className="relative z-10">
        <span className="font-[family-name:var(--font-display)] text-2xl font-semibold tracking-tight text-[var(--color-text)]">
          Trade<span className="text-[var(--color-accent)]">Sphere</span>
        </span>
      </div>

      <div className="relative z-10 flex flex-col gap-8">
        <div>
          <p className="mb-3 font-[family-name:var(--font-mono)] text-xs uppercase tracking-[0.18em] text-[var(--color-accent)]">
            {eyebrow}
          </p>

          <h1 className="font-[family-name:var(--font-display)] text-4xl font-semibold leading-tight tracking-tight text-[var(--color-text)] lg:text-5xl">
            {headline ?? (
              <>
                {headlineLines[0]}
                <br />
                {headlineLines[1]}
              </>
            )}
          </h1>

          <p className="mt-4 max-w-sm text-[15px] leading-relaxed text-[var(--color-text-dim)]">
            {description ?? supportingText}
          </p>
        </div>

        <div className="relative h-40 w-full max-w-md">
          <svg
            viewBox="0 0 400 140"
            fill="none"
            className="h-full w-full"
            aria-hidden="true"
          >
            <defs>
              <linearGradient
                id="pulse-grad"
                x1="0"
                y1="0"
                x2="1"
                y2="0"
              >
                <stop
                  offset="0%"
                  stopColor="var(--color-accent)"
                  stopOpacity="0.1"
                />
                <stop
                  offset="50%"
                  stopColor="var(--color-accent)"
                  stopOpacity="1"
                />
                <stop
                  offset="100%"
                  stopColor="var(--color-accent)"
                  stopOpacity="0.1"
                />
              </linearGradient>
            </defs>

            <path
              d="M0 100 L40 90 L70 105 L100 60 L130 80 L160 40 L190 65 L220 30 L250 55 L280 20 L310 45 L340 15 L400 35"
              stroke="url(#pulse-grad)"
              strokeWidth="2"
              className="animate-draw"
            />
          </svg>

          <div className="animate-float absolute -top-2 left-6 rounded-lg border border-[var(--color-border)] bg-[var(--color-surface-2)]/80 px-3 py-1.5 backdrop-blur-sm">
            <span className="font-[family-name:var(--font-mono)] text-xs text-[var(--color-accent)]">
              AAPL&nbsp;+2.4%
            </span>
          </div>

          <div
            className="animate-float absolute bottom-2 right-2 rounded-lg border border-[var(--color-border)] bg-[var(--color-surface-2)]/80 px-3 py-1.5 backdrop-blur-sm"
            style={{ animationDelay: "1.5s" }}
          >
            <span className="font-[family-name:var(--font-mono)] text-xs text-[var(--color-text-dim)]">
              TSLA&nbsp;-0.8%
            </span>
          </div>
        </div>
      </div>

      <p className="relative z-10 text-xs text-[var(--color-text-faint)]">
        Simulated markets. No real funds at risk.
      </p>
    </div>
  );
}